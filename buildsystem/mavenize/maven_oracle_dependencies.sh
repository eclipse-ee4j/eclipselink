#!/bin/sh
#Register External JARs
export ORACLE_HOME=/usr/local/oracle/ora122
export ORACLE_VERSION=12.2.0.1
export FMW_HOME=/usr/local/oracle/wls122130
export FMW_VERSION=12.2.1-3
#Oracle JDBC
mvn install:install-file -Dfile=$ORACLE_HOME/jdbc/lib/ojdbc8.jar -DgroupId=com.oracle.jdbc -DartifactId=ojdbc8 -Dversion=$ORACLE_VERSION -Dpackaging=jar
mvn install:install-file -Dfile=$ORACLE_HOME/jdbc/lib/simplefan.jar -DgroupId=com.oracle.jdbc -DartifactId=simplefan -Dversion=$ORACLE_VERSION -Dpackaging=jar
#Oracle ONS
mvn install:install-file -Dfile=$ORACLE_HOME/opmn/lib/ons.jar -DgroupId=com.oracle -DartifactId=ons -Dversion=$ORACLE_VERSION -Dpackaging=jar
#Oracle XML parser and XDB
mvn install:install-file -Dfile=$ORACLE_HOME/lib/xmlparserv2.jar -DgroupId=com.oracle -DartifactId=xmlparserv2 -Dversion=$ORACLE_VERSION -Dpackaging=jar
mvn install:install-file -Dfile=$ORACLE_HOME/rdbms/jlib/xdb6.jar -DgroupId=com.oracle -DartifactId=xdb6 -Dversion=$ORACLE_VERSION -Dpackaging=jar
#Oracle Universal Connection Pool (UCP)
mvn install:install-file -Dfile=$ORACLE_HOME/ucp/lib/ucp.jar -DgroupId=com.oracle -DartifactId=ucp -Dversion=$ORACLE_VERSION -Dpackaging=jar
#Oracle AQ
mvn install:install-file -Dfile=$ORACLE_HOME/rdbms/jlib/aqapi.jar -DgroupId=com.oracle -DartifactId=aqapi -Dversion=$ORACLE_VERSION -Dpackaging=jar
#Oracle DMS
mvn install:install-file -Dfile=$FMW_HOME/oracle_common/modules/oracle.dms/dms.jar -DgroupId=com.oracle -DartifactId=dms -Dversion=$ORACLE_VERSION -Dpackaging=jar
#Oracle i18
mvn install:install-file -Dfile=$ORACLE_HOME/jlib/orai18n.jar -DgroupId=com.oracle.orai18n -DartifactId=orai18n -Dversion=$ORACLE_VERSION -Dpackaging=jar
mvn install:install-file -Dfile=$ORACLE_HOME/jlib/orai18n-mapping.jar -DgroupId=com.oracle.orai18n -DartifactId=orai18n-mapping -Dversion=$ORACLE_VERSION -Dpackaging=jar
mvn install:install-file -Dfile=$ORACLE_HOME/jlib/orai18n-collation.jar -DgroupId=com.oracle.orai18n -DartifactId=orai18n-collation -Dversion=$ORACLE_VERSION -Dpackaging=jar
mvn install:install-file -Dfile=$ORACLE_HOME/jlib/orai18n-utility.jar -DgroupId=com.oracle.orai18n -DartifactId=orai18n-utility -Dversion=$ORACLE_VERSION -Dpackaging=jar
#Oracle Spatial API
mvn install:install-file -Dfile=$ORACLE_HOME/md/jlib/sdoapi.jar -DgroupId=com.oracle.spatial -DartifactId=sdoapi -Dversion=$ORACLE_VERSION -Dpackaging=jar
#Oracle Weblogic Server Client
#wlclient leads into troubles with WLS started via cargo plugin 
mvn install:install-file -Dfile=$FMW_HOME/wlserver/server/lib/wlclient.jar -DgroupId=com.oracle.weblogic -DartifactId=wlclient -Dversion=$FMW_VERSION -Dpackaging=jar
#Prefer this
mvn install:install-file -Dfile=$FMW_HOME/wlserver/server/lib/wlthint3client.jar -DgroupId=com.oracle.weblogic -DartifactId=wlthint3client -Dversion=$FMW_VERSION -Dpackaging=jar