/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:logger/logger.dart';

Logger newTestLogger() {
  return Logger(printer: SimplePrinter());
}
