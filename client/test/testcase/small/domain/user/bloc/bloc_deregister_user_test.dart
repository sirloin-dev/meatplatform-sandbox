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
import 'package:sirloin_sandbox_client/domain/user/bloc/deregister/bloc_deregister_user.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/deregister/state_deregister_user.dart';
import 'package:uuid/uuid.dart';

import '../../../../../lib/data/remote/http/randomiser.dart';
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
    blocTest<DeregisterUserBloc, DeregisterUserBlocState>(description,
        build: () => DeregisterUserBloc(userRepo, newTestLogger()),
        setUp: setUp,
        act: (sut) => sut.deregister(uuid),
        expect: expect);
  }

  void onDeregisterThen(dynamic Function(Invocation) answerMaker) {
    when(userRepo.deregister(any)).thenAnswer((realInvocation) async => answerMaker(realInvocation));
  }

  group("deregister 실패로, 후속 처리가:", () {
    testBloc(
      "가능한 오류 발생시 ApiErrorState 에 오류를 포함해 전파한다",
      setUp: () {
        onDeregisterThen((realInvocation) async => throw randomUnexpectedResponseException());
      },
      expect: () => [
        isA<ApiErrorState>().having(
            (state) => state.exception, "UnexpectedResponseException", const TypeMatcher<UnexpectedResponseException>())
      ],
    );

    testBloc(
      "불가능한 오류 발생시 empty ApiErrorState 를 전파한다",
      setUp: () {
        onDeregisterThen((realInvocation) async => throw UnimplementedError("Unrecoverable ERROR!"));
      },
      expect: () => [isA<ApiErrorState>().having((state) => state.exception, "", isNull)],
    );
  });

  testBloc(
    "register 성공시 UserDeregisteredState 를 전파한다",
    setUp: () {
      onDeregisterThen((realInvocation) => {});
    },
    expect: () => [isA<UserDeregisteredState>()],
  );

  tearDown(() {
    reset(userRepo);
  });
}
