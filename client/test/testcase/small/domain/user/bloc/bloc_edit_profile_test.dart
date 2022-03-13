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
import 'package:sirloin_sandbox_client/domain/user/bloc/edit_profile/bloc_edit_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/edit_profile/state_edit_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';
import 'package:uuid/uuid.dart';

import '../../../../../lib/data/remote/http/randomiser.dart';
import '../../../../../lib/domain/user/randomiser.dart';
import '../../../../../lib/test_components.dart';
import '../../../../../mock/lib/domain/user/domain_user.mocks.dart';

void main() {
  late User defaultUser;
  late String uuid;
  late String nickname;
  late String profileImageUrl;
  final userRepo = MockUserRepository();

  setUp(() {
    uuid = const Uuid().v4().toString();
    defaultUser = randomUser(uuid: uuid);
    final faker = Faker();
    nickname = faker.person.name();
    profileImageUrl = faker.image.image(random: true);
  });

  void onEditProfileThen(dynamic Function(Invocation) answerMaker) {
    when(userRepo.updateProfile(
            uuid: anyNamed("uuid"), nickname: anyNamed("nickname"), profileImageUrl: anyNamed("profileImageUrl")))
        .thenAnswer((realInvocation) async => answerMaker(realInvocation));
  }

  void testBloc(
    final String description, {
    final void Function()? setUp,
    required final Args Function() args,
    required final void Function() expect,
    final void Function(EditProfileBloc)? verify,
  }) {
    blocTest<EditProfileBloc, EditProfileBlocState>(description,
        build: () {
          final bloc = EditProfileBloc(userRepo, newTestLogger());
          bloc.user = defaultUser;
          return bloc;
        },
        setUp: setUp ??
            () => onEditProfileThen((realInvocation) async {
                  final realUuid = realInvocation.namedArguments[const Symbol("uuid")];
                  final realNickname = realInvocation.namedArguments[const Symbol("nickname")];
                  final realProfileImageUrl = realInvocation.namedArguments[const Symbol("profileImageUrl")];
                  return User.create(
                      uuid: realUuid,
                      nickname: realNickname ?? defaultUser.nickname,
                      profileImageUrl: realProfileImageUrl ?? defaultUser.profileImageUrl);
                }),
        // user 를 set 하면 다른 state 가 나오기 때문에 skip 1 을 해줘야 함
        skip: 1,
        act: (sut) {
          final arguments = args();
          return sut.updateProfile(arguments.nickname, arguments.profileImageUrl);
        },
        verify: (editProfileBloc) {
          if (verify != null) {
            verify(editProfileBloc);
          }
        },
        expect: expect);
  }

  group("loadUser 실패로, 후속 처리가:", () {
    testBloc(
      "가능한 오류 발생시 ApiErrorState 에 오류를 포함해 전파한다",
      setUp: () {
        onEditProfileThen((realInvocation) async => throw randomUnexpectedResponseException());
      },
      args: () => Args(nickname: nickname, profileImageUrl: profileImageUrl),
      expect: () => [
        isA<ApiErrorState>().having(
            (state) => state.exception, "UnexpectedResponseException", const TypeMatcher<UnexpectedResponseException>())
      ],
    );

    testBloc(
      "불가능한 오류 발생시 empty ApiErrorState 를 전파한다",
      setUp: () {
        onEditProfileThen((realInvocation) async => throw UnimplementedError("Unrecoverable ERROR!"));
      },
      args: () => Args(nickname: nickname, profileImageUrl: profileImageUrl),
      expect: () => [isA<ApiErrorState>().having((state) => state.exception, "", isNull)],
    );
  });

  group("변경이 없는 필드는 업데이트 하지 않는다: ", () {
    testBloc("nickname 을 업데이트하면 안됨",
        args: () => Args(nickname: defaultUser.nickname, profileImageUrl: profileImageUrl),
        expect: () => [isA<UserDataSetState>().having((state) => state.isUpToDate, "", equals(true))],
        verify: (_) {
          verify(userRepo.updateProfile(uuid: defaultUser.uuid, nickname: null, profileImageUrl: profileImageUrl))
              .called(1);
        });

    testBloc("profileImageUrl 을 업데이트하면 안됨",
        args: () => Args(nickname: nickname, profileImageUrl: defaultUser.profileImageUrl),
        expect: () => [isA<UserDataSetState>().having((state) => state.isUpToDate, "", equals(true))],
        verify: (_) {
          verify(userRepo.updateProfile(uuid: defaultUser.uuid, nickname: nickname, profileImageUrl: null)).called(1);
        });
  });

  testBloc(
    "업데이트 성공시 UserDataSetState(isUpToDate = true) 를 전파한다",
    args: () => Args(nickname: nickname, profileImageUrl: profileImageUrl),
    expect: () => [
      isA<UserDataSetState>()
          .having((state) => state.isUpToDate, "", equals(true))
          .having((state) => state.user.nickname, "", equals(nickname))
          .having((state) => state.user.profileImageUrl, "", equals(profileImageUrl))
    ],
  );

  tearDown(() {
    reset(userRepo);
  });
}

class Args {
  final String nickname;
  final String profileImageUrl;

  Args({required final this.nickname, required final this.profileImageUrl});
}
