# !/bin/sh

export JAVA_HOME=/shared/common/ibm-java-jdk-ppc-60
export ANT_HOME=/shared/common/apache-ant-1.7.0
export ANT_ARGS=" "
export ANT_OPTS="-Xmx512m"

export PATH=${JAVA_HOME}/bin:${ANT_HOME}/bin:/usr/bin:/usr/local/bin:${PATH}
