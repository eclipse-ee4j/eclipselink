# !/bin/sh
set -x


version=2.0.3
mavenVersion=${version}-SNAPSHOT
BaseDownloadNFSDir="/home/data/httpd/download.eclipse.org/rt/eclipselink"
buildir=/shared/rt/eclipselink

cd ${buildir}
num_files=10

# leave only the last 10 build dirs for the version on the download server
index=0
for contentdir in `ls -dr ${BaseDownloadNFSDir}/nightly/${version}/[0-9]*` ; do
    index=`expr $index + 1`
    if [ $index -gt $num_files ] ; then
        rm -r $contentdir
    fi
done

# leave only last 5 days worth of files in the maven repository
index=0
# 5 days worth of files - 9 files per day
num_files=45
for mvnfile in `ls -r ${BaseDownloadNFSDir}/maven.repo/org/eclipse/persistence/eclipselink/${mavenVersion}/eclipse*.* ` ; do
        index=`expr $index + 1`
        if [ $index -gt $num_files ] ; then
           rm $mvnfile
        fi
done

