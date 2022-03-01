/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter/material.dart';
import 'package:flutter_i18n/flutter_i18n.dart';
import 'package:sirloin_sandbox_client/client_application.dart';

Future main() async {
  final flutterI18nDelegate = FlutterI18nDelegate(
    translationLoader: FileTranslationLoader(
      useCountryCode: true,
      fallbackFile: "en_us",
      basePath: "assets/i18n",
    ),
  );
  WidgetsFlutterBinding.ensureInitialized();
  runApp(ClientApplication(flutterI18nDelegate: flutterI18nDelegate));
}
