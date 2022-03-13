/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:localstorage/localstorage.dart';
import 'package:logger/logger.dart';
import 'package:sirloin_sandbox_client/data/local/user/localstorage_user.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_client.dart';
import 'package:sirloin_sandbox_client/data/remote/http/user/api_user.dart';
import 'package:sirloin_sandbox_client/di/app/di_app_constants.dart';
import 'package:sirloin_sandbox_client/di/di_modules_helper.dart';

class UserDataModule {
  static const String _namespace = "data.user";
  static const String _userLocalStorage = "local_users";

  static Future registerComponents(final DiRuleHolder ruleHolder) async {
    final logger = ruleHolder.getSingleton(Logger);
    final baseApiUrl = ruleHolder.getSingleton(String, AppConstantsModule.qualifierBaseApiUrl);
    final httpClient = ruleHolder.getPrototype(MtHttpClient);

    ruleHolder
        .withNamespace(_namespace)
        .registerSingleton(UserApi, () => UserApi.newInstance("$baseApiUrl/v1/user", httpClient))
        .registerSingleton(
            UserLocalStorage, () => UserLocalStorage.newInstance(LocalStorage(_userLocalStorage), logger));
  }
}
