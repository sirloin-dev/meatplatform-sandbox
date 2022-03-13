// test 만 따로 package 형태로 import 할 방법이 없음
// ignore_for_file: avoid_relative_lib_imports
/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:faker/faker.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/dto/response_envelope.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/dto/response_error.dart';

import '../../../../../util/randomiser.dart';
import 'mock_error_response.dart';
import 'mock_response_envelope.dart';

ErrorResponse randomErrorResponse({final String? message, final String? code}) {
  final faker = Faker();

  return ErrorResponse(message ?? faker.lorem.sentence(), code ?? faker.lorem.word());
}

ResponseEnvelope<ErrorResponse> anyErrorResponseWithEnvelope({final ErrorResponse? errorResponse}) {
  return ResponseEnvelope(ResponseType.error, DateTime.now(), errorResponse ?? randomErrorResponse());
}

ResponseEnvelope<Map<String, dynamic>> randomResponseEnvelope() {
  final Map<String, dynamic> body = {"message": Faker().lorem.sentence()};

  return ResponseEnvelope(ResponseType.values.random(), DateTime.now(), body);
}

MockResponseEnvelope mockResponseEnvelope() {
  return MockResponseEnvelope(randomResponseEnvelope());
}

MockErrorResponse mockErrorResponse() {
  return MockErrorResponse(randomErrorResponse());
}
