/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:tuple/tuple.dart';

enum InstanceType { singleton, prototype }

typedef TypeToString = String Function(Type);
typedef InstanceProvider<T> = T Function();

abstract class DiRuleHolder {
  void putDefinition<T>(final InstanceType instanceType, final Type type, final InstanceProvider<T> instanceProvider,
      [final TypeToString? typeToKeyConverter, final String optionalQualifier = ""]);

  T getSingleton<T>(final Type type, [final String optionalQualifier = ""]);

  T getPrototype<T>(final Type type, [final String optionalQualifier = ""]);

  List<Error> errors();

  void reset();

  static DiRuleHolder newInstance() {
    return _DiRuleHolderImpl();
  }
}

class _DiRuleHolderImpl implements DiRuleHolder {
  final Map<String, Object> _singletons = {};
  final Map<String, InstanceProvider<Object>> _prototypes = {};

  /*
   * POINT: Tuple2 의 구현을 상세히 살펴보시기 바랍니다. 내부에서
   * == 와 hashCode 를 override 한 이유가 무엇일까요?
   */
  final Map<Tuple2<Type, String>, String> _typeAsKey = {};

  final List<Error> _errors = [];

  @override
  void putDefinition<T>(final InstanceType instanceType, final Type type, final InstanceProvider<T> instanceProvider,
      [final TypeToString? typeToKeyConverter, final String optionalQualifier = ""]) {
    final String rawTypeKey;
    if (typeToKeyConverter == null) {
      rawTypeKey = type.toString();
    } else {
      rawTypeKey = typeToKeyConverter(type);
    }

    _ensureTypeAndQualifierIsUnique(type, rawTypeKey, optionalQualifier);
    final key = _getTypeKeyWithQualifier(type, optionalQualifier);

    switch (instanceType) {
      case InstanceType.singleton:
        {
          final instance = instanceProvider.call();

          if (_singletons.containsKey(key)) {
            _errors.add(ArgumentError("Key '$key' -> $instance Singleton generation rule "
                "is already registered"));
          } else {
            _singletons[key] = instance as Object;
          }
          break;
        }
      case InstanceType.prototype:
        {
          if (_prototypes.containsKey(key)) {
            _errors.add(ArgumentError("'$key' Prototype generation rule is already registered"));
          } else {
            _prototypes[key] = instanceProvider as InstanceProvider<Object>;
          }
        }
        break;
    }
  }

  @override
  T getSingleton<T>(final Type type, [final String optionalQualifier = ""]) {
    final typeAsKey = _getTypeKeyWithQualifier(type, optionalQualifier);
    final instance = _singletons[typeAsKey];
    if (instance == null) {
      throw ArgumentError("No singleton instance is found for Type '$type'");
    }

    return instance as T;
  }

  @override
  T getPrototype<T>(final Type type, [final String optionalQualifier = ""]) {
    final typeAsKey = _getTypeKeyWithQualifier(type, optionalQualifier);
    final prototype = _prototypes[typeAsKey];
    if (prototype == null) {
      throw ArgumentError("No prototype instance is found for Type '$type'");
    }

    return prototype.call() as T;
  }

  @override
  List<Error> errors() => _errors;

  @override
  void reset() {
    _singletons.clear();
    _typeAsKey.clear();
    _errors.clear();
  }

  void _ensureTypeAndQualifierIsUnique(final Type type, final String typeAsString, final String optionalQualifier) {
    final String typeStringWithQualifier;
    if (optionalQualifier.isEmpty) {
      typeStringWithQualifier = typeAsString;
    } else {
      typeStringWithQualifier = "$optionalQualifier::$typeAsString";
    }

    final typeDefinition = Tuple2(type, optionalQualifier);
    if (_typeAsKey.containsKey(typeDefinition)) {
      _errors.add(ArgumentError("Type '$type' -> '$typeStringWithQualifier' rule "
          "is already registered"));
    } else {
      _typeAsKey[typeDefinition] = typeStringWithQualifier;
    }
  }

  String _getTypeKeyWithQualifier(final Type type, final String optionalQualifier) {
    final typeAsKey = _typeAsKey[Tuple2(type, optionalQualifier)];
    if (typeAsKey == null) {
      throw ArgumentError("No type to key conversion rule for $type("
          "qualifier=$optionalQualifier) is found.");
    }
    return typeAsKey;
  }
}

extension DiRuleHolderRegistrar on DiRuleHolder {
  _DiRuleHolderBuilder withNamespace(final String namespace) {
    return _DiRuleHolderBuilder(this, namespace);
  }
}

class _DiRuleHolderBuilder {
  final DiRuleHolder _ruleHolder;
  final String _namespace;

  _DiRuleHolderBuilder(this._ruleHolder, this._namespace);

  _DiRuleHolderBuilder registerSingleton<T>(final Type type, final InstanceProvider<T> instanceProvider,
      {final String qualifier = ""}) {
    return _register(InstanceType.singleton, type, instanceProvider, optionalQualifier: qualifier);
  }

  _DiRuleHolderBuilder registerPrototype<T>(final Type type, final InstanceProvider<T> instanceProvider,
      {final String optionalQualifier = ""}) {
    return _register(InstanceType.prototype, type, instanceProvider, optionalQualifier: optionalQualifier);
  }

  _DiRuleHolderBuilder _register<T>(
      final InstanceType instanceType, final Type type, final InstanceProvider<T> instanceProvider,
      {final String optionalQualifier = ""}) {
    _ruleHolder.putDefinition(instanceType, type, instanceProvider, (_) {
      if (_namespace.isEmpty) {
        return "$type";
      } else {
        return "$_namespace.$type";
      }
    }, optionalQualifier);

    return this;
  }
}
