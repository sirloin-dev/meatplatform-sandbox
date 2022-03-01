// test 만 따로 package 형태로 import 할 방법이 없음
// ignore_for_file: avoid_relative_lib_imports
/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_test/flutter_test.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/dto/response_envelope.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/dto/response_error.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_exceptions.dart';
import 'package:tuple/tuple.dart';

import '../../../../../../../lib/data/remote/http/common/dto/randomiser.dart';
import '../../../../../../../lib/data/remote/http/common/dto/test_support.dart';

void main() {
  group("ResponseEnvelope 파싱 로직은:", () {
    group("다음의 경우 JsonParseException 을 발생시킨다:", () {
      void expectsJsonParseException(final String testName, final String? jsonString) {
        test(testName, () async {
          // expect:
          expect(() => ResponseEnvelope.fromJson(jsonString), throwsA(const TypeMatcher<JsonParseException>()));
        });
      }

      // Parametrised test driver:
      final Iterable<Tuple2<String, String?>> testParams = [
        const Tuple2("null response", null),
        Tuple2("'${ResponseEnvelope.keyType}' = null", mockResponseEnvelope().type(null)),
        Tuple2("'${ResponseEnvelope.keyTimestamp}' = null", mockResponseEnvelope().timestamp(null)),
        Tuple2("'${ResponseEnvelope.keyTimestamp}' = Wrong string", mockResponseEnvelope().timestamp("?")),
        Tuple2("'${ResponseEnvelope.keyBody}' = null", mockResponseEnvelope().body(null)),
      ].map((it) => Tuple2(it.item1, it.item2?.toJsonString()));

      for (final it in testParams) {
        // Dart 에 destructuring 이 없어서 일일히 assign 해야 함
        final testName = it.item1;
        final value = it.item2;

        expectsJsonParseException(testName, value);
      }
    });

    test("정상 응답을 올바르게 파싱할 수 있다.", () {
      // given:
      final expected = mockResponseEnvelope();
      final jsonString = expected.toJsonString();

      // then:
      final actual = ResponseEnvelope.fromJson(jsonString);

      // expect:
      expect(actual.type, equals(expected.get().type));
      expect(actual.timestamp, equals(expected.get().timestamp));
      expect(actual.body, equals(expected.get().body));
    });
  });

  group("ErrorResponse 파싱 로직은:", () {
    group("다음의 경우 JsonParseException 을 발생시킨다:", () {
      void expectsJsonParseException(final String testName, final Map<String, dynamic> jsonMap) {
        test(testName, () async {
          // expect:
          expect(() => ErrorResponse.fromJson(jsonMap), throwsA(const TypeMatcher<JsonParseException>()));
        });
      }

      // Parametrised test driver:
      final Iterable<Tuple2<String, Map<String, dynamic>>> testParams = [
        Tuple2("'${ErrorResponse.keyCode}' = null", mockErrorResponse().code(null)),
        Tuple2("'${ErrorResponse.keyMessage}' = null", mockErrorResponse().message(null)),
      ].map((it) => Tuple2(it.item1, it.item2.toJson()));

      for (final it in testParams) {
        // Dart 에 destructuring 이 없어서 일일히 assign 해야 함
        final testName = it.item1;
        final value = it.item2;

        expectsJsonParseException(testName, value);
      }
    });

    test("정상 응답을 올바르게 파싱할 수 있다", () async {
      // given:
      final jsonMap = randomErrorResponse().toJson();
      // then:
      final actual = ErrorResponse.fromJson(jsonMap);

      // expect:
      expect(actual.message, equals(jsonMap[ErrorResponse.keyMessage]));
      expect(actual.code, equals(jsonMap[ErrorResponse.keyCode]));
    });
  });
}
