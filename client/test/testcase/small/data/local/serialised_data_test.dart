/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter_test/flutter_test.dart';
import 'package:sirloin_sandbox_client/data/local/seraialised_data.dart';

void main() {
  test("Expiration 을 초과한 SerialisedData 는 expired = true 다.", () {
    // given:
    final savedAt = DateTime.now().subtract(const Duration(seconds: SerialisedData.defaultExpiredSeconds + 1));
    final data = SerialisedDataImpl(savedAt);

    // then:
    final isExpired = data.isExpired();

    // expect:
    expect(isExpired, equals(true));
  });

  test("Expiration 을 초과하지 않은 SerialisedData 는 expired = false 다.", () {
    // given:
    final savedAt = DateTime.now();
    final data = SerialisedDataImpl(savedAt);

    // then:
    final isExpired = data.isExpired();

    // expect:
    expect(isExpired, equals(false));
  });
}

class SerialisedDataImpl with SerialisedData<SerialisedDataImpl> {
  SerialisedDataImpl(final DateTime savedAt) : super() {
    super.savedAt = savedAt;
  }

  @override
  SerialisedDataImpl get() {
    return this;
  }
}
