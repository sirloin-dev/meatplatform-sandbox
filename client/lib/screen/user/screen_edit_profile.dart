/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_i18n/widgets/I18nText.dart';
import 'package:sirloin_sandbox_client/di/di_modules.dart';
import 'package:sirloin_sandbox_client/domain/user/user.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/edit_profile/bloc_edit_profile.dart';
import 'package:sirloin_sandbox_client/domain/user/bloc/edit_profile/state_edit_profile.dart';
import 'package:sirloin_sandbox_client/screen/user/screen_profile.dart';
import 'package:sirloin_sandbox_client/widget/dialog/alert_dialog_exceptions.dart';
import 'package:sirloin_sandbox_client/widget/extensions_build_context.dart';

class EditProfileScreen extends StatelessWidget {
  final EditProfileBloc _bloc;
  final nicknameControl = TextEditingController();
  final profileImageControl = TextEditingController();

  EditProfileScreen(final this._bloc, final User user, {Key? key}) : super(key: key) {
    _bloc.user = user;
  }

  @override
  Widget build(final BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: Text(context.i18n("title.editProfile"))),
        body: BlocProvider(
            create: (context) => _bloc,
            child: BlocBuilder<EditProfileBloc, EditProfileBlocState>(
                builder: (ctx, state) => state.when(
                      initialState: (state) => _onInitialState(context),
                      apiErrorState: (state) => _onApiError(ctx, state),
                      userDataSetState: (state) => _onUserDataSetState(context, state),
                    ))));
  }

  Widget _onInitialState(final BuildContext context) {
    return const SizedBox.shrink();
  }

  Widget _onUserDataSetState(final BuildContext context, final UserDataSetState state) {
    final user = state.user;
    nicknameControl.text = user.nickname;
    profileImageControl.text = user.profileImageUrl;

    if (state.isUpToDate) {
      Future.microtask(() => Navigator.pushAndRemoveUntil<void>(
        context,
        MaterialPageRoute<void>(builder: (BuildContext context) => MyProfileScreen.newInstance(state.user)),
            (_) => true,
      ));
    }

    return Padding(
        padding: const EdgeInsets.symmetric(horizontal: 8),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: <Widget>[
          I18nText("profile.text.userId", translationParams: {"userId": user.uuid}),
          TextFormField(
            autofocus: true,
            controller: nicknameControl,
            decoration: InputDecoration(
              border: const UnderlineInputBorder(),
              labelText: context.i18n("profile.editText.nickname"),
              hintText: context.i18n("profile.editText.nicknameHint"),
            ),
          ),
          TextFormField(
            autofocus: true,
            controller: profileImageControl,
            decoration: InputDecoration(
              border: const UnderlineInputBorder(),
              labelText: context.i18n("profile.editText.profileImageUrl"),
            ),
          ),
          Center(
              child: ElevatedButton(
                  child: Text(context.i18n("profile.btn.editProfile")),
                  onPressed: () => _bloc.updateProfile(nicknameControl.text, profileImageControl.text)))
        ]));
  }

  // POINT: 오류 발생시 UI 에 어떤 문제가 있을까요?
  Widget _onApiError(final BuildContext context, final ApiErrorState state) {
    showExceptionAlertDialog(context, state.exception);
    return const SizedBox.shrink();
  }

  static EditProfileScreen newInstance(final User user) {
    final editProfileScreenBloc = ClientApplicationDi.instance.prototype(EditProfileBloc);

    return EditProfileScreen(editProfileScreenBloc, user);
  }
}
