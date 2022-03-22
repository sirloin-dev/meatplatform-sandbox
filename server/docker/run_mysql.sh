#!/usr/bin/env bash
ROOT_PASSWORD="$1"
if [ "$ROOT_PASSWORD" == "" ]; then
  echo "sirloin-sandbox-server 프로젝트 실행을 위한 mysql docker 설정 스크립트입니다."
  echo "사용법: run_mysql.sh ROOT_PASSWORD"
  echo
  echo "docker 인스턴스에 사용할 root password 를 지정해 주세요."
  exit 1
fi

DOCKER_IMAGE="mysql:8.0.28"
ARCH=$(uname -m)
if [ "$ARCH" == "arm64" ]; then
  DOCKER_IMAGE="arm64v8/mysql:8.0.28-oracle"
fi

RDB_NAME="sirloin-sandbox-mysql"
RDB_HOST="localhost"
RDB_PORT=8306
RDB_PORT_LOCAL=3306
RDB_USER_ROOT="root"
RDB_DB_NAME="sirloin_sandbox"
TEST_IMAGE=$(docker ps -a --format "{{.Names}}" | grep "${RDB_NAME}")

# region Functions
run_mysql(){
   COMMAND=$1
   docker exec -it ${RDB_NAME} mysql -h $RDB_HOST -P ${RDB_PORT_LOCAL} --user=${RDB_USER_ROOT} --password=${ROOT_PASSWORD} -e "${COMMAND}" > /dev/null
}
# endregion

if [ -z "$TEST_IMAGE" ]; then
  docker run --name ${RDB_NAME} -e MYSQL_ROOT_PASSWORD=${ROOT_PASSWORD} -p ${RDB_PORT}:${RDB_PORT_LOCAL} -d $DOCKER_IMAGE > /dev/null
  echo "container 내의 mysqld 실행 완료시까지 대기합니다"

  WAIT_LOCK=1
  while [ $WAIT_LOCK -eq 1 ]
  do
      docker exec -it ${RDB_NAME} mysqladmin ping -h $RDB_HOST -P ${RDB_PORT_LOCAL} --user=${RDB_USER_ROOT} --password=${ROOT_PASSWORD} --silent > /dev/null
      WAIT_LOCK=$?
      sleep 2
  done
else
  # 테스트용 docker image 발견
  echo "${RDB_NAME} 컨테이너를 재실행합니다"
  docker restart $RDB_NAME > /dev/null
fi

# mysqld 시작 이후 socket open 까지 시간이 걸리므로 대기
# sock 파일 생성여부 확인 방식으로는 sock 파일 생성직후에도 mysql 클라이언트가 연결 못하는 케이스가 있어서 dummy connection 을 테스트
WAIT_LOCK=1
while [ $WAIT_LOCK -eq 1 ]
do
  run_mysql "SHOW DATABASES;"
  WAIT_LOCK=$?
  sleep 2
done

# 특정 프레임워크에서 mysql caching_sha2_password 방식을 지원하지 않는 경우가 있어 legacy 방식인
# mysql_native_password 로 암호를 강제 변경해준다.
# https://stackoverflow.com/questions/50373427/node-js-cant-authenticate-to-mysql-8-0
run_mysql "DROP DATABASE IF EXISTS ${RDB_DB_NAME};"
run_mysql "CREATE DATABASE ${RDB_DB_NAME};"
run_mysql "ALTER USER '${RDB_USER_ROOT}'@'%' IDENTIFIED WITH mysql_native_password BY '${RDB_PASSWORD}';"
run_mysql "FLUSH PRIVILEGES;"

echo "${RDB_NAME} 컨테이너 실행 완료. Local database 에 여전히 접근할 수 없다면 이 스크립트를 한번 더 실행해주세요."
exit 0
