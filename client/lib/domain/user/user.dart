/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:flutter/cupertino.dart';

abstract class User {
  final String uuid;
  final String nickname;
  final String profileImageUrl;

  User({
    required this.uuid,
    required this.nickname,
    required this.profileImageUrl,
  });

  static User create({
    required final String uuid,
    required final String nickname,
    required final String profileImageUrl,
  }) {
    return _UserImpl(uuid: uuid, nickname: nickname, profileImageUrl: profileImageUrl);
  }
}

@immutable
class _UserImpl extends Equatable implements User {
  @override
  final String uuid;
  @override
  final String nickname;
  @override
  final String profileImageUrl;

  const _UserImpl({
    required this.uuid,
    required this.nickname,
    required this.profileImageUrl,
  }) : super();

  @override
  List<Object?> get props => [uuid, nickname, profileImageUrl];

  @override
  bool get stringify => true;
}
