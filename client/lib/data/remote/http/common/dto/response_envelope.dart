/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'dart:convert';

import 'package:equatable/equatable.dart';
import 'package:flutter/cupertino.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_exceptions.dart';

typedef ResponseTypeToObject<T> = T Function();

@immutable
class ResponseType {
  static const ok = ResponseType("ok");
  static const error = ResponseType("error");

  final String value;

  const ResponseType(this.value);

  // Dart 에 Sealed class 가 없어서 pattern matching 을 직접 손으로 구현...
  T when<T>({
    required final ResponseTypeToObject<T> ok,
    required final ResponseTypeToObject<T> error,
  }) {
    if (this == ResponseType.ok) {
      return ok();
    } else {
      return error();
    }
  }

  static List<ResponseType> get values => [ok, error];

  static ResponseType? from(final String? value) {
    for (final it in values) {
      if (it.value == value) {
        return it;
      }
    }

    return null;
  }
}

@immutable
class ResponseEnvelope<T> extends Equatable {
  final ResponseType type;
  final DateTime timestamp;
  final T body;

  @visibleForTesting
  static const keyType = "type";
  @visibleForTesting
  static const keyTimestamp = "timestamp";
  @visibleForTesting
  static const keyBody = "body";

  const ResponseEnvelope(this.type, this.timestamp, this.body);

  @override
  List<Object?> get props => [type, timestamp, body as Object];

  @override
  bool get stringify => true;

  static ResponseEnvelope<Map<String, dynamic>> fromJson(final String? jsonString) {
    if (jsonString == null) {
      throw JsonParseException("Cannot parse null json string");
    }

    final ResponseType? type;
    final DateTime? timestamp;
    final Map<String, dynamic>? body;
    try {
      final Map<String, dynamic> jsonMap = jsonDecode(jsonString);
      type = ResponseType.from(jsonMap[keyType]);
      timestamp = DateTime.tryParse(jsonMap[keyTimestamp]);
      body = jsonMap[keyBody];
    } catch (e) {
      throw JsonParseException("Cannot parse ResponseEnvelope - (${e.toString()})");
    }

    if (type == null || timestamp == null || body == null) {
      throw JsonParseException("SerialisedUser: null on non-nullable values - $jsonString");
    }

    return ResponseEnvelope(type, timestamp, body);
  }
}
