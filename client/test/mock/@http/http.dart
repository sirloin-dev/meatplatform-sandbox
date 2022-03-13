/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:http/http.dart' as http;
import 'package:mockito/annotations.dart';

@GenerateMocks([HttpClient])
class HttpGenerator {}

// 'Client' 라는 이름은 너무 일반적이라 Name conflict 피하기 위해서 'HttpClient' 라는 이름으로 변경
abstract class HttpClient extends http.BaseClient {}
