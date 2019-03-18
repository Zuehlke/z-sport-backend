FROM snackbar/z-sport-java11:1.0.0

ADD build/libs/zSport-0.0.1-SNAPSHOT.jar.jar /tmp

ENTRYPOINT ["java","-jar","/tmp/zSport-0.0.1-SNAPSHOT.jar"]