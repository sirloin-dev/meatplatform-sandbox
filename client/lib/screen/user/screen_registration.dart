/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:sirloin_sandbox_client/di/di_modules.dart';
import 'package:sirloin_sandbox_client/screen/user/screen_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/register/bloc_register_user.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/register/state_register_user.dart';
import 'package:sirloin_sandbox_client/widget/dialog/alert_dialog_exceptions.dart';
import 'package:sirloin_sandbox_client/widget/extensions_build_context.dart';

class RegistrationScreen extends StatelessWidget {
  final RegisterUserBloc _bloc;

  const RegistrationScreen(final this._bloc, {Key? key}) : super(key: key);

  @override
  Widget build(final BuildContext context) => Scaffold(
      appBar: AppBar(title: Text(context.i18n("title.registration"))),
      body: BlocProvider(
          create: (context) => _bloc,
          child: BlocBuilder<RegisterUserBloc, RegisterUserBlocState>(
            builder: (ctx, state) => state.when(
                initialState: (state) => _onInitialState(ctx),
                apiErrorState: (state) => _onApiError(ctx, state),
                userRegisteredState: (state) => _onUserRegistered(ctx, state)),
          )));

  // POINT: 이 구현은 이용자 편이성이 매우 떨어집니다. 어떻게 개선할 수 있을까요?
  Widget _onInitialState(final BuildContext context) {
    final nicknameControl = TextEditingController();
    final profileImageControl = TextEditingController();

    return Padding(
        padding: const EdgeInsets.symmetric(horizontal: 8),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            TextFormField(
              autofocus: true,
              controller: nicknameControl,
              decoration: InputDecoration(
                border: const UnderlineInputBorder(),
                labelText: context.i18n("registration.text.nickname"),
                hintText: context.i18n("registration.text.nicknameHint"),
              ),
            ),
            TextFormField(
              autofocus: true,
              controller: profileImageControl,
              decoration: InputDecoration(
                border: const UnderlineInputBorder(),
                labelText: context.i18n("registration.text.profileImageUrl"),
              ),
            ),
            Center(
                child: ElevatedButton(
                    child: Text(context.i18n("registration.btn.register")),
                    onPressed: () => _bloc.registerUser(nicknameControl.text, profileImageControl.text)))
          ],
        ));
  }

  // POINT: 오류 발생시 UI 에 어떤 문제가 있을까요?
  Widget _onApiError(final BuildContext context, final ApiErrorState state) {
    showExceptionAlertDialog(context, state.exception);

    return _onInitialState(context);
  }

  Widget _onUserRegistered(final BuildContext context, final UserRegisteredState state) {
    Future.microtask(() => Navigator.pushAndRemoveUntil<void>(
          context,
          MaterialPageRoute<void>(builder: (BuildContext context) => MyProfileScreen.newInstance(state.user)),
          (_) => true,
        ));

    return const SizedBox.shrink();
  }

  static RegistrationScreen newInstance() {
    final registrationScreenBloc = ClientApplicationDi.instance.prototype(RegisterUserBloc);

    return RegistrationScreen(registrationScreenBloc);
  }
}
