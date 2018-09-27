1. Run datrabase -  docker run --name some-mongo -p 27017:27017 -d mongo:latest
2. Build app - ./gradlew build
3. Run app - java -jar ./api/build/libs/api-0.0.1-SNAPSHOT.jar