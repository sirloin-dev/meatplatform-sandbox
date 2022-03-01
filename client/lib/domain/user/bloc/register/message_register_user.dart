/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

abstract class RegisterUserBlocMessage {}

@immutable
class ProceedRegistrationMessage extends Equatable implements RegisterUserBlocMessage {
  final String nickname;
  final String profileImageUrl;

  const ProceedRegistrationMessage(this.nickname, this.profileImageUrl);

  @override
  List<Object?> get props => [nickname, profileImageUrl];

  @override
  bool get stringify => true;
}
