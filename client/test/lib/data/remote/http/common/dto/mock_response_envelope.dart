/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'dart:convert';

import 'package:sirloin_sandbox_client/data/remote/http/common/dto/response_envelope.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/json_exportable.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/json_serializable.dart';

class MockResponseEnvelope implements JsonSerializable, JsonExportable {
  ResponseType? _type;
  String? _timestamp;
  Object? _body;

  MockResponseEnvelope(final ResponseEnvelope response)
      : _type = response.type,
        _timestamp = response.timestamp.toIso8601String(),
        _body = response.body;

  ResponseEnvelope<Object> get() {
    return ResponseEnvelope(_type!, DateTime.parse(_timestamp!), _body!);
  }

  MockResponseEnvelope type(final String? type) {
    _type = ResponseType.from(type);
    return this;
  }

  MockResponseEnvelope timestamp(final String? timestamp) {
    _timestamp = timestamp;
    return this;
  }

  MockResponseEnvelope body(final Object? body) {
    _body = body;
    return this;
  }

  @override
  Map<String, dynamic> toJson() {
    return {
      ResponseEnvelope.keyType: _type?.value,
      ResponseEnvelope.keyTimestamp: _timestamp,
      ResponseEnvelope.keyBody: _body
    };
  }

  @override
  String toJsonString() {
    return jsonEncode(toJson());
  }
}
