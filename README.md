# jpa-query-tests

Performance tests of different kind queries

Environment:

- Oracle JDK 11.0.9
- MariaDB 8.0.27

```
docker run --name mysql -e MYSQL_ROOT_PASSWORD=test -p 3306:3306 -d mysql:8.0.27 --character-set-server=utf8 --collation-server=utf8_general_ci
```

(To observe database can be useful command `alter user 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'test';`)

### Build

1. Checkout and build modified Hibernate:

```
git clone https://github.com/bvfalcon/hibernate-orm.git
cd hibernate-orm
git checkout jpa-query-tests
gradlew clean build publishToMavenLocal -x test -x checkstyleMain
```

2. Build project `mvn clean package assembly:single && mvn assembly:single -Dtest`

### Run

1. Fill database with test records: `java -cp target\jpa-query-tests-TRUE-jar-with-dependencies.jar name.bychkov.jee.FillDatabase`

1. Execute tests with real database queries:  `java -Dorg.jboss.logging.provider=slf4j -Dorg.slf4j.simpleLogger.defaultLogLevel=error -jar target\jpa-query-tests-TRUE-jar-with-dependencies.jar`

1. Execute tests with fake database queries:  `java -Dorg.jboss.logging.provider=slf4j -Dorg.slf4j.simpleLogger.defaultLogLevel=error -jar target\jpa-query-tests-TEST-jar-with-dependencies.jar`
