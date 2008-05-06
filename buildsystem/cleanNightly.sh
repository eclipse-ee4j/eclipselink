# !/bin/sh
set -x

cd /shared/technology/eclipselink
index=0
num_files=10
for zipfile in `ls -r /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/eclipse*.zip ` ; do
	index=`expr $index + 1`
        if [ $index -gt $num_files ] ; then
           rm $zipfile
	fi
done
index=0
#core
for htmlfile in `ls -r /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/core/eclipse*.html ` ; do
        index=`expr $index + 1`
        if [ $index -gt $num_files ] ; then
           rm $htmlfile
        fi
	if [ $index -eq 1 ] ; then
	   cp $htmlfile /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/core/latest-test-results.html
        fi 
done

# maven repository
index=0
# 5 days worth of files - 9 files per day
num_files=45

for mvnfile in `ls -r /home/data/httpd/download.eclipse.org/technology/eclipselink/maven.repo/org/eclipse/persistence/eclipselink/1.0-SNAPSHOT/eclipse*.* ` ; do
        index=`expr $index + 1`
        if [ $index -gt $num_files ] ; then
           rm $mvnfile
        fi
done


