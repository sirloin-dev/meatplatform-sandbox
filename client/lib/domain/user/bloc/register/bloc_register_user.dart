/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:logger/logger.dart';
import 'package:sirloin_sandbox_client/domain/user/repository/repo_user.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/register/message_register_user.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/register/state_register_user.dart';

class RegisterUserBloc extends Bloc<RegisterUserBlocMessage, RegisterUserBlocState> {
  final UserRepository _userRepo;
  final Logger _logger;

  RegisterUserBloc(final this._userRepo, final this._logger) : super(InitialState()) {
    on<ProceedRegistrationMessage>(_onProceedRegistrationMessage);
  }

  void registerUser(final String nickname, final String profileImageUrl) {
    add(ProceedRegistrationMessage(nickname, profileImageUrl));
  }

  Future<void> _onProceedRegistrationMessage(
      final ProceedRegistrationMessage message, final Emitter<RegisterUserBlocState> emit) async {
    final nickname = message.nickname;
    final profileImageUrl = message.profileImageUrl;

    final User registeredUser;
    try {
      registeredUser = await _userRepo.register(nickname: nickname, profileImageUrl: profileImageUrl);
    } catch (e) {
      _logger.d("User registration via API has failed", e);
      if (e is Exception) {
        emit(ApiErrorState(e));
      } else {
        emit(const ApiErrorState.empty());
      }
      return;
    }

    emit(UserRegisteredState(registeredUser));
  }
}
