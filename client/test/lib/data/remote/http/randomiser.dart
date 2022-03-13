/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:faker/faker.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_exceptions.dart';
import 'package:sirloin_sandbox_client/util/numerics.dart';

UnexpectedResponseException randomUnexpectedResponseException({
  final String? message,
  final String? code,
}) {
  final faker = Faker();

  return UnexpectedResponseException(
      message ?? faker.lorem.sentence(), code ?? faker.randomGenerator.integer(int32MaxValue).toString());
}
