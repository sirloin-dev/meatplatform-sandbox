// test 만 따로 package 형태로 import 할 방법이 없음
// ignore_for_file: avoid_relative_lib_imports
/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'dart:convert';

import 'package:sirloin_sandbox_client/data/local/seraialised_data.dart';
import 'package:sirloin_sandbox_client/data/local/user/localstorage_user.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/json_serializable.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';

import '../../../domain/user/randomiser.dart';

MockSerialisedUser mockSerialisedUser({final User? user}) {
  return MockSerialisedUser(user ?? randomUser());
}

class MockSerialisedUser implements JsonSerializable {
  String? _uuid;
  String? _nickname;
  String? _profileImageUrl;
  DateTime? _savedAt;

  MockSerialisedUser(final User user)
      : _uuid = user.uuid,
        _nickname = user.nickname,
        _profileImageUrl = user.profileImageUrl,
        _savedAt = DateTime.now();

  /*
   * SerialisedUser 는 non-null 필드만 가지고 있지만, mock 은 테스트 시나리오상 null 도 필요하다.
   * 이 때문에 mock 이 SerialisedUser 와 type 이 같지 않기 때문에 test 엄밀성이 보장되지 않는다.
   *
   * 이 문제를 해결하려면 dynamic proxy 같은 기능이 필요한데 Flutter 에서는 reflection 이 없어서
   * 요구사항을 달성할 수 없다. 따라서 SerialisedUser 에 comment 로 주의사항을 추가한다.
   */
  @override
  String toJsonString() {
    return jsonEncode({
      SerialisedUser.keyUuid: _uuid,
      SerialisedUser.keyNickname: _nickname,
      SerialisedUser.keyProfileImageUrl: _profileImageUrl,
      SerialisedUser.keySavedAt: _savedAt?.toIso8601String()
    });
  }

  MockSerialisedUser uuid(final String? uuid) {
    _uuid = uuid;
    return this;
  }

  MockSerialisedUser nickname(final String? nickname) {
    _nickname = nickname;
    return this;
  }

  MockSerialisedUser profileImageUrl(final String? profileImageUrl) {
    _profileImageUrl = profileImageUrl;
    return this;
  }

  MockSerialisedUser savedAt(final DateTime? savedAt) {
    _savedAt = savedAt;
    return this;
  }

  SerialisedData<User> asSerialisedData() {
    return SerialisedUser(uuid: _uuid!, nickname: _nickname!, profileImageUrl: _profileImageUrl!, savedAt: _savedAt!);
  }
}
