/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'dart:convert';

import 'package:equatable/equatable.dart';
import 'package:flutter/foundation.dart';
import 'package:localstorage/localstorage.dart';
import 'package:logger/logger.dart';
import 'package:sirloin_sandbox_client/data/local/seraialised_data.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';

import '../../remote/http/common/json_exportable.dart';

abstract class UserLocalStorage {
  Future<String?> findMyUuid();

  Future<String?> saveMyUuid(final String? uuid);

  Future<SerialisedData<User>?> find(final String uuid);

  Future<SerialisedData<User>?> save(final User user);

  Future<bool> delete(final String uuid);

  static UserLocalStorage newInstance(final LocalStorage localStorage, final Logger logger) {
    return UserLocalStorageImpl(localStorage, logger);
  }
}

// POINT: local storage 접근로직 전부를 await 할 필요가 있을까요?
@visibleForTesting
class UserLocalStorageImpl implements UserLocalStorage {
  @visibleForTesting
  static const keyMyUuid = "myUuid";

  final LocalStorage _localStorage;
  final Logger _logger;

  UserLocalStorageImpl(this._localStorage, this._logger);

  @override
  Future<String?> findMyUuid() async {
    return _localStorage.getItem(keyMyUuid);
  }

  @override
  Future<String?> saveMyUuid(final String? uuid) async {
    final Future storageTask;
    if (uuid == null) {
      _logger.d("Removing uuid information");
      storageTask = _localStorage.deleteItem(keyMyUuid);
    } else {
      _logger.d("Registering uuid: '$uuid'");
      storageTask = _localStorage.setItem(keyMyUuid, uuid);
    }

    await storageTask;

    return uuid;
  }

  @override
  Future<SerialisedData<User>?> find(final String uuid) async {
    final dynamic maybeJson;
    try {
      _logger.d("Finding user info for '$uuid'");
      maybeJson = await _localStorage.getItem(uuid);
    } catch (e) {
      _logger.w("Unable to load user('$uuid') into local cache -", e);
      return null;
    }

    if (maybeJson == null) {
      return null;
    }

    return SerialisedUser.fromJson(maybeJson, _logger);
  }

  @override
  Future<SerialisedData<User>?> save(final User user) async {
    final serialisedUser = SerialisedUser.from(user);
    try {
      _logger.d("Saving user '$user'");
      await _localStorage.setItem(user.uuid, serialisedUser.toJson());
      return serialisedUser;
    } catch (e) {
      _logger.w("Unable to save user into local cache -", e);
      return null;
    }
  }

  @override
  Future<bool> delete(final String uuid) async {
    _logger.d("Deleting user entry '$uuid'");
    try {
      await _localStorage.deleteItem(uuid);
      return true;
    } catch (e) {
      _logger.w("Unable to delete user('$uuid') in local cache -", e);
      return false;
    }
  }
}

/// 이 클래스 로직 수정시, MockSerialisedUser 를 이용한 test 들을 재검토 해야 합니다.
/// 자세한 이유는 MockSerialisedUser 에 기록했습니다.
@visibleForTesting
@immutable
class SerialisedUser extends Equatable with SerialisedData<User> implements JsonExportable {
  final String uuid;
  final String nickname;
  final String profileImageUrl;

  late final User user = _createUser();

  @visibleForTesting
  static const keyUuid = "uuid";
  @visibleForTesting
  static const keyNickname = "nickname";
  @visibleForTesting
  static const keyProfileImageUrl = "profileImageUrl";
  @visibleForTesting
  static const keySavedAt = "savedAt";

  SerialisedUser({
    required this.uuid,
    required this.nickname,
    required this.profileImageUrl,
    required final DateTime savedAt,
  }) : super() {
    super.savedAt = savedAt;
  }

  @override
  User get() => user;

  User _createUser() => User.create(uuid: uuid, nickname: nickname, profileImageUrl: profileImageUrl);

  @override
  Map<String, dynamic> toJson() => {
        keyUuid: uuid,
        keyNickname: nickname,
        keyProfileImageUrl: profileImageUrl,
        keySavedAt: savedAt.toIso8601String()
      };

  @override
  List<Object?> get props => [uuid, nickname, profileImageUrl, savedAt];

  @override
  bool get stringify => true;

  static SerialisedUser from(final User user) {
    return SerialisedUser(
        uuid: user.uuid, nickname: user.nickname, profileImageUrl: user.profileImageUrl, savedAt: DateTime.now());
  }

  static SerialisedUser? fromJson(final String jsonString, final Logger logger) {
    final Map<String, dynamic> decodedJson;
    final String? uuid;
    final String? nickname;
    final String? profileImageUrl;
    final DateTime? savedAt;
    try {
      decodedJson = jsonDecode(jsonString);
      uuid = decodedJson[keyUuid];
      nickname = decodedJson[keyNickname];
      profileImageUrl = decodedJson[keyProfileImageUrl];
      savedAt = DateTime.tryParse(decodedJson[keySavedAt]);
    } catch (e) {
      logger.d("SerialisedUser: JSON parse error - '$jsonString'");
      return null;
    }

    if (uuid == null || nickname == null || profileImageUrl == null || savedAt == null) {
      logger.d("SerialisedUser: null on non-nullable values - $decodedJson");
      return null;
    }

    return SerialisedUser(uuid: uuid, nickname: nickname, profileImageUrl: profileImageUrl, savedAt: savedAt);
  }
}
