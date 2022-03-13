/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter/material.dart';
import 'package:flutter_i18n/flutter_i18n.dart';

extension FlutterI18nContextExtension on BuildContext {
  String i18n(final String key) {
    return FlutterI18n.translate(this, key);
  }
}
