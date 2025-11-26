# 노드 이미지
FROM eclipse-temurin:21-jre-alpine

# 작업 디렉토리 /app으로 설정
WORKDIR /app

# JAR 파일 컨테이너에 카피
COPY build/libs/*.jar app.jar

# 8080 포트 사용
EXPOSE 8080

# 앱 실행 명령
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]