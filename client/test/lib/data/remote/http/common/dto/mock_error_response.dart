/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'dart:convert';

import 'package:sirloin_sandbox_client/data/remote/http/common/dto/response_error.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/json_exportable.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/json_serializable.dart';

class MockErrorResponse implements JsonSerializable, JsonExportable {
  String? _message;
  String? _code;

  MockErrorResponse(final ErrorResponse errorResponse)
      : _message = errorResponse.message,
        _code = errorResponse.code;

  MockErrorResponse message(final String? message) {
    _message = message;
    return this;
  }

  MockErrorResponse code(final String? code) {
    _code = code;
    return this;
  }

  @override
  Map<String, dynamic> toJson() {
    return {
      ErrorResponse.keyMessage: _message,
      ErrorResponse.keyCode: _code,
    };
  }

  @override
  String toJsonString() {
    return jsonEncode(toJson());
  }
}
