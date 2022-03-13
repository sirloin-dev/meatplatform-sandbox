/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_exceptions.dart';

@immutable
class ErrorResponse extends Equatable {
  final String message;
  final String code;

  @visibleForTesting
  static const keyMessage = "message";
  @visibleForTesting
  static const keyCode = "code";

  const ErrorResponse(this.message, this.code);

  @override
  List<Object?> get props => [message, code];

  @override
  bool get stringify => true;

  static ErrorResponse fromJson(final Map<String, dynamic> jsonMap) {
    final message = jsonMap[keyMessage];
    final code = jsonMap[keyCode];

    if (message == null || code == null) {
      throw JsonParseException();
    }

    return ErrorResponse(message, code);
  }
}
