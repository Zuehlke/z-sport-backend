FROM snackbar/z-sport-java11:1.0.0

RUN mkdir /opt/zsport

ADD build/libs/zSport-0.0.1-SNAPSHOT.jar /opt/zsport

CMD ["java","-jar","/opt/zsport/zSport-0.0.1-SNAPSHOT.jar"]