#!/bin/bash

#svn 업데이트
#svn update

#메이븐 빌드
#mvn clean package -Dmaven.test.skip=true

#톰캣 정지
systemctl stop tomcat

#톰캣의 ROOT 폴더 삭제
rm -rf /home/tomcat/webapps/ROOT

#빌드된 폴더 톰캣의 webapps 경로에 ROOT이름으로 폴더 생성
mv target/my-1.0.0-BUILD-SNAPSHOT /home/tomcat/webapps/ROOT

#톰캣 재시작
systemctl start tomcat

#로그 출력
tail -500f /home/tomcat/logs/catalina.out