/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:logger/logger.dart';
import 'package:sirloin_sandbox_client/data/remote/http/debug_logging_http_client.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_client.dart';
import 'package:sirloin_sandbox_client/di/di_modules_helper.dart';

class AppComponentsModule {
  static const String _namespace = "app.components.internal";

  static Future registerComponents(final DiRuleHolder ruleHolder) async {
    final logger = Logger();

    ruleHolder.withNamespace(_namespace).registerSingleton(Logger, () => logger).registerPrototype(
        MtHttpClient,
        () => MtHttpClient.newInstance(factory: () {
              // POINT: Debug 모드에서만 이 httpClient 를 쓰고 싶습니다. 어떻게 해야 할까요?
              return DebugLoggingHttpClient(logger);
            }));
  }
}
