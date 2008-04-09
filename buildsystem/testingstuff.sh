# !/bin/sh

export JAVA_HOME=/shared/common/ibm-java2-ppc-50
export PATH=${JAVA_HOME}/bin:/usr/bin:/usr/local/bin:${PATH}

cd /shared/technology/eclipselink

find /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly -name \*.zip -printf '     ${/%f%incubation*}'

