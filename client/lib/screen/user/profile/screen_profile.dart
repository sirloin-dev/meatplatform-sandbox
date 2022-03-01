/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_i18n/flutter_i18n.dart';
import 'package:sirloin_sandbox_client/di/di_modules.dart';
import 'package:sirloin_sandbox_client/domain/base_bloc_state.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/deregister/bloc_deregister_user.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/deregister/state_deregister_user.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/get_profile/bloc_get_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/get_profile/state_get_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';
import 'package:sirloin_sandbox_client/screen/screen_splash.dart';
import 'package:sirloin_sandbox_client/screen/user/screen_edit_profile.dart';
import 'package:sirloin_sandbox_client/widget/dialog/alert_dialog_exceptions.dart';
import 'package:sirloin_sandbox_client/widget/extensions_build_context.dart';

class MyProfileScreen extends StatelessWidget {
  final GetProfileBloc _getProfileBloc;
  final DeregisterUserBloc _deregisterUserBloc;

  MyProfileScreen(final this._getProfileBloc, final this._deregisterUserBloc, final User user, {Key? key})
      : super(key: key) {
    _getProfileBloc.user = user;
  }

  User get currentUser => _getProfileBloc.user;

  set currentUser(final User value) => _getProfileBloc.user = value;

  @override
  Widget build(final BuildContext context) => Scaffold(
        appBar: AppBar(title: Text(context.i18n("title.myProfile"))),
        body: MultiBlocProvider(
          providers: [
            BlocProvider(
              create: (context) => _getProfileBloc,
              child: BlocBuilder<GetProfileBloc, GetProfileBlocState>(
                builder: (ctx, state) => state.when(
                  initialState: (state) => _onInitialState(ctx),
                  apiErrorState: (state) => _onApiError(ctx, state),
                  userDataSetState: (state) => _onUserDataSetState(ctx, state),
                ),
              ),
            ),
            BlocProvider(
              create: (context) => _deregisterUserBloc,
              child: BlocBuilder<DeregisterUserBloc, DeregisterUserBlocState>(
                builder: (ctx, state) => state.when(
                  initialState: (state) => _onInitialState(ctx),
                  apiErrorState: (state) => _onApiError(ctx, state),
                  userDeregisteredState: (state) => _onDeregisteredState(ctx),
                ),
              ),
            )
          ],
          child: const SizedBox.shrink(),
        ),
      );

  Widget _onInitialState(final BuildContext context) {
    return const SizedBox.shrink();
  }

  Widget _onUserDataSetState(final BuildContext context, final UserDataSetState state) {
    final user = state.user;

    return Padding(
        padding: const EdgeInsets.symmetric(horizontal: 8),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: <Widget>[
          I18nText("profile.text.userId", translationParams: {"userId": user.uuid}),
          I18nText("profile.text.nickname", translationParams: {"nickname": user.nickname}),
          I18nText("profile.text.profileImageUrl", translationParams: {"profileImageUrl": user.profileImageUrl}),
          Center(
              child: Row(
            children: <Widget>[
              ElevatedButton(
                  child: Text(context.i18n("profile.btn.editProfile")), onPressed: () => _onClickEditProfile(context)),
              ElevatedButton(
                  child: Text(context.i18n("profile.btn.deregister")),
                  onPressed: () => _onClickDeregister(context),
                  style: ElevatedButton.styleFrom(primary: Colors.red))
            ],
          ))
        ]));
  }

  Widget _onDeregisteredState(final BuildContext context) {
    Future.microtask(() => Navigator.pushAndRemoveUntil<void>(
          context,
          MaterialPageRoute<void>(builder: (context) => SplashScreen.newInstance()),
          (_) => true,
        ));

    return const SizedBox.shrink();
  }

  // POINT: 오류 발생시 UI 에 어떤 문제가 있을까요?
  Widget _onApiError(final BuildContext context, final BaseExceptionalState state) {
    showExceptionAlertDialog(context, state.exception);
    return const SizedBox.shrink();
  }

  void _onClickEditProfile(final BuildContext context) {
    Future.microtask(() => Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => EditProfileScreen.newInstance(currentUser)),
        ));
  }

  // POINT: 탈퇴 API 호출에 시간이 오래 걸릴 경우 어떻게 해 줘야 할까요?
  void _onClickDeregister(final BuildContext context) {
    final cancelButton = TextButton(
      child: Text(context.i18n("text.btn.cancel")),
      onPressed: () => Navigator.pop(context),
    );

    final deregisterButton = TextButton(
        child: Text(context.i18n("deregister.btn.deregister")),
        onPressed: () => _deregisterUserBloc.deregister(currentUser.uuid),
        style: TextButton.styleFrom(primary: Colors.red));

    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (context) => AlertDialog(
                title: Text(context.i18n("deregister.title")),
                content: Text(context.i18n("deregister.message")),
                actions: <Widget>[
                  cancelButton,
                  deregisterButton,
                ]));
  }

  static MyProfileScreen newInstance(final User user) {
    final getProfileBloc = ClientApplicationDi.instance.prototype(GetProfileBloc);
    final deregisterUserBloc = ClientApplicationDi.instance.prototype(DeregisterUserBloc);

    return MyProfileScreen(getProfileBloc, deregisterUserBloc, user);
  }
}
