FROM gradle:jdk11 as build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --warning-mode all --no-daemon --watch-fs

FROM openjdk:11-jdk-slim-buster

WORKDIR /home/kittybot

COPY --from=build /home/gradle/src/build/libs/KittyBot-*-all.jar KittyBot.jar

RUN sudo apt install -y curl

ENTRYPOINT ["java"]
CMD ["-jar", "KittyBot.jar"]