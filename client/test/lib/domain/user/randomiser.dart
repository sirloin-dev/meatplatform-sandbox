/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:sirloin_sandbox_client/domain/user/user.dart';
import 'package:uuid/uuid.dart';
import 'package:faker/faker.dart';

User randomUser({
  final String? uuid,
  final String? nickname,
  final String? profileImageUrl,
}) {
  final faker = Faker();

  return User.create(
      uuid: uuid ?? const Uuid().v4().toString(),
      nickname: nickname ?? faker.person.name(),
      profileImageUrl: profileImageUrl ?? faker.image.image());
}
