/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_exceptions.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';

@immutable
class UserResponse extends Equatable {
  final String uuid;
  final String nickname;
  final String profileImageUrl;

  @visibleForTesting
  static const keyUuid = "uuid";
  @visibleForTesting
  static const keyNickname = "nickname";
  @visibleForTesting
  static const keyProfileImageUrl = "profileImageUrl";

  const UserResponse({
    required this.uuid,
    required this.nickname,
    required this.profileImageUrl,
  });

  User toUser() => User.create(uuid: uuid, nickname: nickname, profileImageUrl: profileImageUrl);

  @override
  List<Object?> get props => [uuid, nickname, profileImageUrl];

  @override
  bool get stringify => true;

  static UserResponse fromJson(final Map<String, dynamic> jsonMap) {
    final uuid = jsonMap[keyUuid];
    final nickname = jsonMap[keyNickname];
    final profileImageUrl = jsonMap[keyProfileImageUrl];

    if (uuid == null || nickname == null || profileImageUrl == null) {
      throw JsonParseException();
    }

    return UserResponse(uuid: uuid, nickname: nickname, profileImageUrl: profileImageUrl);
  }
}
