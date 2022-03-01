/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:meta/meta.dart';
import 'package:sirloin_sandbox_client/domain/base_bloc_state.dart';

abstract class DeregisterUserBlocState extends BaseBlocState {}

@immutable
class InitialState extends BaseEmptyState implements DeregisterUserBlocState {}

@immutable
class ApiErrorState extends BaseExceptionalState implements DeregisterUserBlocState {
  const ApiErrorState(Exception? exception) : super(exception);

  const ApiErrorState.empty() : super(null);
}

@immutable
class UserDeregisteredState implements DeregisterUserBlocState {}

typedef StateToObject<T, U extends DeregisterUserBlocState> = T Function(U);

// Dart 에 Sealed class 가 없어서 pattern matching 을 직접 손으로 구현...
extension DeregisterUserBlocStatePatternMatcher on DeregisterUserBlocState {
  T when<T>(
      {required final StateToObject<T, InitialState> initialState,
      required final StateToObject<T, UserDeregisteredState> userDeregisteredState,
      required final StateToObject<T, ApiErrorState> apiErrorState}) {
    // Dart 에 Smart cast 가 없어서 불편...
    if (this is InitialState) {
      return initialState(this as InitialState);
    } else if (this is UserDeregisteredState) {
      return userDeregisteredState(this as UserDeregisteredState);
    } else if (this is ApiErrorState) {
      return apiErrorState(this as ApiErrorState);
    }

    throw UnimplementedError("Pattern matching rule for '$this extends ProfileScreenState' "
        "is not implemented");
  }
}
