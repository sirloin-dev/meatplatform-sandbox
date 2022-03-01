/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:http/http.dart' as http;
import 'package:sirloin_sandbox_client/data/remote/http/common/dto/response_envelope.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/dto/response_error.dart';
import 'package:sirloin_sandbox_client/data/remote/http/common/json_serializable.dart';
import 'package:sirloin_sandbox_client/data/remote/http/http_exceptions.dart';

typedef MtHttpClientFactory = http.Client Function();

abstract class MtHttpClient {
  Future<String> get(final String url);

  Future<String> post(final String url, final JsonSerializable payload);

  Future<String> patch(final String url, final JsonSerializable payload);

  Future<String> delete(final String url);

  static const Map<String, String> defaultHeaders = {
    "Content-type": "application/json",
    "Accept": "application/json",
  };

  static MtHttpClient newInstance({final MtHttpClientFactory? factory}) {
    final MtHttpClientFactory factoryImpl;
    if (factory == null) {
      factoryImpl = () => http.Client();
    } else {
      factoryImpl = factory;
    }

    return _MtHttpClientImpl(factoryImpl);
  }
}

class _MtHttpClientImpl implements MtHttpClient {
  final MtHttpClientFactory _factory;

  _MtHttpClientImpl(this._factory);

  @override
  Future<String> get(final String url) async {
    final uri = Uri.parse(url);
    final client = _factory.call();

    try {
      return _processResponse(uri, () => client.get(uri, headers: MtHttpClient.defaultHeaders));
    } finally {
      client.close();
    }
  }

  @override
  Future<String> post(final String url, final JsonSerializable payload) async {
    final uri = Uri.parse(url);
    final client = _factory.call();

    try {
      return _processResponse(
          uri, () => client.post(uri, body: payload.toJsonString(), headers: MtHttpClient.defaultHeaders));
    } finally {
      client.close();
    }
  }

  @override
  Future<String> patch(final String url, final JsonSerializable payload) async {
    final uri = Uri.parse(url);
    final client = _factory.call();

    try {
      return _processResponse(
          uri, () => client.patch(uri, body: payload.toJsonString(), headers: MtHttpClient.defaultHeaders));
    } finally {
      client.close();
    }
  }

  @override
  Future<String> delete(final String url) async {
    final uri = Uri.parse(url);
    final client = _factory.call();

    try {
      return _processResponse(uri, () => client.delete(uri, headers: MtHttpClient.defaultHeaders));
    } finally {
      client.close();
    }
  }

  Future<String> _processResponse(final Uri uri, final Future<http.Response> Function() responseProducer) async {
    final response = await responseProducer.call();

    if (response.statusCode == 200) {
      return response.body;
    }

    try {
      final ResponseEnvelope<Map<String, dynamic>> envelope = ResponseEnvelope.fromJson(response.body);
      final ErrorResponse errorResponse = ErrorResponse.fromJson(envelope.body);

      throw UnexpectedResponseException(errorResponse.message, errorResponse.code);
    } on JsonParseException {
      throw HttpStatusCodeException(response.body, uri, response.statusCode);
    }
  }
}
