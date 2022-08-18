[//]: # " Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved. "
[//]: # "  "
[//]: # " This program and the accompanying materials are made available under the "
[//]: # " terms of the Eclipse Public License v. 2.0 which is available at "
[//]: # " http://www.eclipse.org/legal/epl-2.0, "
[//]: # " or the Eclipse Distribution License v. 1.0 which is available at "
[//]: # " http://www.eclipse.org/org/documents/edl-v10.php. "
[//]: # "  "
[//]: # " SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause "

# EclipseLink Test Applications

## Running & debugging tests

Edit properties file in [$REPO/etc](https://github.com/eclipse-ee4j/eclipselink/tree/master/etc) to match you environment and then:

* `mvn verify -pl :org.eclipse.persistence.jpa.testapps -amd` - runs all tests against default in-memory Derby DB in Java SE environment;
the DB is started/stopped for every testapp

* `mvn verify -pl :org.eclipse.persistence.jpa.testapps -amd -Pmysql` - runs all tests against MySQL DB in Java SE environment;
the DB must be started/stopped externally. This allows running tests in parallel (ie `-T3C` maven option or using `mvnd`)

* `mvn verify -pl :org.eclipse.persistence.jpa.testapps -amd -Pmysql,wildfly` - runs all tests against MySQL DB in Java SE environment
and in Jakarta EE environment on WildFly server; the DB must be started/stopped externally. To allow running tests in parallel (ie `-T3C` maven option or using `mvnd`),
datasources on the server need to point to different MySQL DB schemas from those used for running in the SE env.

* `mvn test -pl :org.eclipse.persistence.jpa.testapps.jpql -Poracle` - runs all JPQL tests against Oracle DB in Java SE
* `mvn test -pl :org.eclipse.persistence.jpa.testapps.jpql -Dtest=JUnitJPQLDateTimeTest -Pmysql` - runs single JPQL test against MySQL DB in Java SE

* use `-Dmaven.surefire.debug` property to debug tests; the debugger will wait on port `5005`

## WildFly configuration

### EclipseLink module

```
WILDFLY_HOME=...
REPO_HOME=$HOME/.m2/repository/org/eclipse/persistence
VERSION=4.0.0-SNAPSHOT
ASM_VERSION=9.3.0

WR=$WILDFLY_HOME/modules/system/layers/base/org/eclipse/persistence/main

cp -v $REPO_HOME/org.eclipse.persistence.asm/$ASM_VERSION/org.eclipse.persistence.asm-$ASM_VERSION.jar $WR/org.eclipse.persistence.asm.jar
cp -v $REPO_HOME/org.eclipse.persistence.core/$VERSION/org.eclipse.persistence.core-$VERSION.jar $WR/org.eclipse.persistence.core.jar
cp -v $REPO_HOME/org.eclipse.persistence.jpa/$VERSION/org.eclipse.persistence.jpa-$VERSION.jar $WR/org.eclipse.persistence.jpa.jar
cp -v $REPO_HOME/org.eclipse.persistence.jpa.jpql/$VERSION/org.eclipse.persistence.jpa.jpql-$VERSION.jar $WR/org.eclipse.persistence.jpa.jpql.jar
cp -v $REPO_HOME/org.eclipse.persistence.moxy/$VERSION/org.eclipse.persistence.moxy-$VERSION.jar $WR/org.eclipse.persistence.moxy.jar
cp -v $REPO_HOME/org.eclipse.persistence.oracle/$VERSION/org.eclipse.persistence.oracle-$VERSION.jar $WR/org.eclipse.persistence.oracle.jar

echo '<module name="org.eclipse.persistence" xmlns="urn:jboss:module:1.9">
    <properties>
        <property name="jboss.api" value="public"/>
    </properties>

    <resources>
        <resource-root path="jipijapa-eclipselink-jakarta-27.0.0.Alpha4.jar"/>
        <resource-root path="org.eclipse.persistence.asm.jar"/>
        <resource-root path="org.eclipse.persistence.core.jar"/>
        <resource-root path="org.eclipse.persistence.jpa.jar" />
        <resource-root path="org.eclipse.persistence.jpa.jpql.jar"/>
        <resource-root path="org.eclipse.persistence.moxy.jar"/>
        <resource-root path="org.eclipse.persistence.oracle.jar"/>
    </resources>

    <dependencies>
        <module name="java.desktop"/>
        <module name="java.instrument"/>
        <module name="java.logging"/>
        <module name="java.management"/>
        <module name="java.naming"/>
        <module name="java.rmi"/>
        <module name="java.xml"/>
        <module name="jdk.unsupported"/>
        <module name="jakarta.annotation.api"/>
        <module name="jakarta.enterprise.api"/>
        <module name="jakarta.json.api" optional="true"/>
        <module name="javax.persistence.api"/>
        <module name="jakarta.transaction.api"/>
        <module name="jakarta.validation.api"/>
        <module name="jakarta.xml.bind.api"/>
        <module name="org.jboss.as.jpa.spi"/>
        <module name="org.jboss.logging"/>
        <module name="org.jboss.vfs"/>
        <module name="com.oracle.ojdbc11" optional="true"/>
    </dependencies>

</module>' > $WR/module.xml

```

### MySQL

#### JDBC driver

```
connect
module add --name=com.mysql.driver8 --resources=$HOME/.m2/repository/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28.jar --dependencies=javax.api,javax.transaction.api
/subsystem=datasources/jdbc-driver=mysql8/:add(driver-module-name=com.mysql.driver8,driver-name=mysql8,driver-class-name=com.mysql.cj.jdbc.Driver,driver-major-version=8,driver-minor-version=0)
:shutdown(restart=true)
```

#### DataSources

```
connect
data-source add --jndi-name=java:/jdbc/EclipseLinkDS --name=EclipseLinkDS --connection-url=jdbc:mysql://localhost:3306/testds --driver-name=mysql8 --user-name=root —-password=root --jta=true --validate-on-match=true --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter --enabled=true
/subsystem=datasources/data-source=EclipseLinkDS:write-attribute(name=password,value=testds)

xa-data-source add --jndi-name=java:/jdbc/EclipseLinkXADS --name=EclipseLinkXADS --driver-name=mysql8 --user-name=root —-password=root --xa-datasource-class=com.mysql.cj.jdbc.MysqlXADataSource --validate-on-match=true --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter --enabled=true --xa-datasource-properties={"ServerName"="localhost","PortNumber"="3306","DatabaseName"="testxads1"}
/subsystem=datasources/xa-data-source=EclipseLinkXADS:write-attribute(name=password,value=testxads1)

xa-data-source add --jndi-name=java:/jdbc/EclipseLinkDS2 --name=EclipseLinkDS2 --driver-name=mysql8 --user-name=root —-password=root --xa-datasource-class=com.mysql.cj.jdbc.MysqlXADataSource --validate-on-match=true --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter --enabled=true --xa-datasource-properties={"ServerName"="localhost","PortNumber"="3306","DatabaseName"="testxads2"}
/subsystem=datasources/xa-data-source=EclipseLinkDS2:write-attribute(name=password,value=testxads2)

xa-data-source add --jndi-name=java:/jdbc/EclipseLinkDS3 --name=EclipseLinkDS3 --driver-name=mysql8 --user-name=root —-password=root --xa-datasource-class=com.mysql.cj.jdbc.MysqlXADataSource --validate-on-match=true --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter --enabled=true --xa-datasource-properties={"ServerName"="localhost","PortNumber"="3306","DatabaseName"="testxads3"}
/subsystem=datasources/xa-data-source=EclipseLinkDS3:write-attribute(name=password,value=testxads3)

:reload
```

### Oracle

#### JDBC driver

```
connect
module add --name=com.oracle.ojdbc11 --resources=$HOME/.m2/repository/com/oracle/database/jdbc/ojdbc11/21.6.0.0.1/ojdbc11-21.6.0.0.1.jar:$HOME/.m2/repository/com/oracle/database/jdbc/ucp/21.6.0.0.1/ucp-21.6.0.0.1.jar:$HOME/.m2/repository/com/oracle/database/ha/simplefan/21.6.0.0.1/simplefan-21.6.0.0.1.jar:$HOME/.m2/repository/com/oracle/database/ha/ons/21.6.0.0.1/ons-21.6.0.0.1.jar:$HOME/.m2/repository/com/oracle/database/xml/xmlparserv2/21.6.0.0.1/xmlparserv2-21.6.0.0.1.jar:$HOME/.m2/repository/com/oracle/database/xml/xdb/21.6.0.0.1/xdb-21.6.0.0.1.jar:$HOME/.m2/repository/com/oracle/database/nls/orai18n/21.6.0.0.1/orai18n-21.6.0.0.1.jar --dependencies=javax.api,javax.transaction.api
/subsystem=datasources/jdbc-driver=ojdbc11/:add(driver-module-name=com.oracle.ojdbc11,driver-name=ojdbc11,driver-class-name=oracle.jdbc.OracleDriver,driver-major-version=21,driver-minor-version=6)
:shutdown(restart=true)
```

#### DataSources

```
connect
data-source add --jndi-name=java:/jdbc/EclipseLinkDS --name=EclipseLinkDS --connection-url=jdbc:oracle:thin:@//localhost:1521/ORCLPDB1 --driver-name=ojdbc11 --user-name=testds —-password=testds --jta=true --validate-on-match=true --enabled=true
/subsystem=datasources/data-source=EclipseLinkDS:write-attribute(name=password,value=tst)

# XA ds TBD
```

## Add new server configuration

To add new server configuration for runnig tests, define new profile which does deploy/undeploy,
test execution and sets properties for the test run. Manual JDBC DS creation and server start up/shutdown
is currently required.

In the WildFly example below, `deploy` is bound to `pre-integration-test`, `test-run` to `integration-test`,
and `undeploy` to `post-integration-test` phase.

```xml
    <profile>
        <!-- server ID -->
        <id>wildfly</id>
        <properties>
            <!-- EJB client configuration -->
            <server.jndi.factory>org.wildfly.naming.client.WildFlyInitialContextFactory</server.jndi.factory>
            <server.remote.url>remote+http://localhost:8080</server.remote.url>
            <!-- don't deploy anything if tests are being skipped -->
            <wildfly.deploy.skip>${server.test.skip}</wildfly.deploy.skip>
        </properties>
        <!-- server specific dependencies (EJB client) -->
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>org.wildfly</groupId>
                    <artifactId>wildfly-client-all</artifactId>
                    <version>27.0.0.Alpha4</version>
                </dependency>
            </dependencies>
        </dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.wildfly</groupId>
                <artifactId>wildfly-client-all</artifactId>
                <scope>test</scope>
            </dependency>
        </dependencies>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.wildfly.plugins</groupId>
                    <artifactId>wildfly-maven-plugin</artifactId>
                    <version>2.1.0.Beta1</version>
                    <configuration>
                        <skip>${wildfly.deploy.skip}</skip>
                        <username>${wildfly.username}</username>
                        <password>${wildfly.password}</password>
                        <name>${project.build.finalName}.ear</name>
                    </configuration>
                    <executions>
                        <execution>
                            <id>server-deploy</id>
                            <phase>pre-integration-test</phase>
                            <goals>
                                <goal>deploy-only</goal>
                            </goals>
                            <configuration>
                                <filename>${project.build.finalName}.ear</filename>
                                <force>true</force>
                            </configuration>
                        </execution>
                        <execution>
                            <id>server-undeploy</id>
                            <phase>post-integration-test</phase>
                            <goals>
                                <goal>undeploy</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>server-test</id>
                            <phase>integration-test</phase>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
```

## Oracle Spatial

[Oracle Spatial](https://www.oracle.com/database/spatial/) API jar is available either in the Oracle DB installation or in the [Oracle SQL Developer](https://www.oracle.com/database/sqldeveloper/technologies/download/).
To be able to run tests for this functionality, locate `sdoapi.jar` in the instalation of one of these products and install it into
local maven repository, ie via:

```
mvn install:install-file -Dfile=sdoapi.jar -DgroupId=com.oracle.spatial -DartifactId=sdoapi -Dversion=LOCAL -Dpackaging=jar
```

To run tests on the server, add the `sdoapi.jar` to the Oracle JDBC driver library there.