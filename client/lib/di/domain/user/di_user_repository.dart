/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:logger/logger.dart';
import 'package:sirloin_sandbox_client/data/local/user/localstorage_user.dart';
import 'package:sirloin_sandbox_client/data/remote/http/user/api_user.dart';
import 'package:sirloin_sandbox_client/data/repository/user/repo_user_impl.dart';
import 'package:sirloin_sandbox_client/di/di_modules_helper.dart';
import 'package:sirloin_sandbox_client/domain/user/repository/repo_ro_user.dart';
import 'package:sirloin_sandbox_client/domain/user/repository/repo_user.dart';

class UserRepositoryModule {
  static const String _namespace = "domain.user";

  static Future registerComponents(final DiRuleHolder ruleHolder) async {
    final Logger logger = ruleHolder.getSingleton(Logger);
    final userLocalStorage = ruleHolder.getSingleton(UserLocalStorage);
    final userApi = ruleHolder.getSingleton(UserApi);

    final userRepositoryImpl = UserRepositoryImpl(userLocalStorage, userApi, logger);

    ruleHolder
        .withNamespace(_namespace)
        .registerSingleton(UserReadonlyRepository, () => userRepositoryImpl)
        .registerSingleton(UserRepository, () => userRepositoryImpl);
  }
}
