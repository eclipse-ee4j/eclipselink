# Notices about EclipseLink Mavenization - initial steps to test

##System requirements
#####For sources synchronization
* `bash, rsync`
* `diff, zipinfo, sort` [optional for compare output bundles]
#####For build
* Apache Maven >= 3.5
* JDK 8
* JDK 11 [optional]

##Preparation steps (from project root)
* `cd buildsystem/mavenize`
* `./prepare.sh`

##Build commands (from project root)
* `mvn clean install -DskipTests` - build without Oracle dependencies (JDBC driver, XDK, Oracle Spatial....)
* `mvn clean install -DskipTests -P oracle` - full build. Oracle dependencies are required

##Preparation steps - description
Purpose of `prepare.sh` script is populate Maven modules with sources and resources from current Ant projects.
Initial state of Maven modules is directory with `pom.xml` and in some cases with assembly descriptors without any source and resource files.
`prepare.sh` script calls other `*_prepare.sh` scripts like `core_prepare.sh, dbws_prepare.sh...`
These scripts are written in bash and synchronization/copy commands using `rsync` tool.

Opposite to `prepare.sh` is `cleanup.sh`. It deletes all source/resource files copied by `prepare.sh` to the Maven project/modules. It keeps POM/assembly files untouched.

Synchronization/cleanup scripts doesn't modify origin Ant/Tycho projects.

##DB, JEE Server property files
Templates of property files used in testing environment are in `buildsystem/mavenize/test.properties` directory.
Copy them to the ${user.home} directory. Modify them in dependency of current environment.
If there is MySQL database and WildFly JEE server only following two property files are enough `el-test.mysql.properties` `el-testjee.wildfly.properties`.

##Oracle dependencies
There are some Oracle specific dependencies which is not freely available for a download from Maven central like JDBC driver, XDK....
See `buildsystem/mavenize/maven_oracle_dependencies.sh` script. This scripts expects Oracle DB (or at least Oracle DB client) local installation and Oracle WebLogic Server.
Check/modify environment variables on the beginning of the script (ORACLE_HOME, FMW_HOME).


For more details see `MAVENIZE.pdf`