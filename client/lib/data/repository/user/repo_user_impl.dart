/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:logger/logger.dart';
import 'package:sirloin_sandbox_client/data/local/user/localstorage_user.dart';
import 'package:sirloin_sandbox_client/data/remote/http/user/api_user.dart';
import 'package:sirloin_sandbox_client/domain/user/repository/repo_user.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';

/*
 * POINT: 이 예제에서는 User 라는 단일 아이템만 반환하고 있습니다.
 * 여러 이용자의 목록을 반환하도록 기능을 구현하려면 어떻게 해야 할까요?
 *
 * localCache write 권한 없는 문제 어떻게 해결할 수 있을까요?
 */
class UserRepositoryImpl implements UserRepository {
  final UserLocalStorage _localCache;
  final UserApi _api;
  final Logger _logger;

  UserRepositoryImpl(final this._localCache, final this._api, final this._logger);

  @override
  Future<User?> findSavedSelf() async {
    final maybeMyUuid = await _localCache.findMyUuid();
    if (maybeMyUuid == null) {
      _logger.d("No saved user info is found");
      return null;
    }

    final maybeUser = await _localCache.find(maybeMyUuid);
    if (maybeUser == null) {
      _logger.d("Saved user data is maybe corrupted");
    }
    return maybeUser?.get();
  }

  @override
  Future<User> getUser({required final String uuid, final bool forceRefresh = false}) async {
    if (!forceRefresh) {
      final maybeSavedUser = await _localCache.find(uuid);
      if (maybeSavedUser != null && !maybeSavedUser.isExpired()) {
        return maybeSavedUser.get();
      }
    }

    final remoteUser = await _api.getUser(uuid);
    final parsedUser = remoteUser.toUser();
    try {
      await _localCache.save(parsedUser);
    } catch (ignore) {
      _logger.w("getUser(): Failed to save user data '$uuid' - ", ignore);
    }

    return parsedUser;
  }

  @override
  Future<User> register({required final String nickname, required final String profileImageUrl}) async {
    final registeredUser = await _api.createUser(nickname: nickname, profileImageUrl: profileImageUrl);
    final user = registeredUser.toUser();

    try {
      await Future.wait([_localCache.save(user), _localCache.saveMyUuid(user.uuid)]);
    } catch (ignore) {
      _logger.w("register(): Failed to save user data '${user.uuid}' - ", ignore);
    }

    return user;
  }

  @override
  Future<User> updateProfile(
      {required final String uuid, final String? nickname, final String? profileImageUrl}) async {
    final updatedUser = await _api.updateUser(uuid, nickname: nickname, profileImageUrl: profileImageUrl);
    final parsedUser = updatedUser.toUser();

    try {
      await _localCache.save(parsedUser);
    } catch (ignore) {
      _logger.w("updateProfile(): Failed to save user data '$uuid' - ", ignore);
    }

    return parsedUser;
  }

  // POINT: API Call 성공, local cache delete 실패시 어떻게 해야 할까요?
  @override
  Future<void> deregister(final String uuid) async {
    await _api.deleteUser(uuid);
    await Future.wait([deleteSavedUser(uuid), _localCache.saveMyUuid(null)]);
  }

  @override
  Future<void> deleteSavedUser(final String uuid) async {
    await _localCache.delete(uuid);
  }
}
