# !/bin/sh
#set -x

UPDATE_DIR=/home/data/httpd/download.eclipse.org/rt/eclipselink/updates
UPDATE_NAME="EclipseLink Update Site"
CMD_ERROR=false
SITE_DIR=$1
SITE_NAME=$2
if [ "${SITE_DIR}" = "update" ]
then
    SITE_DIR=${UPDATE_DIR}
    SITE_NAME=${UPDATE_NAME}
fi

if [ "${SITE_DIR}" = "" ]
then
    echo "ERROR: Site location must be specified!"
    CMD_ERROR=true
fi
if [ ! -d ${SITE_DIR} ]
then
    echo "ERROR: Need to generate the children repositories before generating the composite!"
    CMD_ERROR=true
fi
if [ "${SITE_NAME}" = "" ]
then
    echo "ERROR: Site name must be specified!"
    CMD_ERROR=true
fi
if [ "${CMD_ERROR}" = "true" ]
then
            echo "USAGE: ./buildCompositeP2.sh site_dir site_name"
            echo "   where:"
            echo "      site_dir  = the location of the compositeRepository with"
            echo "                  child repositories already present under it"
            echo "      site_name = string designating the compositeRepository name"
            exit
fi

# safe temp directory
tmp=${TMPDIR-/tmp}
tmp=$tmp/somedir.$RANDOM.$RANDOM.$RANDOM.$$
(umask 077 && mkdir $tmp) || {
  echo "Could not create temporary directory! Exiting." 1>&2
  exit 1
}

#------- Subroutines -------#
unset genContent
genContent() {
    # Generate the nightly build table
    echo "<?xml version='1.0' encoding='UTF-8'?>" > $tmp/content.xml
    echo "<?compositeMetadataRepository version='1.0.0'?>" >> $tmp/content.xml
    echo "<repository name='&quot;${SITE_NAME}&quot;' type='org.eclipse.equinox.internal.p2.metadata.repository.CompositeMetadataRepository' version='1.0.0'>" >> $tmp/content.xml
    echo "  <properties size='1'>" >> $tmp/content.xml
    echo "    <property name='p2.timestamp' value='1267023743270'/>" >> $tmp/content.xml
    echo "  </properties>" >> $tmp/content.xml
    echo "  <children size='${child_count}'>" >> $tmp/content.xml
    echo "    <child location='categories'/>" >> $tmp/content.xml
    cat $tmp/children.xml >> $tmp/content.xml
    echo "  </children>" >> $tmp/content.xml
    echo "</repository>" >> $tmp/content.xml
}

unset genArtifact
genArtifact() {
    # Generate the nightly build table
    echo "<?xml version='1.0' encoding='UTF-8'?>" > $tmp/artifact.xml
    echo "<?compositeArtifactRepository version='1.0.0'?>" >> $tmp/artifact.xml
    echo "<repository name='&quot;${SITE_NAME}&quot;' type='org.eclipse.equinox.internal.p2.artifact.repository.CompositeArtifactRepository' version='1.0.0'>" >> $tmp/artifact.xml
    echo "  <properties size='1'>" >> $tmp/artifact.xml
    echo "    <property name='p2.timestamp' value='1267023743270'/>" >> $tmp/artifact.xml
    echo "  </properties>" >> $tmp/artifact.xml
    echo "  <children size='${child_count}'>" >> $tmp/artifact.xml
    echo "    <child location='categories'/>" >> $tmp/artifact.xml
    cat $tmp/children.xml >> $tmp/artifact.xml
    echo "  </children>" >> $tmp/artifact.xml
    echo "</repository>" >> $tmp/artifact.xml
}

unset genChildren
genChildren() {
    for child in `ls -dr [0-9]*` ; do
        child_count=`expr $child_count + 1`
        echo "    <child location='${child}'/>" >> $tmp/children.xml
    done
}

#------- MAIN -------#
cd ${SITE_DIR}
echo "generating Composite Repository..."
echo "    At:     '${SITE_DIR}'"
echo "    Called: '${SITE_NAME}'"

#  child_count is 1 because there will always be a child
#  for the categories (which wo't be counted)
child_count=1
genChildren
genArtifact
genContent

# Copy the completed file to the server, and cleanup
mv -f $tmp/content.xml  ${SITE_DIR}/compositeContent.xml
mv -f $tmp/artifact.xml ${SITE_DIR}/compositeArtifacts.xml
rm -rf $tmp
