/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';
import 'package:sirloin_sandbox_client/domain/base_bloc_state.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';

abstract class GetProfileBlocState extends BaseBlocState {}

@immutable
class InitialState extends BaseEmptyState implements GetProfileBlocState {}

@immutable
class ApiErrorState extends BaseExceptionalState implements GetProfileBlocState {
  const ApiErrorState(Exception? exception) : super(exception);

  const ApiErrorState.empty() : super(null);
}

@immutable
class UserDataSetState extends Equatable implements GetProfileBlocState {
  final User user;

  const UserDataSetState(this.user);

  @override
  List<Object?> get props => [user];

  @override
  bool get stringify => true;
}

typedef StateToObject<T, U extends GetProfileBlocState> = T Function(U);

// Dart 에 Sealed class 가 없어서 pattern matching 을 직접 손으로 구현...
extension GetProfileBlocStatePatternMatcher on GetProfileBlocState {
  T when<T>(
      {required final StateToObject<T, InitialState> initialState,
      required final StateToObject<T, UserDataSetState> userDataSetState,
      required final StateToObject<T, ApiErrorState> apiErrorState}) {
    // Dart 에 Smart cast 가 없어서 불편...
    if (this is InitialState) {
      return initialState(this as InitialState);
    } else if (this is UserDataSetState) {
      return userDataSetState(this as UserDataSetState);
    } else if (this is ApiErrorState) {
      return apiErrorState(this as ApiErrorState);
    }

    throw UnimplementedError("Pattern matching rule for '$this extends ProfileScreenState' "
        "is not implemented");
  }
}
