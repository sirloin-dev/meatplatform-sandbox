/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:logger/logger.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/get_profile/message_get_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/get_profile/state_get_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/repository/repo_user.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';

class GetProfileBloc extends Bloc<GetProfileBlocMessage, GetProfileBlocState> {
  late User _user;
  final UserRepository _userRepo;
  final Logger _logger;

  GetProfileBloc(final this._userRepo, final this._logger) : super(InitialState()) {
    on<UserDataSetMessage>(_onUserDataSetMessage);
    on<ErrorMessage>(_onErrorMessage);
  }

  User get user => _user;

  set user(final User user) {
    _user = user;
    add(UserDataSetMessage(user));
  }

  Future<void> loadUser(final String uuid, [final bool forceRefresh = false]) async {
    late User updatedUser;
    try {
      updatedUser = await _userRepo.getUser(uuid: uuid, forceRefresh: forceRefresh);
    } catch (e) {
      _logger.d("User retrieval has failed", e);
      if (e is Exception) {
        add(ErrorMessage(e));
      } else {
        add(const ErrorMessage(null));
      }
      return;
    }

    user = updatedUser;
  }

  Future<void> _onErrorMessage(final ErrorMessage message, final Emitter<GetProfileBlocState> emit) async {
    emit(ApiErrorState(message.exception));
  }

  Future<void> _onUserDataSetMessage(final UserDataSetMessage message, final Emitter<GetProfileBlocState> emit) async {
    emit(UserDataSetState(message.user));
  }
}
