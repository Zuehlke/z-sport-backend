FROM snackbar/z-sport-java11:l.0.0

ADD build/libs/*.jar /tmp

ENTRYPOINT ["java","-jar","/tmp/zSport-0.0.1-SNAPSHOT.jar"]