FROM openjdk:8
COPY target/cloud-mall-user-1.0-SNAPSHOT.jar /tmp/app.jar
WORKDIR /tmp
CMD java -Xmx64m -Xss256k -XX:ParallelGCThreads=2 -jar -Djava.compiler=NONE -Dspring.profiles.active=prod app.jar