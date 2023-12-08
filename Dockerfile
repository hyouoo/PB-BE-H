# base image
FROM amazoncorretto:17
# 빌드 파일의 경로
ARG JAR_FILE=build/libs/*.jar
# 빌드 파일을 app.jar 컨테이너로 복사
COPY ${JAR_FILE} app.jar
# jar 파일 실행
# 생성된 이미지를 컨테이너로 실행하는 시점에 app.jar 실행
# Duser.timezone : 타임 존 지정
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/app.jar"]
