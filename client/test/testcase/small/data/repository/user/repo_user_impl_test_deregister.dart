// test target 의 method name 표현을 위한 naming rule 위반
// ignore_for_file: file_names
// test 만 따로 package 형태로 import 할 방법이 없음
// ignore_for_file: avoid_relative_lib_imports
/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:sirloin_sandbox_client/data/repository/user/repo_user_impl.dart';
import 'package:uuid/uuid.dart';

import '../../../../../mock/lib/data/local/user/localstorage_user.mocks.dart';
import '../../../../../mock/lib/data/remote/http/user/api_user.mocks.dart';
import 'repo_user_impl_test.dart';

void deregisterTestCases(
    final SutSupplier supplier, final MockUserLocalStorage userLocalStorage, final MockUserApi userApi) {
  late String uuid;
  late UserRepositoryImpl sut;

  setUp(() {
    sut = supplier.call();

    uuid = const Uuid().v4().toString();
  });

  test("탈퇴 API 호출에 성공하면, local data 도 삭제한다", () async {
    // when:
    when(userApi.deleteUser(any)).thenAnswer((realInvocation) async {
      final realUuid = realInvocation.positionalArguments[0] as String;
      return realUuid;
    });
    when(userLocalStorage.delete(any)).thenAnswer((realInvocation) async => true);
    when(userLocalStorage.saveMyUuid(any)).thenAnswer((realInvocation) async => null);

    // then:
    await sut.deregister(uuid);

    // expect:
    verify(userLocalStorage.delete(uuid)).called(1);
    verify(userLocalStorage.saveMyUuid(null)).called(1);
  });

  test("탈퇴 API 호출에 실패하면, local data 를 삭제하면 안된다", () async {
    // when:
    when(userApi.deleteUser(any))
        .thenAnswer((realInvocation) async => throw UnimplementedError("deleteUser API ERROR!!"));

    // expect:
    expect(() async => await sut.deregister(uuid), throwsA(const TypeMatcher<Error>()));

    // expect:
    verifyNever(userLocalStorage.delete(uuid));
    verifyNever(userLocalStorage.saveMyUuid(null));
  });
}
