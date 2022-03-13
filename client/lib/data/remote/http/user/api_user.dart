/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:sirloin_sandbox_client/data/remote/http/common/dto/response_envelope.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/dto/response_error.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_client.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_exceptions.dart';
import 'package:sirloin_sandbox_client/data/remote/http/user/dto/request_create_user.dart';
import 'package:sirloin_sandbox_client/data/remote/http/user/dto/request_update_user.dart';
import 'package:sirloin_sandbox_client/data/remote/http/user/dto/response_deleted_user.dart';
import 'package:sirloin_sandbox_client/data/remote/http/user/dto/response_user.dart';

abstract class UserApi {
  Future<UserResponse> createUser({required final String nickname, required final String profileImageUrl});

  Future<UserResponse> getUser(final String uuid);

  Future<UserResponse> updateUser(final String uuid, {final String? nickname, final String? profileImageUrl});

  Future<String> deleteUser(final String uuid);

  static UserApi newInstance(final String urlPrefix, final MtHttpClient httpClient) {
    return _UserApiImpl(urlPrefix, httpClient);
  }
}

class _UserApiImpl implements UserApi {
  final String _urlPrefix;
  final MtHttpClient _httpClient;

  _UserApiImpl(final this._urlPrefix, final this._httpClient);

  @override
  Future<UserResponse> createUser({required final String nickname, required final String profileImageUrl}) async {
    final String jsonString =
        await _httpClient.post(_urlPrefix, CreateUserRequest(nickname: nickname, profileImageUrl: profileImageUrl));
    return _openEnvelope(jsonString).toUserResponse();
  }

  @override
  Future<UserResponse> getUser(final String uuid) async {
    final String jsonString = await _httpClient.get(_urlPrefix);
    return _openEnvelope(jsonString).toUserResponse();
  }

  @override
  Future<UserResponse> updateUser(final String uuid, {final String? nickname, final String? profileImageUrl}) async {
    final String jsonString = await _httpClient.patch(
        "$_urlPrefix/$uuid", UpdateUserRequest(nickname: nickname, profileImageUrl: profileImageUrl));
    return _openEnvelope(jsonString).toUserResponse();
  }

  @override
  Future<String> deleteUser(final String uuid) async {
    final String jsonString = await _httpClient.delete("$_urlPrefix/$uuid");
    final response = _openEnvelope(jsonString).toDeletedUserResponse();
    return response.uuid;
  }

  ResponseEnvelope _openEnvelope(final String jsonString) {
    final envelope = ResponseEnvelope.fromJson(jsonString);

    return envelope.type.when(
        ok: () => envelope,
        error: () {
          final errorResponse = ErrorResponse.fromJson(envelope.body);
          throw UnexpectedResponseException(errorResponse.message, errorResponse.code);
        });
  }
}

extension _UserApiExtension on ResponseEnvelope {
  UserResponse toUserResponse() {
    return UserResponse.fromJson(body);
  }

  DeletedUserResponse toDeletedUserResponse() {
    return DeletedUserResponse.fromJson(body);
  }
}
