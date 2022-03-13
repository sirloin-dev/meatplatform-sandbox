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

import '../../../../../lib/data/local/user/randomiser.dart';
import '../../../../../lib/domain/user/randomiser.dart';
import '../../../../../mock/lib/data/local/user/localstorage_user.mocks.dart';
import '../../../../../mock/lib/data/remote/http/user/api_user.mocks.dart';
import 'repo_user_impl_test.dart';

void findSavedSelfTestCases(
    final SutSupplier supplier, final MockUserLocalStorage userLocalStorage, final MockUserApi userApi) {
  late UserRepositoryImpl sut;
  late String maybeMyUuid;

  setUp(() {
    sut = supplier.call();
    maybeMyUuid = const Uuid().v4().toString();
  });

  group("local cache 검색 실패로 null 을 반환한다:", () {
    test("uuid 정보 없음", () async {
      // when:
      when(userLocalStorage.findMyUuid()).thenAnswer((_) async => null);

      // then:
      final maybeSavedSelf = await sut.findSavedSelf();

      // expect:
      expect(maybeSavedSelf, equals(null));
    });

    test("uuid 는 있지만, 해당하는 user 가 없음", () async {
      // when:
      when(userLocalStorage.findMyUuid()).thenAnswer((_) async => maybeMyUuid);
      when(userLocalStorage.find(any)).thenAnswer((_) async => null);

      // then:
      final maybeSavedSelf = await sut.findSavedSelf();

      // expect:
      expect(maybeSavedSelf, equals(null));
    });
  });

  test("uuid 에 해당하는 user 정보가 모두 local cache 에 존재", () async {
    // given:
    final user = randomUser(uuid: maybeMyUuid);
    final savedUser = mockSerialisedUser(user: user).asSerialisedData();

    // when:
    when(userLocalStorage.findMyUuid()).thenAnswer((_) async => user.uuid);
    when(userLocalStorage.find(any)).thenAnswer((_) async => savedUser);

    // then:
    final maybeSavedSelf = await sut.findSavedSelf();

    // expect:
    expect(maybeSavedSelf, equals(user));
  });
}
