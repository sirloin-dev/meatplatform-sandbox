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
import 'package:sirloin_sandbox_client/domain/user/user.dart';

import '../../../../../lib/domain/user/randomiser.dart';
import '../../../../../mock/@localstorage/local_storage.mocks.dart';
import 'localstorage_user_test.dart';

void saveTestCases(final SutSupplier supplier, final MockLocalStorage localStorage, final Logger logger) {
  late UserLocalStorage sut;
  late User user;

  setUp(() {
    sut = supplier.call();
    user = randomUser();
  });

  test("User 를 로컬 캐시에 저장한다", () async {
    // then:
    final actual = await sut.save(user);

    // expect:
    expect(actual, isNot(null));
    verify(localStorage.setItem(user.uuid, any)).called(1);
  });

  test("로컬 캐시 저장이 실패하더라도 save 는 항상 성공한다", () async {
    // when:
    when(localStorage.setItem(any, any)).thenThrow(Exception("TEST!!"));

    // then:
    final actual = await sut.save(user);

    // expect null
    expect(actual, equals(null));
  });
}
