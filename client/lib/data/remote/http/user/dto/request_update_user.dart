/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'dart:convert';

import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/json_serializable.dart';

@immutable
class UpdateUserRequest extends Equatable implements JsonSerializable {
  final String? nickname;
  final String? profileImageUrl;

  const UpdateUserRequest({final this.nickname, final this.profileImageUrl});

  @override
  List<Object?> get props => [nickname, profileImageUrl];

  @override
  bool get stringify => true;

  @override
  String toJsonString() {
    return jsonEncode({"nickname": nickname, "profileImageUrl": profileImageUrl});
  }
}
