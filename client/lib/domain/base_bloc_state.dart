/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

abstract class BaseBlocState {}

@immutable
abstract class BaseEmptyState implements BaseBlocState {}

@immutable
abstract class BaseExceptionalState extends Equatable implements BaseBlocState {
  final Exception? exception;

  const BaseExceptionalState(this.exception);

  @override
  List<Object?> get props => [exception];

  @override
  bool get stringify => true;
}
