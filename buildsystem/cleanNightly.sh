# !/bin/sh
#set -x

version=2.3.2
BaseDownloadNFSDir="/home/data/httpd/download.eclipse.org/rt/eclipselink"
buildir=/shared/rt/eclipselink

cd ${buildir}
if [ "input" == "release" ]
then
    # When releasing clear all nightly builds
    num_builds=0
    num_p2_builds=0
    num_maven_builds=0
else
    num_builds=10
    num_p2_builds=5
    # Maven: 5 builds * 9 files/build = 45
    num_maven_files=45
fi

### Download Site ###
#      leave only the last 10 build dirs for the version on the download server
index=0
removed=0
cd ${BaseDownloadNFSDir}/nightly/${version}
for contentdir in `ls -dr [0-9]*` ; do
    index=`expr $index + 1`
    if [ $index -gt $num_builds ] ; then
        echo "Removing ${contentdir}..."
        rm -r $contentdir
        removed=`expr $removed + 1`
    fi
done
echo "Removed $removed direcories from ${BaseDownloadNFSDir}/nightly/${version}."

### P2 Site ###
#      leave only the last "num_p2_builds" builds for the version in the nightly P2 repos
index=0
removed=0
cd ${BaseDownloadNFSDir}/nightly-updates
for contentdir in `ls -dr ${version}*` ; do
    index=`expr $index + 1`
    if [ $index -gt $num_p2_builds ] ; then
        echo "Removing ${contentdir}..."
        rm -r $contentdir
        removed=`expr $removed + 1`
    fi
done
echo "Removed $removed direcories from ${BaseDownloadNFSDir}/nightly-updates."

### Maven Site ###
#      leave only last 5 days worth of files in the maven repository
cd ${BaseDownloadNFSDir}/maven.repo/org/eclipse/persistence
for mvncomp in `ls -d *eclipse*` ; do
    index=0
    removed=0
    cd ${BaseDownloadNFSDir}/maven.repo/org/eclipse/persistence/${mvncomp}/${version}-SNAPSHOT
    for mvnfile in `ls -r ${mvncomp}*.*` ; do
        index=`expr $index + 1`
        if [ $index -gt $num_maven_files ] ; then
           echo "Removing ${mvnfile}..."
           rm $mvnfile
           removed=`expr $removed + 1`
        fi
    done
    echo "Removed $removed files from ${BaseDownloadNFSDir}/maven.repo/org/eclipse/persistence/${mvncomp}/${version}-SNAPSHOT."
done
cd ${buildir}
