/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:sirloin_sandbox_client/di/app/di_app_constants.dart';
import 'package:sirloin_sandbox_client/di/di_modules_helper.dart';
import 'package:sirloin_sandbox_client/di/domain/user/di_user_data.dart';
import 'package:sirloin_sandbox_client/di/domain/user/di_user_repository.dart';
import 'package:sirloin_sandbox_client/di/domain/user/di_user_bloc.dart';

import 'app/di_app_components.dart';

/// Dependency Injection 은 쉽게 익히기 어려운 개념이고,
/// Flutter 에서 이를 제대로 익히려면 Widget state 를 이해하고 있어야 합니다.
/// 또한 Dart 의 factory, singleton, equality 개념과 더불어,
/// 일반적인 프로그래밍 기법인 lazy initialisation, proxy generation 등도 이해해야 합니다.
///
/// 이런 개념들을 단기간에 모두 익히기는 어렵기 때문에 모든 DI 를 손으로 하도록 구현합니다.
/// 물론 손 DI 로 인해 의존성을 런타임에 일일히 확인해야 하는 수고가 필요하며,
/// 프로그램 규모가 커질 수록 이 부담은 점점 가중되게 됩니다.
///
/// 이 로직을 분석하면서 Object 생성이 얼마나 어려운 일인지를 직접 체험해 보시기 바랍니다.
///
/// @since 2022-02-14
class ClientApplicationDi {
  ClientApplicationDi._privateConstructor();

  static final ClientApplicationDi _instance = ClientApplicationDi._privateConstructor();

  static ClientApplicationDi get instance => _instance;

  final DiRuleHolder _ruleHolder = DiRuleHolder.newInstance();

  /*
   * POINT: 이 method 를 호출하면 모든 Object 를 한번에 로드합니다. lazy 하게 object 를
   * 제공하고 싶다면 어떻게 수정하는게 좋을까요?
   */
  Future init() async {
    _ruleHolder.reset();
    await _registerComponents(_ruleHolder);

    if (_ruleHolder.errors().isNotEmpty) {
      _ruleHolder.errors().forEach((it) {
        // 개발 모드에서 사용할 debug print 입니다.
        // ignore: avoid_print
        print(it);
      });

      throw AssertionError("DI initialisation failed. Fix this error and try again");
    }
  }

  T singleton<T>(final Type type) {
    return _ruleHolder.getSingleton(type);
  }

  T prototype<T>(final Type type) {
    return _ruleHolder.getPrototype(type);
  }

  Future<void> _registerComponents(final DiRuleHolder ruleHolder) async {
    // DI Module: App components
    await AppConstantsModule.registerComponents(ruleHolder);
    await AppComponentsModule.registerComponents(ruleHolder);

    // DI Module: Domain
    await UserDataModule.registerComponents(ruleHolder);
    await UserRepositoryModule.registerComponents(ruleHolder);

    // DI Module: BLoC
    await UserBlocModule.registerComponents(ruleHolder);
  }
}
