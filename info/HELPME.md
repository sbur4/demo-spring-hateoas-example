1. Docker:
- image-> mariadb:latest
 port-> 3036:3036
  MARIADB_ROOT_PASSWORD=password12@

2. How to run:
   + Application run as debug
   - gradle build -x test
   - in git bash ./gradlew build -x test

3. GET http://localhost:8080/users/1
4. GET http://localhost:8080/users
5. POST http://localhost:8080/users/user/name
6. PUT http://localhost:8080/users/user/1?name=tester
7. DELETE http://localhost:8080/users/user/3

