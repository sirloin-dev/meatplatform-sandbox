/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';

abstract class GetProfileBlocMessage {}

@immutable
class UserDataSetMessage extends Equatable implements GetProfileBlocMessage {
  final User user;

  const UserDataSetMessage(this.user);

  @override
  List<Object?> get props => [user];

  @override
  bool get stringify => true;
}

@immutable
class ErrorMessage extends Equatable implements GetProfileBlocMessage {
  final Exception? exception;

  const ErrorMessage(this.exception);

  @override
  List<Object?> get props => [exception];

  @override
  bool get stringify => true;
}
