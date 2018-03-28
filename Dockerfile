FROM openjdk:8-alpine

COPY build/libs/kalocu.jar kalocu.jar

CMD java -jar kalocu.jar