/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:faker/faker.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/json_exportable.dart';
import 'package:sirloin_sandbox_client/data/remote/http/user/dto/response_user.dart';
import 'package:uuid/uuid.dart';

UserResponse randomUserResponse({
  final String? uuid,
  final String? nickname,
  final String? profileImageUrl,
}) {
  final faker = Faker();

  return UserResponse(
      uuid: uuid ?? const Uuid().v4().toString(),
      nickname: nickname ?? faker.person.name(),
      profileImageUrl: profileImageUrl ?? faker.image.image());
}

MockUserResponse mockUserResponse() {
  return MockUserResponse(randomUserResponse());
}

class MockUserResponse implements JsonExportable {
  String? _uuid;
  String? _nickname;
  String? _profileImageUrl;

  MockUserResponse(final UserResponse src)
      : _uuid = src.uuid,
        _nickname = src.nickname,
        _profileImageUrl = src.profileImageUrl;

  MockUserResponse uuid(final String? uuid) {
    _uuid = uuid;
    return this;
  }

  MockUserResponse nickname(final String? nickname) {
    _nickname = nickname;
    return this;
  }

  MockUserResponse profileImageUrl(final String? profileImageUrl) {
    _profileImageUrl = profileImageUrl;
    return this;
  }

  @override
  Map<String, dynamic> toJson() {
    return {
      UserResponse.keyUuid: _uuid,
      UserResponse.keyNickname: _nickname,
      UserResponse.keyProfileImageUrl: _profileImageUrl
    };
  }
}
