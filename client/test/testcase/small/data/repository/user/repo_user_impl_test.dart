// test 만 따로 package 형태로 import 할 방법이 없음
// ignore_for_file: avoid_relative_lib_imports
/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:sirloin_sandbox_client/data/repository/user/repo_user_impl.dart';

import '../../../../../lib/test_components.dart';
import '../../../../../mock/lib/data/local/user/localstorage_user.mocks.dart';
import '../../../../../mock/lib/data/remote/http/user/api_user.mocks.dart';
import 'repo_user_impl_test_deregister.dart';
import 'repo_user_impl_test_findSavedSelf.dart';
import 'repo_user_impl_test_getUser.dart';
import 'repo_user_impl_test_register.dart';
import 'repo_user_impl_test_updateProfile.dart';

typedef SutSupplier = UserRepositoryImpl Function();

void main() {
  final mockUserLocalStorage = MockUserLocalStorage();
  final mockUserApi = MockUserApi();
  final logger = newTestLogger();

  supplier() => UserRepositoryImpl(mockUserLocalStorage, mockUserApi, logger);

  group("findSavedSelf 는:", () {
    findSavedSelfTestCases(supplier, mockUserLocalStorage, mockUserApi);
  });

  group("getUser 는:", () {
    getUserTestCases(supplier, mockUserLocalStorage, mockUserApi);
  });

  group("register 는:", () {
    registerTestCases(supplier, mockUserLocalStorage, mockUserApi);
  });

  group("updateProfile 은:", () {
    updateProfileTestCases(supplier, mockUserLocalStorage, mockUserApi);
  });

  group("deregister 는:", () {
    deregisterTestCases(supplier, mockUserLocalStorage, mockUserApi);
  });

  tearDown(() {
    reset(mockUserLocalStorage);
    reset(mockUserApi);
  });
}
