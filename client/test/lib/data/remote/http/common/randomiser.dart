/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:faker/faker.dart';
import 'package:meta/meta.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/json_serializable.dart';

typedef OnToJsonString = String Function();

JsonSerializable anyJsonSerializable({final OnToJsonString? toJsonString}) {
  if (toJsonString == null) {
    final word = Faker().lorem.word();
    return _SimpleJsonSerializable(() => word);
  } else {
    return _SimpleJsonSerializable(toJsonString);
  }
}

@immutable
class _SimpleJsonSerializable extends Equatable implements JsonSerializable {
  final OnToJsonString onToJsonString;

  const _SimpleJsonSerializable(this.onToJsonString);

  @override
  String toJsonString() {
    return onToJsonString.call();
  }

  @override
  List<Object?> get props => [onToJsonString];
}
