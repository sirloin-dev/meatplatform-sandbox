/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';
import 'package:sirloin_sandbox_client/domain/base_bloc_state.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';

abstract class EditProfileBlocState extends BaseBlocState {}

@immutable
class InitialState extends BaseEmptyState implements EditProfileBlocState {}

@immutable
class ApiErrorState extends BaseExceptionalState implements EditProfileBlocState {
  const ApiErrorState(Exception? exception) : super(exception);

  const ApiErrorState.empty() : super(null);
}

@immutable
class UserDataSetState extends Equatable implements EditProfileBlocState {
  final User user;
  final bool isUpToDate;

  const UserDataSetState(this.user, this.isUpToDate);

  @override
  List<Object?> get props => [user, isUpToDate];

  @override
  bool get stringify => true;
}

typedef StateToObject<T, U extends EditProfileBlocState> = T Function(U);

// Dart 에 Sealed class 가 없어서 pattern matching 을 직접 손으로 구현...
extension EditProfileBlocStatePatternMatcher on EditProfileBlocState {
  T when<T>({
    required final StateToObject<T, InitialState> initialState,
    required final StateToObject<T, ApiErrorState> apiErrorState,
    required final StateToObject<T, UserDataSetState> userDataSetState,
  }) {
    // Dart 에 Smart cast 가 없어서 불편...
    if (this is InitialState) {
      return initialState(this as InitialState);
    } else if (this is ApiErrorState) {
      return apiErrorState(this as ApiErrorState);
    } else if (this is UserDataSetState) {
      return userDataSetState(this as UserDataSetState);
    }

    throw UnimplementedError("Pattern matching rule for '$this extends EditProfileScreenState' "
        "is not implemented");
  }
}
