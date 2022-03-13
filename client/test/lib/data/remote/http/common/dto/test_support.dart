/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'dart:convert';

import 'package:sirloin_sandbox_client/data/remote/http/common/dto/response_envelope.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/dto/response_error.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/json_exportable.dart';

extension ErrorResponseExtension on ErrorResponse {
  Map<String, dynamic> toJson() {
    return {
      ErrorResponse.keyMessage: message,
      ErrorResponse.keyCode: code,
    };
  }

  String toJsonString() {
    return jsonEncode(toJson());
  }
}

extension ResponseEnvelopeExtension on ResponseEnvelope {
  Map<String, dynamic> toJson() {
    final dynamic jsonBody;
    if (body is JsonExportable) {
      jsonBody = body.toJson();
    } else if (body is ErrorResponse) {
      jsonBody = (body as ErrorResponse).toJson();
    } else {
      // 매우 높은 확률로 여기서 오류 발생 (타입 추가될 때 마다 테스트에서 처리 필요)
      jsonBody = body;
    }

    return {
      ResponseEnvelope.keyType: type.value,
      ResponseEnvelope.keyTimestamp: timestamp.toIso8601String(),
      ResponseEnvelope.keyBody: jsonBody
    };
  }

  String toJsonString() {
    return jsonEncode(toJson());
  }
}
