# !/bin/sh
set -x

cd /shared/technology/eclipselink
num_files=10

# leave only the last $num_files (10) builds on the download server
index=0
for zipfile in `ls -r /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/eclipselink-plugins-incubation*.zip ` ; do
	index=`expr $index + 1`
        if [ $index -gt $num_files ] ; then
           rm $zipfile
	fi
done

index=0
for zipfile in `ls -r /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/eclipselink-incubation*.zip ` ; do
	index=`expr $index + 1`
        if [ $index -gt $num_files ] ; then
           rm $zipfile
	fi
done
#Test suite results
for suite in core jpa sdo  ; do
    # Clean SRG Results
    index=0
    for htmlfile in `ls -r /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/${suite}/eclipse*srg*.html ` ; do
        index=`expr $index + 1`
        if [ $index -gt $num_files ] ; then
            rm $htmlfile
        fi
        if [ $index -eq 1 ] ; then
            cp $htmlfile /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/${suite}/latest-srg-test-results.html
        fi 
    done

    # Clean LRG Results
    index=0
    for htmlfile in `ls -r /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/${suite}/eclipse*lrg*.html ` ; do
        index=`expr $index + 1`
        if [ $index -gt $num_files ] ; then
            rm $htmlfile
        fi
        if [ $index -eq 1 ] ; then
            cp $htmlfile /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/${suite}/latest-lrg-test-results.html
        fi 
    done
done
index=0
for htmlfile in `ls -r /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/moxy/eclipse*oxm*.html ` ; do
    index=`expr $index + 1`
    if [ $index -gt $num_files ] ; then
        rm $htmlfile
    fi
    if [ $index -eq 1 ] ; then
        cp $htmlfile /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/moxy/latest-oxm-test-results.html
    fi 
done
index=0
for htmlfile in `ls -r /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/moxy/eclipse*jaxb*.html ` ; do
    index=`expr $index + 1`
    if [ $index -gt $num_files ] ; then
        rm $htmlfile
    fi
    if [ $index -eq 1 ] ; then
        cp $htmlfile /home/data/httpd/download.eclipse.org/technology/eclipselink/nightly/test-results/moxy/latest-jaxb-test-results.html
    fi 
done

# leave only last 5 days worth of files in the maven repository
index=0
# 5 days worth of files - 9 files per day
num_files=45
for mvnfile in `ls -r /home/data/httpd/download.eclipse.org/technology/eclipselink/maven.repo/org/eclipse/persistence/eclipselink/1.0-SNAPSHOT/eclipse*.* ` ; do
        index=`expr $index + 1`
        if [ $index -gt $num_files ] ; then
           rm $mvnfile
        fi
done

