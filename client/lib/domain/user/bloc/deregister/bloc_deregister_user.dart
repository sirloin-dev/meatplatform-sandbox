/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:logger/logger.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/deregister/message_deregister_user.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/deregister/state_deregister_user.dart';
import 'package:sirloin_sandbox_client/domain/user/repository/repo_user.dart';

class DeregisterUserBloc extends Bloc<DeregisterUserBlocMessage, DeregisterUserBlocState> {
  final UserRepository _userRepo;
  final Logger _logger;

  DeregisterUserBloc(final this._userRepo, final this._logger) : super(InitialState()) {
    on<ProceedDeregisterMessage>(_onProceedDeregisterMessage);
  }

  Future<void> deregister(final String userId) async {
    add(ProceedDeregisterMessage(userId));
  }

  Future<void> _onProceedDeregisterMessage(
      final ProceedDeregisterMessage message, final Emitter<DeregisterUserBlocState> emit) async {
    try {
      await _userRepo.deregister(message.uuid);
    } catch (e) {
      _logger.d("User deregistration via API has failed", e);
      if (e is Exception) {
        emit(ApiErrorState(e));
      } else {
        emit(const ApiErrorState.empty());
      }
      return;
    }

    emit(UserDeregisteredState());
  }
}
