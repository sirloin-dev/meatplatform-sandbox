/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:sirloin_sandbox_client/screen/bloc_sreen_splash.dart';
import 'package:sirloin_sandbox_client/screen/events_screen_splash.dart';
import 'package:sirloin_sandbox_client/screen/states_screen_splash.dart';
import 'package:sirloin_sandbox_client/screen/user/profile/screen_profile.dart';
import 'package:sirloin_sandbox_client/screen/user/screen_registration.dart';
import 'package:sirloin_sandbox_client/widget/extensions_build_context.dart';

/// Splash 에서는 앱의 최초 페이지를 보여주며,
/// 앱의 필요한 초기화 작업을 수행합니다.
class SplashScreen extends StatelessWidget {
  const SplashScreen({Key? key}) : super(key: key);

  @override
  Widget build(final BuildContext context) => Scaffold(
        appBar: AppBar(title: Text(context.i18n("title.splash"))),
        body: BlocProvider(
          create: (context) => SplashScreenBloc(),
          child: BlocBuilder<SplashScreenBloc, SplashScreenState>(
              builder: (ctx, state) => state.when(
                  initiatedState: (state) {
                    ctx.read<SplashScreenBloc>().add(ScreenReadyMessage());
                    return _onInitiated(ctx);
                  },
                  loadingState: (state) {
                    ctx.read<SplashScreenBloc>().add(InitProgrammeMessage());
                    return _onLoading(ctx);
                  },
                  savedUserNotFoundState: (state) => _onSavedUserNotFound(ctx),
                  savedUserFoundState: (state) => _onSavedUserFound(ctx, state))),
        ),
      );

  Widget _onInitiated(final BuildContext context) {
    return const Text("Splash");
  }

  Widget _onLoading(final BuildContext context) {
    return Center(
        child: Column(mainAxisAlignment: MainAxisAlignment.center, children: <Widget>[
      const CircularProgressIndicator(),
      const Padding(padding: EdgeInsets.all(8.0)),
      Text(context.i18n("text.loading")),
    ]));
  }

  Widget _onSavedUserNotFound(final BuildContext context) {
    return Container(
      color: Colors.white,
      child: Center(
        child: ElevatedButton(
          child: Text(context.i18n("splash.btn.register")),
          onPressed: () {
            Future.microtask(() => Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => RegistrationScreen.newInstance()),
                ));
          },
        ),
      ),
    );
  }

  Widget _onSavedUserFound(final BuildContext context, final SavedUserFoundState state) {
    Future.microtask(() => Navigator.pushAndRemoveUntil<void>(
          context,
          MaterialPageRoute<void>(builder: (BuildContext context) => MyProfileScreen.newInstance(state.user)),
          (_) => true,
        ));

    return const SizedBox.shrink();
  }

  static SplashScreen newInstance() {
    return const SplashScreen();
  }
}
