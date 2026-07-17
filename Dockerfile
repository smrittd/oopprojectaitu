FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app


COPY src/ ./src/


COPY lib/ ./lib/


RUN javac -cp "lib/*" -d bin src/*.java

EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-cp", "bin:lib/*", "Main"]