// test 만 따로 package 형태로 import 할 방법이 없음
// ignore_for_file: avoid_relative_lib_imports
/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;
import 'package:mockito/mockito.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_client.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_exceptions.dart';

import '../../../../../lib/data/remote/http/common/dto/randomiser.dart';
import '../../../../../lib/data/remote/http/common/dto/test_support.dart';
import '../../../../../lib/data/remote/http/common/randomiser.dart';
import '../../../../../mock/@http/http.mocks.dart';

void main() {
  const url = "http://localhost/";
  final uri = Uri.parse(url);
  const expectedBody = "TEST";
  final expectedOkResponse = Future.value(http.Response(expectedBody, 200));

  late http.Client mockClient;
  late MtHttpClient sut;

  setUp(() {
    mockClient = MockHttpClient();
    sut = MtHttpClient.newInstance(factory: () => mockClient);
  });

  test("HTTP GET 은 URI, Header 를 이용한다", () async {
    // when:
    when(mockClient.get(uri, headers: MtHttpClient.defaultHeaders)).thenAnswer((_) => expectedOkResponse);

    // then:
    final actual = await sut.get(url);

    // expect:
    expect(actual, equals(expectedBody));
  });

  test("HTTP POST 는 URI, JSON Body, Header 를 이용한다", () async {
    // given:
    final request = anyJsonSerializable();

    // when:
    when(mockClient.post(uri, body: request.toJsonString(), headers: MtHttpClient.defaultHeaders))
        .thenAnswer((_) => expectedOkResponse);

    // then:
    final actual = await sut.post(url, request);

    // expect:
    expect(actual, equals(expectedBody));
  });

  test("HTTP PATCH 는 URI, JSON Body, Header 를 이용한다", () async {
    // given:
    final request = anyJsonSerializable();

    // when:
    when(mockClient.patch(uri, body: request.toJsonString(), headers: MtHttpClient.defaultHeaders))
        .thenAnswer((_) => expectedOkResponse);

    // then:
    final actual = await sut.patch(url, request);

    // expect:
    expect(actual, equals(expectedBody));
  });

  test("HTTP DELETE 는 URI, Header 를 이용한다", () async {
    // when:
    when(mockClient.delete(uri, headers: MtHttpClient.defaultHeaders)).thenAnswer((_) => expectedOkResponse);

    // then:
    final actual = await sut.delete(url);

    // expect:
    expect(actual, equals(expectedBody));
  });

  group("응답을 처리할 때 HTTP 200 응답이 아닌 경우: ", () {
    setMockResponse(final String response) {
      when(mockClient.get(uri, headers: MtHttpClient.defaultHeaders))
          .thenAnswer((_) => Future.value(http.Response(response, 400)));
    }

    expectException(final TypeMatcher exception) {
      expect(() async => await sut.get(url), throwsA(exception));
    }

    test("응답이 ErrorResponse 라면 UnexpectedResponseException 이 발생한다", () async {
      // when:
      setMockResponse(anyErrorResponseWithEnvelope().toJsonString());

      // expect:
      expectException(const TypeMatcher<UnexpectedResponseException>());
    });

    test("응답이 ErrorResponse 가 아니면 HttpStatusCodeException 이 발생한다", () async {
      // when:
      setMockResponse("<html></html>");

      // expect:
      expectException(const TypeMatcher<HttpStatusCodeException>());
    });
  });
}
