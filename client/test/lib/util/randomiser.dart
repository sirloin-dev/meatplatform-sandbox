/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'dart:math';

extension IterableExtension<T> on Iterable<T> {
  T random() {
    final randomIndex = Random().nextInt(length);
    final iterator = this.iterator;

    int iterations = 0;
    while (iterator.moveNext()) {
      if (iterations == randomIndex) {
        return iterator.current;
      }
      ++iterations;
    }

    return iterator.current;
  }
}
