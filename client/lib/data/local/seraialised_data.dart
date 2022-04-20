/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */

// POINT: mixin 과 abstract class(extends), interface(implements) 의 차이가 뭘까요?
mixin SerialisedData<T> {
  static const int defaultExpiredSeconds = 5 * 60;

  /// 반드시 Constructor 에서 초기화해야 합니다.
  late final DateTime savedAt;

  // POINT: expiration check 를 외부에서 주입받는게 나을까요, 여기서 구현하는게 좋을까요?
  // opinion: 외부에서 주입받는게 낫다. 왜? static으로 기본값을 정해둘 수 있는 상황이 아니기 때문이다.
  bool isExpired({final int expirationSeconds = defaultExpiredSeconds}) {
    final now = DateTime.now();
    final then = savedAt;

    return now.difference(then).inSeconds > expirationSeconds;
  }

  T get();
}
