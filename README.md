# Kalah API Service

Kalah RESTful API service. It allows two players to play this game in a 6-Stone and 6-pit board.

## Compile and build artifact
    Gradle : ./gradlew build
    Test   : java  -jar build/libs/kalah-game-1.0.jar
    URL    : http://localhost:8080/healthCheck

### Test locally

To test this locally follow these steps:

- Install docker
- Install docker-compose
- cd docker
- docker-compose up
- mongo mongodb://127.0.0.1/admin --username=root --password=example docker/init.js 

With these we are ready to go
Enjoy!
