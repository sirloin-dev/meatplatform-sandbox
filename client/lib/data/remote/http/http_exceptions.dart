/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'dart:io';

class HttpStatusCodeException implements HttpException {
  @override
  final String message;
  @override
  final Uri? uri;
  final int statusCode;

  HttpStatusCodeException(this.message, this.uri, this.statusCode);
}

class JsonParseException extends IOException implements Exception {
  final String message;

  JsonParseException([final this.message = ""]);

  @override
  String toString() {
    if (message.isEmpty) {
      return "JsonParseException";
    } else {
      return "JsonParseException: $message";
    }
  }
}

class UnexpectedResponseException implements Exception {
  final String message;
  final String code;

  UnexpectedResponseException(final this.message, final this.code);
}
