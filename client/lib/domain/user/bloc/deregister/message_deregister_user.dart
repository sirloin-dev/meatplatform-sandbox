/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

abstract class DeregisterUserBlocMessage {}

@immutable
class ProceedDeregisterMessage extends Equatable implements DeregisterUserBlocMessage {
  final String uuid;

  const ProceedDeregisterMessage(this.uuid);

  @override
  List<Object?> get props => [uuid];

  @override
  bool get stringify => true;
}
