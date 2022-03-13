/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:logger/logger.dart';
import 'package:sirloin_sandbox_client/domain/user/repository/repo_user.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/edit_profile/message_edit_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/edit_profile/state_edit_profile.dart';

class EditProfileBloc extends Bloc<EditProfileBlocMessage, EditProfileBlocState> {
  late User _user;
  final UserRepository _userRepo;
  final Logger _logger;

  EditProfileBloc(final this._userRepo, final this._logger) : super(InitialState()) {
    on<UserDataSetMessage>(_onUserDataSetMessage);
    on<ProceedEditProfileMessage>(_onProceedEditProfileMessage);
  }

  User get user => _user;

  set user(final User user) {
    _user = user;
    add(UserDataSetMessage(user));
  }

  // POINT: 이 구현에는 어떤 문제가 있을까요?
  void updateProfile(final String nickname, final String profileImageUrl) {
    add(ProceedEditProfileMessage(nickname, profileImageUrl));
  }

  Future<void> _onUserDataSetMessage(final UserDataSetMessage message, final Emitter<EditProfileBlocState> emit) async {
    emit(UserDataSetState(message.user, false));
  }

  Future<void> _onProceedEditProfileMessage(
      final ProceedEditProfileMessage message, final Emitter<EditProfileBlocState> emit) async {
    final nickname = message.nickname != _user.nickname ? message.nickname : null;
    final profileImageUrl = message.profileImageUrl != _user.profileImageUrl ? message.profileImageUrl : null;

    final User updatedUser;
    try {
      updatedUser =
          await _userRepo.updateProfile(uuid: _user.uuid, nickname: nickname, profileImageUrl: profileImageUrl);
    } catch (e) {
      _logger.d("User verification via API has failed", e);
      if (e is Exception) {
        emit(ApiErrorState(e));
      } else {
        emit(const ApiErrorState.empty());
      }
      return;
    }

    emit(UserDataSetState(updatedUser, true));
  }
}
