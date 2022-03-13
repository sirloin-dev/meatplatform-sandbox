// test target 의 method name 표현을 위한 naming rule 위반
// ignore_for_file: file_names
// test 만 따로 package 형태로 import 할 방법이 없음
// ignore_for_file: avoid_relative_lib_imports
/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_test/flutter_test.dart';
import 'package:logger/logger.dart';
import 'package:mockito/mockito.dart';
import 'package:sirloin_sandbox_client/data/local/user/localstorage_user.dart';
import 'package:uuid/uuid.dart';

import '../../../../../mock/@localstorage/local_storage.mocks.dart';
import 'localstorage_user_test.dart';

void findMyUuidTestCases(final SutSupplier supplier, final MockLocalStorage localStorage, final Logger logger) {
  late UserLocalStorage sut;

  setUp(() {
    sut = supplier.call();
  });

  test("uuid 가 저장되어 있다면, 저장된 값을 반환한다", () async {
    // given:
    final uuid = const Uuid().v4().toString();

    // when:
    when(localStorage.getItem(UserLocalStorageImpl.keyMyUuid)).thenReturn(uuid);

    // then:
    final savedUuid = await sut.findMyUuid();

    // expect:
    expect(savedUuid, equals(uuid));
  });

  test("uuid 가 저장되어 있지 않다면, null 을 반환한다", () async {
    // when:
    when(localStorage.getItem(UserLocalStorageImpl.keyMyUuid)).thenReturn(null);

    // then:
    final savedUuid = await sut.findMyUuid();

    // expect:
    expect(savedUuid, equals(null));
  });
}
