/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:logger/logger.dart';
import 'package:sirloin_sandbox_client/di/di_modules.dart';
import 'package:sirloin_sandbox_client/domain/user/repository/repo_user.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';
import 'package:sirloin_sandbox_client/screen/message_screen_splash.dart';
import 'package:sirloin_sandbox_client/screen/state_screen_splash.dart';

/*
 * POINT: 이 Bloc 은 Splash screen 의 비즈니스 뿐 아니라 애플리케이션 초기화라는
 * 두가지 비즈니스를 한번에 하고 있습니다. 문제점을 어떻게 개선할 수 있을까요?
 */
class SplashScreenBloc extends Bloc<SplashScreenMessage, SplashScreenState> {
  SplashScreenBloc() : super(InitiatedState()) {
    on<ScreenReadyMessage>(_onScreenReadyMessage);
    on<InitProgrammeMessage>(_onInitProgrammeMessage);
  }

  void _onScreenReadyMessage(final ScreenReadyMessage message, final Emitter<SplashScreenState> emit) {
    emit(LoadingState());
  }

  // POINT: DI 모듈 초기화처럼 중요한 동작을 특정 UI 의 비즈니스에 종속시켜도 괜찮을까요?
  Future<void> _onInitProgrammeMessage(
      final InitProgrammeMessage message, final Emitter<SplashScreenState> emit) async {
    // Step 1: DI module 초기화
    await ClientApplicationDi.instance.init();
    final Logger logger = ClientApplicationDi.instance.singleton(Logger);

    // Step 2: Load Cached User
    final UserRepository userRepository = ClientApplicationDi.instance.singleton(UserRepository);

    final maybeUser = await userRepository.findSavedSelf();
    if (maybeUser == null) {
      logger.d("No locally cached user is found");
      emit(SavedUserNotFoundState());
      return;
    }

    // Step 3: Verify Cached User
    final User maybeVerifiedUser;
    try {
      maybeVerifiedUser = await userRepository.getUser(uuid: maybeUser.uuid, forceRefresh: true);
    } catch (e) {
      logger.d("User verification via API has failed", e);
      await userRepository.deleteSavedUser(maybeUser.uuid);
      emit(SavedUserNotFoundState());
      return;
    }

    logger.d("Locally cached user is found and verified");
    emit(SavedUserFoundState(maybeVerifiedUser));
  }
}
