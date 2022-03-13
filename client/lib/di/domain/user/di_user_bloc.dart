/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:logger/logger.dart';
import 'package:sirloin_sandbox_client/di/di_modules_helper.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/deregister/bloc_deregister_user.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/get_profile/bloc_get_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/repository/repo_user.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/edit_profile/bloc_edit_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/register/bloc_register_user.dart';

class UserBlocModule {
  static const String _namespace = "bloc.user.registration";

  static Future registerComponents(final DiRuleHolder ruleHolder) async {
    final Logger logger = ruleHolder.getSingleton(Logger);
    final userRepository = ruleHolder.getSingleton(UserRepository);

    ruleHolder
        .withNamespace(_namespace)
        .registerPrototype(RegisterUserBloc, () => RegisterUserBloc(userRepository, logger))
        .registerPrototype(GetProfileBloc, () => GetProfileBloc(userRepository, logger))
        .registerPrototype(EditProfileBloc, () => EditProfileBloc(userRepository, logger))
        .registerPrototype(DeregisterUserBloc, () => DeregisterUserBloc(userRepository, logger));
  }
}
