#base 
FROM ubuntu:16.04

#install java
RUN apt-get update
RUN apt-get install default-jre -y

#install apache2 & php
RUN apt-get install apache2 -y
RUN apt-get install php -y
RUN apt-get install php7.0 libapache2-mod-php7.0 -y
RUN apt-get update
RUN apt-get clean all

#ENTRYPOINT ["/usr/sbin/apache2", "-k", "start"]

#copy local apps
COPY /web /var/www/html
COPY /KF /home/KF

#start kafka, flink
#WORKDIR /home/KF/kafka
#ENTRYPOINT ["./kafka_start.sh"]
#WORKDIR /home/KF/flink
#ENTRYPOINT ["./flink_start.sh"]

EXPOSE 80 443 2181 8081 9092 

#apache start
CMD apachectl -D FOREGROUND



