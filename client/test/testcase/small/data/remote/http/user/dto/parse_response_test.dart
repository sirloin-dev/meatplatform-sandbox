// test 만 따로 package 형태로 import 할 방법이 없음
// ignore_for_file: avoid_relative_lib_imports
/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_test/flutter_test.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_exceptions.dart';
import 'package:sirloin_sandbox_client/data/remote/http/user/dto/response_deleted_user.dart';
import 'package:sirloin_sandbox_client/data/remote/http/user/dto/response_user.dart';
import 'package:tuple/tuple.dart';

import '../../../../../../../lib/data/remote/http/user/dto/randomiser_response_deleted_user.dart';
import '../../../../../../../lib/data/remote/http/user/dto/randomiser_response_user.dart';

void main() {
  group("DeletedUserResponse 파싱 로직은:", () {
    group("다음의 경우 JsonParseException 을 발생시킨다:", () {
      void expectsJsonParseException(final String testName, final Map<String, dynamic> jsonMap) {
        test(testName, () async {
          // expect:
          expect(() => DeletedUserResponse.fromJson(jsonMap), throwsA(const TypeMatcher<JsonParseException>()));
        });
      }

      // Parametrised test driver:
      final Iterable<Tuple2<String, Map<String, dynamic>>> testParams = [
        Tuple2("'${DeletedUserResponse.keyUuid}' = null", mockDeletedUserResponse().uuid(null)),
      ].map((it) => Tuple2(it.item1, it.item2.toJson()));

      for (final it in testParams) {
        // Dart 에 destructuring 이 없어서 일일히 assign 해야 함
        final testName = it.item1;
        final value = it.item2;

        expectsJsonParseException(testName, value);
      }
    });

    test("정상 응답을 올바르게 파싱할 수 있다.", () {
      // given:
      final jsonMap = mockDeletedUserResponse().toJson();

      // then:
      final actual = DeletedUserResponse.fromJson(jsonMap);

      // expect:
      expect(actual.uuid, equals(jsonMap[DeletedUserResponse.keyUuid]));
    });
  });

  group("UserResponse 파싱 로직은:", () {
    group("다음의 경우 JsonParseException 을 발생시킨다:", () {
      void expectsJsonParseException(final String testName, final Map<String, dynamic> jsonMap) {
        test(testName, () async {
          // expect:
          expect(() => UserResponse.fromJson(jsonMap), throwsA(const TypeMatcher<JsonParseException>()));
        });
      }

      // Parametrised test driver:
      final Iterable<Tuple2<String, Map<String, dynamic>>> testParams = [
        Tuple2("'${UserResponse.keyUuid}' = null", mockUserResponse().uuid(null)),
        Tuple2("'${UserResponse.keyNickname}' = null", mockUserResponse().nickname(null)),
        Tuple2("'${UserResponse.keyProfileImageUrl}' = null", mockUserResponse().profileImageUrl(null)),
      ].map((it) => Tuple2(it.item1, it.item2.toJson()));

      for (final it in testParams) {
        // Dart 에 destructuring 이 없어서 일일히 assign 해야 함
        final testName = it.item1;
        final value = it.item2;

        expectsJsonParseException(testName, value);
      }
    });

    test("정상 응답을 올바르게 파싱할 수 있다.", () {
      // given:
      final jsonMap = mockUserResponse().toJson();

      // then:
      final actual = UserResponse.fromJson(jsonMap);

      // expect:
      expect(actual.uuid, equals(jsonMap[UserResponse.keyUuid]));
      expect(actual.nickname, equals(jsonMap[UserResponse.keyNickname]));
      expect(actual.profileImageUrl, equals(jsonMap[UserResponse.keyProfileImageUrl]));
    });
  });
}
