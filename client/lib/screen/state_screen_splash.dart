/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:sirloin_sandbox_client/domain/user/user.dart';

abstract class SplashScreenState {}

class InitiatedState implements SplashScreenState {}

class LoadingState implements SplashScreenState {}

class SavedUserNotFoundState implements SplashScreenState {}

class SavedUserFoundState implements SplashScreenState {
  final User user;

  SavedUserFoundState(this.user);
}

typedef StateToObject<T, U extends SplashScreenState> = T Function(U);

// Dart 에 Sealed class 가 없어서 pattern matching 을 직접 손으로 구현...
extension SplashScreenStatePatternMatcher on SplashScreenState {
  T when<T>({
    required final StateToObject<T, InitiatedState> initiatedState,
    required final StateToObject<T, LoadingState> loadingState,
    required final StateToObject<T, SavedUserNotFoundState> savedUserNotFoundState,
    required final StateToObject<T, SavedUserFoundState> savedUserFoundState,
  }) {
    // Dart 에 Smart cast 가 없어서 불편...
    if (this is InitiatedState) {
      return initiatedState(this as InitiatedState);
    } else if (this is LoadingState) {
      return loadingState(this as LoadingState);
    } else if (this is SavedUserNotFoundState) {
      return savedUserNotFoundState(this as SavedUserNotFoundState);
    } else if (this is SavedUserFoundState) {
      return savedUserFoundState(this as SavedUserFoundState);
    }

    throw UnimplementedError("Pattern matching rule for '$this extends SplashScreenState' "
        "is not implemented");
  }
}
