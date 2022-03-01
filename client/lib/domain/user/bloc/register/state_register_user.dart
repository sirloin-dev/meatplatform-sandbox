/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';
import 'package:sirloin_sandbox_client/domain/base_bloc_state.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';

abstract class RegisterUserBlocState extends BaseBlocState {}

@immutable
class InitialState extends BaseEmptyState implements RegisterUserBlocState {}

@immutable
class ApiErrorState extends BaseExceptionalState implements RegisterUserBlocState {
  const ApiErrorState(Exception? exception) : super(exception);

  const ApiErrorState.empty() : super(null);
}

@immutable
class UserRegisteredState extends Equatable implements RegisterUserBlocState {
  final User user;

  const UserRegisteredState(this.user);

  @override
  List<Object?> get props => [user];

  @override
  bool get stringify => true;
}

typedef StateToObject<T, U extends RegisterUserBlocState> = T Function(U);

// Dart 에 Sealed class 가 없어서 pattern matching 을 직접 손으로 구현...
extension RegisterUserBlocStatePatternMatcher on RegisterUserBlocState {
  T when<T>({
    required final StateToObject<T, InitialState> initialState,
    required final StateToObject<T, ApiErrorState> apiErrorState,
    required final StateToObject<T, UserRegisteredState> userRegisteredState,
  }) {
    // Dart 에 Smart cast 가 없어서 불편...
    if (this is InitialState) {
      return initialState(this as InitialState);
    } else if (this is ApiErrorState) {
      return apiErrorState(this as ApiErrorState);
    } else if (this is UserRegisteredState) {
      return userRegisteredState(this as UserRegisteredState);
    }

    throw UnimplementedError("Pattern matching rule for '$this extends RegistrationScreenState' "
        "is not implemented");
  }
}
