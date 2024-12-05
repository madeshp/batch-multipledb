// pom.xml dependencies
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-batch</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.oracle.database.jdbc</groupId>
            <artifactId>ojdbc8</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.2.3</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
</project>

// application.yml
spring:
  batch:
    job:
      enabled: false
    jdbc:
      initialize-schema: always
  
  datasource:
    primary:
      jdbc-url: jdbc:oracle:thin:@localhost:1521/db1
      username: user1
      password: pass1
      driver-class-name: oracle.jdbc.OracleDriver
    
    secondary:
      jdbc-url: jdbc:oracle:thin:@localhost:1521/db2
      username: user2
      password: pass2
      driver-class-name: oracle.jdbc.OracleDriver

api:
  backend:
    url: http://backend-api-url

output:
  directory: /path/to/output/directory
