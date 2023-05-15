#FROM harbor.1000kit.org/1000kit/ubi-quarkus:8.4
#COPY target/*-runner /work/application

# FROM harbor.1000kit.org/1000kit/ubi-quarkus-jvm:8.4
# COPY target/lib/* /work/lib/
# COPY target/*-runner.jar /work/app.jar

FROM registry.access.redhat.com/ubi8/openjdk-17:1.11

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --chown=185 target/quarkus-app/lib/ /deployments/lib/
COPY --chown=185 target/quarkus-app/*.jar /deployments/
COPY --chown=185 target/quarkus-app/app/ /deployments/app/
COPY --chown=185 target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"
