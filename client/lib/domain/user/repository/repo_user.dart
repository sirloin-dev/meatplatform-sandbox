/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:sirloin_sandbox_client/domain/user/repository/repo_ro_user.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';

abstract class UserRepository extends UserReadonlyRepository {
  Future<User> register({required final String nickname, required final String profileImageUrl});

  Future<User> updateProfile({
    required final String uuid,
    final String? nickname,
    final String? profileImageUrl,
  });

  Future<void> deregister(final String uuid);

  Future<void> deleteSavedUser(final String uuid);
}
