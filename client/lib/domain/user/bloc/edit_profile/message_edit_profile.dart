/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';

abstract class EditProfileBlocMessage {}

@immutable
class UserDataSetMessage extends Equatable implements EditProfileBlocMessage {
  final User user;

  const UserDataSetMessage(this.user);

  @override
  List<Object?> get props => [user];

  @override
  bool get stringify => true;
}

@immutable
class ProceedEditProfileMessage extends Equatable implements EditProfileBlocMessage {
  final String nickname;
  final String profileImageUrl;

  const ProceedEditProfileMessage(this.nickname, this.profileImageUrl);

  @override
  List<Object?> get props => [nickname, profileImageUrl];

  @override
  bool get stringify => true;
}
