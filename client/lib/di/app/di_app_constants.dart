/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:sirloin_sandbox_client/di/di_modules_helper.dart';

class AppConstantsModule {
  static const String _namespace = "app";

  static const String qualifierBaseApiUrl = "BASE_API_URL";

  static Future registerComponents(final DiRuleHolder ruleHolder) async {
    ruleHolder
        .withNamespace(_namespace)
        .registerSingleton(String, () => "http://10.0.2.2:8080", qualifier: qualifierBaseApiUrl);
  }
}
