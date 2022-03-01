/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_exceptions.dart';

@immutable
class DeletedUserResponse extends Equatable {
  final String uuid;

  @visibleForTesting
  static const keyUuid = "uuid";

  const DeletedUserResponse({required this.uuid});

  @override
  List<Object?> get props => [uuid];

  @override
  bool get stringify => true;

  static DeletedUserResponse fromJson(final Map<String, dynamic> jsonMap) {
    final uuid = jsonMap[keyUuid];

    if (uuid == null) {
      throw JsonParseException();
    }

    return DeletedUserResponse(uuid: uuid);
  }
}
