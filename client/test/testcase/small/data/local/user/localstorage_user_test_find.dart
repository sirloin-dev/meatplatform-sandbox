// test 만 따로 package 형태로 import 할 방법이 없음
// ignore_for_file: avoid_relative_lib_imports
/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';
import 'package:logger/logger.dart';
import 'package:mockito/mockito.dart';
import 'package:sirloin_sandbox_client/data/local/user/localstorage_user.dart';
import 'package:tuple/tuple.dart';
import 'package:uuid/uuid.dart';

import '../../../../../lib/data/local/user/randomiser.dart';
import '../../../../../lib/domain/user/randomiser.dart';
import '../../../../../mock/@localstorage/local_storage.mocks.dart';
import 'localstorage_user_test.dart';

void findTestCases(final SutSupplier supplier, final MockLocalStorage localStorage, final Logger logger) {
  late UserLocalStorage sut;
  late String uuid;

  setUp(() {
    sut = supplier.call();
    uuid = const Uuid().v4().toString();
  });

  group("로컬 캐시에서 읽어들일 수 없을 때 null 을 반환한다:", () {
    test("로컬 캐시 접근에 실패", () async {
      // when:
      when(localStorage.getItem(uuid)).thenThrow(Exception("TEST!!"));

      // then:
      final serialisedUser = await sut.find(uuid);

      // expect:
      expect(serialisedUser, equals(null));
    });

    void expectsNullResult(final String testName, final String? storageReturnValue) {
      test("저장된 값이 $testName", () async {
        // when:
        when(localStorage.getItem(uuid)).thenReturn(storageReturnValue);

        // then:
        final serialisedUser = await sut.find(uuid);

        // expect:
        expect(serialisedUser, equals(null));
      });
    }

    // Parametrised test 가 없어서 직접 구현
    group("로컬 캐시에 저장된 항목의 포맷 오류:", () {
      final testParams = <Tuple2<String, String?>>[
        const Tuple2("null", null),
        const Tuple2("json 형식이 아님", ""),
      ];

      for (final it in testParams) {
        // Dart 에 destructuring 이 없어서 일일히 assign 해야 함
        final testName = it.item1;
        final value = it.item2;

        expectsNullResult(testName, value);
      }
    });

    // Parametrised test 가 없어서 직접 구현
    group("저장된 항목의 필수값 중 하나가 null:", () {
      // Parametrised test driver:
      final Iterable<Tuple2<String, String>> testParams = [
        Tuple2("'${SerialisedUser.keyUuid}' = null", mockSerialisedUser().uuid(null)),
        Tuple2("'${SerialisedUser.keyNickname}' = null", mockSerialisedUser().nickname(null)),
        Tuple2("'${SerialisedUser.keyProfileImageUrl}' = null", mockSerialisedUser().profileImageUrl(null)),
        Tuple2("'${SerialisedUser.keySavedAt}' = null", mockSerialisedUser().savedAt(null)),
      ].map((it) => Tuple2(it.item1, it.item2.toJsonString()));

      for (final it in testParams) {
        // Dart 에 destructuring 이 없어서 일일히 assign 해야 함
        final testName = it.item1;
        final value = it.item2;

        expectsNullResult(testName, value);
      }
    });
  });

  test("저장된 항목을 User 로 올바르게 반환한다", () async {
    // given:
    final expected = SerialisedUser.from(randomUser(uuid: uuid));
    final jsonSerialisedUser = jsonEncode(expected.toJson());

    // when:
    when(localStorage.getItem(uuid)).thenReturn(jsonSerialisedUser);

    // then:
    final actual = await sut.find(uuid);

    // expect:
    expect(actual, equals(expected));
  });
}
