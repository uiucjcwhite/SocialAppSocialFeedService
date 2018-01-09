# SocialAppMicroservice

### Setup
On the Eclipse Marketplace, you can find and install "Spring Tools (aka Spring IDE or Spring Tool Suite)." This is not required, but could be helpful in working with spring applications.

In Eclipse, import the SocialAppMicroservice folder as an existing project.

Right click on pom.xml in the Project Explorer > Run As > Maven build. When running for the first time, type 'compile' in the Goals field of the Run Configurations 

In the Project Explore, right click on SocialAppMicroserviceApplication.java under src/main/java/com.socialapp.microservice > Run As > Java Application (or > Spring Boot App if you have install Spring Tool Suite). This will run the service on the default port 8080.

#### Example Endpoints
http://localhost:8080
http://localhost:8080/json
http://localhost:8080/json?name=Andrew

#### More info
[Getting Started](https://spring.io/guides/gs/spring-boot/)

[Intro to Spring Boot and more info](http://www.adeveloperdiary.com/java/spring-boot/an-introduction-to-spring-boot/)

[Another example with a postgres database](http://mrbool.com/rest-server-with-spring-data-spring-boot-and-postgresql/34023)
