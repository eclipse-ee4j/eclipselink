# !/bin/sh

export JAVA_HOME=/shared/common/ibm-java2-ppc-50
export PATH=${JAVA_HOME}/bin:/usr/bin:/usr/local/bin:${PATH}

cd /shared/technology/eclipselink

echo "generating webpage"

# safe temp directory
tmp=${TMPDIR-/tmp}
tmp=$tmp/somedir.$RANDOM.$RANDOM.$RANDOM.$$
(umask 077 && mkdir $tmp) || {
  echo "Could not create temporary directory! Exiting." 1>&2 
  exit 1
}
cat ./trunk/buildsystem/phphead.txt > $tmp/index.xml
find /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly -name eclipselink-incubation-[1-9]\*.zip -printf '%h        <p> <a href="http://www.eclipse.org/downloads/download.php?file=/technology/eclipselink/nightly/%f"> %f </a>    -----    <a href="http://www.eclipse.org/eclipselink/testing/index.php"> Test Results </a></p>\n' | grep -v 2008 | cut -d' ' -f2- | sort -r >> $tmp/index.xml
echo \<\/description\> >> $tmp/index.xml
echo \<\/section\> >> $tmp/index.xml
echo \<section class=\"main\" name=\"1.0 Nightly Builds - OSGi Plugins\"\> >> $tmp/index.xml
echo \<description\> >> $tmp/index.xml

find /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly -name eclipselink-plugins-incubation\*.zip -printf '%h        <p> <a href="http://www.eclipse.org/downloads/download.php?file=/technology/eclipselink/nightly/%f"> %f </a></p>\n' | grep -v 2008 | cut -d' ' -f2- | sort -r >> $tmp/index.xml
cat ./trunk/buildsystem/phptail.txt >> $tmp/index.xml

cat ./trunk/buildsystem/testinghead.txt > $tmp/testing.xml
#core test results

find /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/core -name \*.html -printf '        <p> <a href="http://download.eclipse.org/technology/eclipselink/nightly/test-results/core/%f"> %f </a></p>\n' | sort -r >> $tmp/testing.xml

find /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/jpa -name \*.html -printf '        <p> <a href="http://download.eclipse.org/technology/eclipselink/nightly/test-results/jpa/%f"> %f </a></p>\n' | sort -r >> $tmp/testing.xml

find /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/moxy -name \*.html -printf '        <p> <a href="http://download.eclipse.org/technology/eclipselink/nightly/test-results/moxy/%f"> %f </a></p>\n' | sort -r >> $tmp/testing.xml

find /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/sdo -name \*.html -printf '        <p> <a href="http://download.eclipse.org/technology/eclipselink/nightly/test-results/sdo/%f"> %f </a></p>\n' | sort -r >> $tmp/testing.xml

cat ./trunk/buildsystem/testingtail.txt >> $tmp/testing.xml

mv -f $tmp/index.xml  /home/data/httpd/download.eclipse.org/technology/eclipselink/downloads.xml
mv -f $tmp/testing.xml  /home/data/httpd/download.eclipse.org/technology/eclipselink/testing.xml
rm -rf $tmp


