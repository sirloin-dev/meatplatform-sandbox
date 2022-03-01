/*
 * sirloin-sandbox-client
 * Distributed under CC BY-NC-SA
 */
import 'package:flutter/material.dart';
import 'package:flutter_i18n/widgets/I18nText.dart';
import 'package:sirloin_sandbox_client/widget/extensions_build_context.dart';

/// Dialog 또한 Widget 의 sub tree 이므로 dialog 를 그리기 위해서는 호출 지점에서
/// dialog 를 그리는 상태라는 것을 알려야 (setState()) 한다.
///
/// 그리고 Flutter 의 규칙에 따라 setState 는 widget build 완료 이후에만 호출할 수 있다.
/// 즉, Future 를 이용하지 않는 UI building 과정에서 이 함수 호출시 문제가 발생한다.
///
/// 따라서 문제를 해결하기 위해 UI context 에서 직접 [isImmediate] 값을 결정할 수 있도록 한다.
///
/// See also: https://api.flutter.dev/flutter/material/AlertDialog/build.html
bool showExceptionAlertDialog(final BuildContext context, final Exception? exception,
    {final bool isImmediate = false}) {
  final Widget content;
  if (exception == null) {
    content = Text(context.i18n("error.common.message"));
  } else {
    content = I18nText("error.common.messageWithFormat", translationParams: {"message": exception.toString()});
  }

  showAlertDialog() {
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (context) =>
            AlertDialog(title: Text(context.i18n("registration.error")), content: content, actions: <Widget>[
              TextButton(
                child: Text(context.i18n("text.btn.confirm")),
                onPressed: () => Navigator.pop(context),
              )
            ]));
  }

  if (isImmediate) {
    showAlertDialog();
    return true;
  }

  // UI context 사라진 이후 호출될 수 있기 때문에 검사 수행
  final binding = WidgetsBinding.instance;
  if (binding == null) {
    return false;
  }

  binding.addPostFrameCallback((_) => showAlertDialog());
  return true;
}
