/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:sirloin_sandbox_client/data/local/user/localstorage_user.dart';

// test 만 따로 package 형태로 import 할 방법이 없음
// ignore: avoid_relative_lib_imports
import '../../../../../lib/test_components.dart';
import '../../../../../mock/@localstorage/local_storage.mocks.dart';
import 'localstorage_user_test_find.dart';
import 'localstorage_user_test_findMyUuid.dart';
import 'localstorage_user_test_save.dart';
import 'localstorage_user_test_saveMyUuid.dart';

typedef SutSupplier = UserLocalStorage Function();

/// UserLocalStorage Test suite
void main() {
  final mockLocalStorage = MockLocalStorage();
  final logger = newTestLogger();

  UserLocalStorage instantiateSut() => UserLocalStorage.newInstance(mockLocalStorage, logger);

  group("findMyUuid 는:", () {
    findMyUuidTestCases(instantiateSut, mockLocalStorage, logger);
  });

  group("saveMyUuid 는:", () {
    saveMyUuidTestCases(instantiateSut, mockLocalStorage, logger);
  });

  group("find 는:", () {
    findTestCases(instantiateSut, mockLocalStorage, logger);
  });

  group("save 는:", () {
    saveTestCases(instantiateSut, mockLocalStorage, logger);
  });

  tearDown(() {
    reset(mockLocalStorage);
  });
}
