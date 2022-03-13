/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter/material.dart';
import 'package:flutter_i18n/flutter_i18n.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:sirloin_sandbox_client/screen/screen_splash.dart';

class ClientApplication extends StatelessWidget {
  final FlutterI18nDelegate flutterI18nDelegate;

  const ClientApplication({Key? key, required this.flutterI18nDelegate}) : super(key: key);

  @override
  Widget build(final BuildContext context) {
    return MaterialApp(
      // POINT 여기 App 이름을 Locale 에 맞게 바꾸려면 어떻게 해야 할까요?
      title: "sirloin-sandbox-client",
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const SplashScreen(),
      localizationsDelegates: [
        flutterI18nDelegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate
      ],
      builder: FlutterI18n.rootAppBuilder(),
    );
  }
}
