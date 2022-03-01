import 'package:mockito/annotations.dart';
import 'package:sirloin_sandbox_client/domain/user/repository/repo_ro_user.dart';
import 'package:sirloin_sandbox_client/domain/user/repository/repo_user.dart';

@GenerateMocks([UserReadonlyRepository])
class UserReadonlyRepositoryMockGenerator {}

@GenerateMocks([UserRepository])
class UserRepositoryMockGenerator {}
