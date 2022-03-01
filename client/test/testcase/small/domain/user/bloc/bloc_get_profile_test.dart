// test 만 따로 package 형태로 import 할 방법이 없음
// ignore_for_file: avoid_relative_lib_imports
/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:bloc_test/bloc_test.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_exceptions.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/get_profile/bloc_get_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/get_profile/state_get_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';
import 'package:uuid/uuid.dart';

import '../../../../../lib/data/remote/http/randomiser.dart';
import '../../../../../lib/domain/user/randomiser.dart';
import '../../../../../lib/test_components.dart';
import '../../../../../mock/lib/domain/user/domain_user.mocks.dart';

void main() {
  late String uuid;
  final userRepo = MockUserRepository();

  setUp(() {
    uuid = const Uuid().v4().toString();
  });

  void testBloc(
    final String description, {
    required final void Function() setUp,
    required final void Function() expect,
  }) {
    blocTest<GetProfileBloc, GetProfileBlocState>(description,
        build: () => GetProfileBloc(userRepo, newTestLogger()),
        setUp: setUp,
        act: (sut) => sut.loadUser(uuid),
        expect: expect);
  }

  void onGetProfileThen(dynamic Function(Invocation) answerMaker) {
    when(userRepo.getUser(uuid: anyNamed("uuid"), forceRefresh: anyNamed("forceRefresh")))
        .thenAnswer((realInvocation) async => answerMaker(realInvocation));
  }

  group("loadUser 실패로, 후속 처리가:", () {
    testBloc(
      "가능한 오류 발생시 ApiErrorState 에 오류를 포함해 전파한다",
      setUp: () {
        onGetProfileThen((realInvocation) async => throw randomUnexpectedResponseException());
      },
      expect: () => [
        isA<ApiErrorState>().having(
            (state) => state.exception, "UnexpectedResponseException", const TypeMatcher<UnexpectedResponseException>())
      ],
    );

    testBloc(
      "불가능한 오류 발생시 empty ApiErrorState 를 전파한다",
      setUp: () {
        onGetProfileThen((realInvocation) async => throw UnimplementedError("Unrecoverable ERROR!"));
      },
      expect: () => [isA<ApiErrorState>().having((state) => state.exception, "", isNull)],
    );
  });

  group("loadUser 성공시: ", () {
    late User expectedUser;

    setUp(() {
      expectedUser = randomUser(uuid: uuid);
    });

    testBloc(
      "UserDataSetState 를 전파한다",
      setUp: () {
        onGetProfileThen((realInvocation) async => expectedUser);
      },
      expect: () => [isA<UserDataSetState>().having((state) => state.user, "user", expectedUser)],
    );
  });

  tearDown(() {
    reset(userRepo);
  });
}
