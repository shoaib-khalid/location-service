<p align="center"><a href="https://spring.io/projects/spring-boot" target="_blank"><img src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/spring-boot/spring-boot.png" width="100"></a></p>


## Springboot

Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".

We take an opinionated view of the Spring platform and third-party libraries so you can get started with minimum fuss. Most Spring Boot applications need minimal Spring configuration.


## Environment Setup

1. java version 1.8
2. jdk 1.8
3. build tool using maven


## Steps for run application

Step 1: Open `\resources\application.properties` and inject your variable

Step 2: Run the program


## Deploy to staging

Step 1: Merge to staging branch

Step 2: Upgrade version in pom.xml & SwaggerConfig.java in method apiInfo() -> licenseUrl version

Step 3: Put description in ReleaseNotes.md for upgrade version

Step 4: Once successful build helm-location-service , then visit kubeapps dashboard to upgrade the version . 
(If your upgrade version not showing in kubeapps, click menu> app repositories > Refresh all. Then it will the display the upgrade version we made, then deploy.)

Step 5: Check the version in kubernetes in order to make sure the deployed version is correct

## To debug live or previous log

Step 1: Go to kubernetes > pods > choose location service > clik icon sign in (execute into pods) > terminal will display

Step 2: cd logs/symplified/location-service/

Step 3: Choose preferences log

Live check =  tail -f product-service.log

Previous log  = less product-service.log

Previous days log = cd archived > ls > choose the log based on date you want

