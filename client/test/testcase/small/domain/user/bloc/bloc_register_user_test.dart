// test 만 따로 package 형태로 import 할 방법이 없음
// ignore_for_file: avoid_relative_lib_imports
/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:bloc_test/bloc_test.dart';
import 'package:faker/faker.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_exceptions.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/register/bloc_register_user.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/register/state_register_user.dart';

import '../../../../../lib/data/remote/http/randomiser.dart';
import '../../../../../lib/domain/user/randomiser.dart';
import '../../../../../lib/test_components.dart';
import '../../../../../mock/lib/domain/user/domain_user.mocks.dart';

void main() {
  late String nickname;
  late String profileImageUrl;
  final userRepo = MockUserRepository();

  setUp(() {
    final faker = Faker();
    nickname = faker.person.name();
    profileImageUrl = faker.image.image();
  });

  void testBloc(
    final String description, {
    required final void Function() setUp,
    required final void Function() expect,
  }) {
    blocTest<RegisterUserBloc, RegisterUserBlocState>(description,
        build: () => RegisterUserBloc(userRepo, newTestLogger()),
        setUp: setUp,
        act: (sut) => sut.registerUser(nickname, profileImageUrl),
        expect: expect);
  }

  void onRegisterThen(dynamic Function(Invocation) answerMaker) {
    when(userRepo.register(nickname: anyNamed("nickname"), profileImageUrl: anyNamed("profileImageUrl")))
        .thenAnswer((realInvocation) async => answerMaker(realInvocation));
  }

  group("register 실패로, 후속 처리가:", () {
    testBloc(
      "가능한 오류 발생시 ApiErrorState 에 오류를 포함해 전파한다",
      setUp: () {
        onRegisterThen((realInvocation) async => throw randomUnexpectedResponseException());
      },
      expect: () => [
        isA<ApiErrorState>().having(
            (state) => state.exception, "UnexpectedResponseException", const TypeMatcher<UnexpectedResponseException>())
      ],
    );

    testBloc(
      "불가능한 오류 발생시 empty ApiErrorState 를 전파한다",
      setUp: () {
        onRegisterThen((realInvocation) async => throw UnimplementedError("Unrecoverable ERROR!"));
      },
      expect: () => [isA<ApiErrorState>().having((state) => state.exception, "", isNull)],
    );
  });

  testBloc(
    "register 성공시 UserRegisteredState 를 전파한다",
    setUp: () {
      onRegisterThen((realInvocation) async {
        final realNickname = realInvocation.namedArguments[const Symbol("nickname")];
        final realProfileImageUrl = realInvocation.namedArguments[const Symbol("profileImageUrl")];
        return randomUser(nickname: realNickname, profileImageUrl: realProfileImageUrl);
      });
    },
    expect: () => [
      isA<UserRegisteredState>()
          .having((state) => state.user.nickname, "nickname", nickname)
          .having((state) => state.user.profileImageUrl, "profileImageUrl", profileImageUrl)
    ],
  );

  tearDown(() {
    reset(userRepo);
  });
}
