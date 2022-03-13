/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:sirloin_sandbox_client/data/remote/http/common/json_exportable.dart';
import 'package:sirloin_sandbox_client/data/remote/http/user/dto/response_deleted_user.dart';
import 'package:uuid/uuid.dart';

DeletedUserResponse randomDeletedUserResponse({final String? uuid}) {
  return DeletedUserResponse(uuid: uuid ?? const Uuid().v4().toString());
}

MockDeletedUserResponse mockDeletedUserResponse() {
  return MockDeletedUserResponse(randomDeletedUserResponse());
}

class MockDeletedUserResponse implements JsonExportable {
  String? _uuid;

  MockDeletedUserResponse(final DeletedUserResponse src) : _uuid = src.uuid;

  MockDeletedUserResponse uuid(final String? uuid) {
    _uuid = uuid;
    return this;
  }

  @override
  Map<String, dynamic> toJson() {
    return {DeletedUserResponse.keyUuid: _uuid};
  }
}
