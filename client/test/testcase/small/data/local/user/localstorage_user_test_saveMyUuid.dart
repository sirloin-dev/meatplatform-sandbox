// test target 의 method name 표현을 위한 naming rule 위반
// ignore_for_file: file_names
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

void saveMyUuidTestCases(final SutSupplier supplier, final MockLocalStorage localStorage, final Logger logger) {
  late UserLocalStorage sut;

  setUp(() {
    sut = supplier.call();
  });

  test("null 을 입력받으면, 저장된 uuid 를 삭제한다.", () async {
    // given:
    const String? uuid = null;

    // when:
    await sut.saveMyUuid(uuid);

    // expect:
    verify(localStorage.deleteItem(UserLocalStorageImpl.keyMyUuid)).called(1);
  });

  test("uuid 를 입력받으면, 입력받은 uuid 를 저장한다.", () async {
    // given:
    final String? uuid = const Uuid().v4().toString();

    // when:
    await sut.saveMyUuid(uuid);

    // expect:
    verify(localStorage.setItem(UserLocalStorageImpl.keyMyUuid, uuid)).called(1);
  });
}
