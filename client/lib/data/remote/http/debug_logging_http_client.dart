/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:logger/logger.dart';

class DebugLoggingHttpClient extends http.BaseClient {
  final Logger _logger;

  DebugLoggingHttpClient(this._logger);

  @override
  Future<http.Response> head(final Uri url, {final Map<String, String>? headers}) =>
      _logResponse(() => http.Client().head(url, headers: headers));

  @override
  Future<http.Response> get(final Uri url, {final Map<String, String>? headers}) =>
      _logResponse(() => http.Client().get(url, headers: headers));

  @override
  Future<http.Response> post(final Uri url,
          {final Map<String, String>? headers, final Object? body, final Encoding? encoding}) =>
      _logResponse(() => http.Client().post(url, headers: headers, body: body, encoding: encoding));

  @override
  Future<http.Response> put(final Uri url,
          {final Map<String, String>? headers, final Object? body, final Encoding? encoding}) =>
      _logResponse(() => http.Client().put(url, headers: headers, body: body, encoding: encoding));

  @override
  Future<http.Response> patch(final Uri url,
          {final Map<String, String>? headers, final Object? body, final Encoding? encoding}) =>
      _logResponse(() => http.Client().patch(url, headers: headers, body: body, encoding: encoding));

  @override
  Future<http.Response> delete(final Uri url,
          {final Map<String, String>? headers, final Object? body, final Encoding? encoding}) =>
      _logResponse(() => http.Client().delete(url, headers: headers, body: body, encoding: encoding));

  @override
  Future<http.StreamedResponse> send(final http.BaseRequest request) {
    _logger.d("HTTP ${request.method}");
    request.headers.forEach((k, v) {
      _logger.d("$k : $v");
    });

    return http.Client().send(request);
  }

  Future<http.Response> _logResponse(final Future<http.Response> Function() operation) async {
    final response = await operation();
    _logger.d("HTTP ${response.statusCode} ${response.reasonPhrase}");
    response.headers.forEach((k, v) {
      _logger.d("$k : $v");
    });
    _logger.d(response.body);
    return response;
  }
}
