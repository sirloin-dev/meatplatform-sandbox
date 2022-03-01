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
import 'package:sirloin_sandbox_client/domain/user/user.dart';
import 'package:uuid/uuid.dart';

import '../../../../../lib/data/local/user/randomiser.dart';
import '../../../../../lib/data/remote/http/user/dto/randomiser_response_user.dart';
import '../../../../../lib/domain/user/randomiser.dart';
import '../../../../../mock/lib/data/local/user/localstorage_user.mocks.dart';
import '../../../../../mock/lib/data/remote/http/user/api_user.mocks.dart';
import 'repo_user_impl_test.dart';

void getUserTestCases(
    final SutSupplier supplier, final MockUserLocalStorage userLocalStorage, final MockUserApi userApi) {
  late UserRepositoryImpl sut;
  late String uuid;
  late User expectedUser;

  setUp(() {
    sut = supplier.call();
    uuid = const Uuid().v4().toString();

    final expectedUserResponse = randomUserResponse(uuid: uuid);
    expectedUser = expectedUserResponse.toUser();

    when(userApi.getUser(any)).thenAnswer((_) async => expectedUserResponse);
    when(userLocalStorage.save(any))
        .thenAnswer((it) async => mockSerialisedUser(user: it.positionalArguments[0]).asSerialisedData());
  });

  group("forceRefresh 모드일 때:", () {
    test("api call 결과를 저장 후 반환한다", () async {
      // then:
      final actual = await sut.getUser(uuid: uuid, forceRefresh: true);

      // expect:
      expect(actual, equals(expectedUser));
      verify(userLocalStorage.save(expectedUser)).called(1);
    });

    test("저장에 실패하더라도 실행 결과에 문제가 없다", () async {
      // when:
      when(userLocalStorage.save(any)).thenAnswer((it) async => throw UnimplementedError("ERROR!!"));

      // then:
      final actual = await sut.getUser(uuid: uuid, forceRefresh: true);

      // expect:
      expect(actual, equals(expectedUser));
    });
  });

  group("forceRefresh 모드가 아닐 때, local cache 의 user 정보가:", () {
    setUp(() {
      expectedUser = randomUser(uuid: uuid);
    });

    test("만료되지 않았다면 cache 데이터를 반환한다", () async {
      // given:
      final mockSavedUser = mockSerialisedUser(user: expectedUser);

      // when:
      when(userLocalStorage.find(any)).thenAnswer((it) async => mockSavedUser.asSerialisedData());

      // then:
      final actual = await sut.getUser(uuid: uuid, forceRefresh: false);

      // expect:
      expect(actual, equals(expectedUser));
    });

    test("만료되었다면 api call 결과를 반환한다", () async {
      // given:
      final mockSavedUser =
          mockSerialisedUser(user: expectedUser).savedAt(DateTime.now().subtract(const Duration(days: 365)));

      // when:
      when(userLocalStorage.find(any)).thenAnswer((it) async => mockSavedUser.asSerialisedData());

      // then:
      await sut.getUser(uuid: uuid, forceRefresh: false);

      // expect:
      verify(userApi.getUser(uuid)).called(1);
    });
  });
}
