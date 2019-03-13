FROM centos

# install java11
ADD jdk-11.0.2_linux-x64_bin.rpm /tmp
RUN yum -y localinstall /tmp/jdk-11.0.2_linux-x64_bin.rpm
ENV JAVA_HOME=/usr/java/jdk-11.0.2
ENV PATH=$PATH:/usr/java/jdk-11.0.2/bin

ADD build/libs/zSport-0.0.1-SNAPSHOT.jar /tmp

ENTRYPOINT ["java","-jar","/tmp/zSport-0.0.1-SNAPSHOT.jar"]