/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:sirloin_sandbox_client/domain/user/user.dart';

abstract class UserReadonlyRepository {
  Future<User?> findSavedSelf();

  Future<User> getUser({required final String uuid, final bool forceRefresh = false});
}
