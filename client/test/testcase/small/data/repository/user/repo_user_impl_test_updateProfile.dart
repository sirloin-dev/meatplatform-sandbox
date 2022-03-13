// test target 의 method name 표현을 위한 naming rule 위반
// ignore_for_file: file_names
// test 만 따로 package 형태로 import 할 방법이 없음
// ignore_for_file: avoid_relative_lib_imports
/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:faker/faker.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:sirloin_sandbox_client/data/local/user/localstorage_user.dart';
import 'package:sirloin_sandbox_client/data/remote/http/user/dto/response_user.dart';
import 'package:sirloin_sandbox_client/data/repository/user/repo_user_impl.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';
import 'package:uuid/uuid.dart';

import '../../../../../mock/lib/data/local/user/localstorage_user.mocks.dart';
import '../../../../../mock/lib/data/remote/http/user/api_user.mocks.dart';
import 'repo_user_impl_test.dart';

void updateProfileTestCases(
    final SutSupplier supplier, final MockUserLocalStorage userLocalStorage, final MockUserApi userApi) {
  late String uuid;
  late String nickname;
  late String profileImageUrl;
  late UserRepositoryImpl sut;

  setUp(() {
    sut = supplier.call();

    final faker = Faker();
    uuid = const Uuid().v4().toString();
    nickname = faker.person.name();
    profileImageUrl = faker.image.image();

    when(userApi.updateUser(any, nickname: anyNamed("nickname"), profileImageUrl: anyNamed("profileImageUrl")))
        .thenAnswer((realInvocation) async {
      final realUuid = realInvocation.positionalArguments[0] as String;
      final realNickname = realInvocation.namedArguments[const Symbol("nickname")];
      final realProfileImageUrl = realInvocation.namedArguments[const Symbol("profileImageUrl")];

      return UserResponse(uuid: realUuid, nickname: realNickname, profileImageUrl: realProfileImageUrl);
    });
  });

  test("api call 결과를 반환한다", () async {
    // when:
    when(userLocalStorage.save(any)).thenAnswer((realInvocation) async {
      final realUser = realInvocation.positionalArguments[0] as User;
      return SerialisedUser.from(realUser);
    });

    // then:
    final actual = await sut.updateProfile(uuid: uuid, nickname: nickname, profileImageUrl: profileImageUrl);

    // expect:
    verify(userLocalStorage.save(any)).called(1);

    // and:
    expect(actual.nickname, equals(nickname));
    expect(actual.profileImageUrl, equals(profileImageUrl));
  });

  test("local cache 저장에 실패하더라도 실행 결과에 문제가 없다", () async {
    // when:
    when(userLocalStorage.save(any))
        .thenAnswer((realInvocation) async => throw UnimplementedError("Save user ERROR!!"));

    // then:
    final actual = await sut.updateProfile(uuid: uuid, nickname: nickname, profileImageUrl: profileImageUrl);

    // expect:
    expect(actual.nickname, equals(nickname));
    expect(actual.profileImageUrl, equals(profileImageUrl));
  });
}
