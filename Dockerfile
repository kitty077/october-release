FROM tomee:8-jre-8.0.5-plume
COPY ./target/MOBILES.war /usr/local/tomee/webapps/
COPY ./tomee/conf/tomee.xml /usr/local/tomee/conf/
COPY ./tomee/conf/server.xml /usr/local/tomee/conf/
COPY ./tomee/conf/tomcat-users.xml /usr/local/tomee/conf/
COPY ./tomee/conf/settings.xml /usr/local/tomee/conf/
COPY ./tomee/conf/context.xml /usr/local/tomee/webapps/manager/META-INF
COPY ./tomee/conf/context.xml /usr/local/tomee/webapps/host-manager/META-INF
COPY ./target/MOBILES/WEB-INF/lib /usr/local/tomee/lib
CMD ["catalina.sh", "run"]
HEALTHCHECK --interval=10s --timeout=3s --retries=6 CMD curl -f http://app:8080/MOBILES/ \
|| exit 1
HEALTHCHECK --interval=10s --timeout=3s --retries=12 CMD curl -f http://app:8080/MOBILES/JNDIServlet \
|| exit 1
#EXPOSE 8080