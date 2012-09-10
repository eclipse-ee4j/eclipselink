# !/bin/sh

#------------------------------------------------------------------------------------------------------
#    This script is designed to run from cron on the Eclipse foundation's build server
#       It tests for the existence of a completed build or test run
#       then initiates the publication of the results to the appropriate locations and
#       sends out notifiacation when complete.
#
#    Author: Eric Gwin (Oracle)
#

#==========================
#   Basic Env Setup
#

#Define common variables
ARG1=$1
umask 0002
ANT_ARGS=" "
ANT_OPTS="-Xmx512m"
START_DATE=`date '+%y%m%d-%H%M'`

#Directories
ANT_HOME=/shared/common/apache-ant-1.7.0
HOME_DIR=/shared/rt/eclipselink
EXEC_DIR=${HOME_DIR}
DNLD_DIR=/home/data/httpd/download.eclipse.org/rt/eclipselink
JAVA_HOME=/shared/common/jdk-1.6.x86_64
LOG_DIR=${HOME_DIR}/logs
RELENG_REPO=${HOME_DIR}/eclipselink.releng

#Files
ANT_BLDFILE=publishbuild.xml

#Global Variables
PUB_SCOPE_EXPECTED=0
PUB_SCOPE_COMPLETED=0

PATH=${JAVA_HOME}/bin:${ANT_HOME}/bin:/usr/bin:/usr/local/bin:${PATH}

# Export necessary global environment variables
export ANT_ARGS ANT_OPTS ANT_HOME HOME_DIR JAVA_HOME LOG_DIR PATH
#==========================
#   Functions Definitions
#
unset createPath
createPath() {
    # Usage: createPath path
    path=$1

    if [ "${DEBUG}" = "true" ] ; then
        echo "createPath: Attempting to create '${path}' path."
    fi
    newdir=
    for directory in `echo ${path} | tr '/' ' '`
    do
        newdir=${newdir}/${directory}
        if [ ! -d "${newdir}" ] ; then
            if [ "${DEBUG}" = "true" ] ; then
                echo "createPath: Creating subdir: '${newdir}'"
            fi
            mkdir ${newdir}
            if [ $? -ne 0 ]
            then
                echo "   Error (createPath):  Creation of ${newdir} failed!"
                exit
            fi
        fi
    done
}

unset parseHandoff
parseHandoff() {
    # Usage: parseHandoff handoff_file
    handoff_file=$1
    handoff_error_string1="Error: Invalid handoff_file name: '${handoff_file}'"
    handoff_error_string2="                   Was expecting: 'handoff-file-<PROC>-<BRANCH>-<QUALIFIER>.dat'"
    handoff_content_error1="Error: Invalid handoff_file contents: '`cat ${handoff_file}`'"
    handoff_content_error2="                       Was expecting: 'extract.loc=<BUILD_ARCHIVE_LOC> host=<HOST> maven.tag=<VERSION>-<MILESTONE>' "

    ## Parse handoff_file name for BRANCH, QUALIFIER, and PROC (Procedure: Build/Test)
    BRANCH=`echo ${handoff_file} | cut -s -d'-' -f4`
    if [ "${BRANCH}" = "" ] ; then
        echo "BRANCH ${handoff_error_string1}"
        echo "       ${handoff_error_string2}"
        BRANCH_ERR=true
    else
        BRANCH_ERR=false
    fi
    QUALIFIER=`echo ${handoff_file} | cut -s -d'-' -f5,6 | cut -d'.' -f1`
    if [ "${QUALIFIER}" = "" ] ; then
        echo "QUALIFIER ${handoff_error_string1}"
        echo "          ${handoff_error_string2}"
        QUALIFIER_ERR=true
    else
        QUALIFIER_ERR=false
    fi
    PROC=`echo ${handoff_file} | cut -s -d'-' -f3`
    if [ !  \( \( "${PROC}" = "test" \) -o \( "${PROC}" = "build" \) \) ] ; then
        echo "PROC ${handoff_error_string1}"
        echo "     ${handoff_error_string2}"
        PROC_ERR=true
    else
        PROC_ERR=false
    fi
    ## Parse $QUALIFIER for build date value
    BLDDATE=`echo ${QUALIFIER} | cut -s -d'-' -f1 | cut -s -dv -f2`
    if [ "${BLDDATE}" = "" ] ; then
        echo "BLDDATE Error: There is something wrong with QUALIFIER. ('$QUALIFIER' should be vDATE-rREV)!"
        BLDDATE_ERR=true
    else
        BLDDATE_ERR=false
    fi
    ## Parse $QUALIFIER for Git Hash value
    GITHASH=`echo ${QUALIFIER} | cut -s -d'-' -f2`
    if [ "${GITHASH}" = "" ] ; then
        echo "GITHASH Error: There is something wrong with QUALIFIER. ('$QUALIFIER' should be vDATE-rREV)!"
        GITHASH_ERR=true
    else
        GITHASH_ERR=false
    fi
    ## Parse handoff_file contents for BUILD_ARCHIVE_LOC (Where files were stored), HOST and MAVEN_TAG
    BUILD_ARCHIVE_LOC=`cat ${handoff_file} | cut -d' ' -f1 | cut -d'=' -f2`
    if [ "${BUILD_ARCHIVE_LOC}" = "" ] ; then
        echo "BUILD_ARCHIVE_LOC ${handoff_content_error1}"
        echo "                  ${handoff_content_error2}"
        BUILD_ARCHIVE_LOC_ERR=true
    else
        BUILD_ARCHIVE_LOC_ERR_ERR=false
    fi
    HOST=`cat ${handoff_file} | cut -d' ' -f2 | cut -d'=' -f2`
    if [ "${HOST}" = "" ] ; then
        echo "HOST ${handoff_content_error1}"
        echo "     ${handoff_content_error2}"
        HOST_ERR=true
    else
        HOST_ERR=false
    fi
    MAVEN_TAG=`cat ${handoff_file} | cut -d' ' -f3 | cut -d'=' -f2`
    if [ "${MAVEN_TAG}" = "" ] ; then
        echo "MAVEN_TAG ${handoff_content_error1}"
        echo "          ${handoff_content_error2}"
        MAVEN_TAG_ERR=true
    else
        MAVEN_TAG_ERR=false
    fi
    ## Parse MAVEN_TAG for VERSION
    VERSION=`echo ${MAVEN_TAG} | cut -d'-' -f1`
    if [ "${VERSION}" = "" ] ; then
        echo "VERSION Error: Something wrong with MAVEN_TAG ('${MAVEN_TAG}' should be VERSION-MILESTONE}!"
        ## If parsing MAVEN_TAG failed, try parsing BUILD_ARCHIVE_LOC
        VERSION=`echo ${BUILD_ARCHIVE_LOC} | cut -d'/' -f6`
        if [ "${VERSION}" = "" ] ; then
            echo "VERSION (attempt 2) Error: Something wrong with BUILD_ARCHIVE_LOC '${BUILD_ARCHIVE_LOC}'!"
            VERSION_ERR=true
        else
            VERSION_ERR=false
        fi
    else
        VERSION_ERR=false
    fi
    ## Parse handoff_file directory listing for TIMESTAMP
    TIMESTAMP=`ls -l --time-style=+%Y%m%d%H%M.%S ${handoff_file} | cut -d' ' -f6`
    if [ "${TIMESTAMP}" = "" ] ; then
        echo "TIMESTAMP error: Dir listing failed!"
        TIMESTAMP_ERR=true
    else
        TIMESTAMP_ERR=false
    fi
    if [ "${DEBUG}" = "true" ] ; then
        echo "parseHandoff: Parsed values:"
        echo "   BRANCH           = '${BRANCH}'"
        echo "   QUALIFIER        = '${QUALIFIER}'"
        echo "   PROC             = '${PROC}'"
        echo "   BLDDATE          = '${BLDDATE}'"
        echo "   GITHASH          = '${GITHASH}'"
        echo "   BUILD_ARCHIVE_LOC= '${BUILD_ARCHIVE_LOC}'"
        echo "   HOST             = '${HOST}'"
        echo "   MAVEN_TAG        = '${MAVEN_TAG}'"
        echo "   VERSION          = '${VERSION}'"
        echo "   TIMESTAMP        = '${TIMESTAMP}'"
    fi
}

unset establishPublishScope
establishPublishScope() {
    # Usage: establishPublishScope src
    src=$1

    ## To test success set variables "PUB_SCOPE_EXPECTED" and "PUB_SCOPE_COMPLETED"
    ## compare at end and if match delete local build artifacts
    ## and handoff file.
    # 100 - artifacts to pub
    #  10 - p2 to pub
    #   1 - maven to pub (should be set if 100 = true, but only need eclipselink.jar and bundle.zip)

    #reset PUB_SCOPE_EXPECTED for this handoff instance
    PUB_SCOPE_EXPECTED=0

    # search for zip files in src meaning need to publish artifacts
    srcZipCount=`ls ${src} | grep -c [.]zip$`
    if [ "${srcZipCount}" -gt 0 ] ; then
        PUB_SCOPE_EXPECTED=`expr ${PUB_SCOPE_EXPECTED} + 101`
        echo "Zip archives detected. Logging 'Archive publish' within scope."
    else
        echo "No zip archives detected. 'Archive publish' beyond scope."
    fi

    # search for p2repo dir, meaning need to publish P2
    # search for zip files in src meaning need to publish artifacts
    srcP2Count=`ls ${src} | grep -c p2repo`
    if [ \( ! "${srcP2Count}" = "0" \) ] ; then
        srcP2jarCount=`ls -r ${src}/p2repo/* | grep -c [.]jar$`
        if [ "${srcP2jarCount}" -gt 0 ] ; then
            PUB_SCOPE_EXPECTED=`expr ${PUB_SCOPE_EXPECTED} + 10`
            echo "Viable p2repo found. Logging 'p2 publish' within scope"
        else
            echo "p2repo dir found, but it is empty to publish. 'P2 publish' beyond scope."
        fi
    else
        echo "No p2repo found. 'P2 publish' beyond scope."
    fi
}

unset publishBuildArtifacts
publishBuildArtifacts() {
    # Usage: publishBuildArtifacts src dest version date timestamp
    src=$1
    dest=$2
    version=$3
    date=$4
    timestamp=$5
    qualifier=$6

    echo " "
    echo "Processing Build archives for publishing..."
    rootDest=${dest}/nightly

    #verify src, root dest, and needed variables exist before proceeding
    if [ \( -d "${src}" \) -a \( -d "${rootDest}" \) -a \( ! "${version}" = "" \) -a \( ! "${date}" = "" \) -a \( ! "${timestamp}" = "" \) ] ; then
        if [ "${DEBUG}" = "true" ] ; then
            echo "publishBuildArtifacts: Required locations and data verified... proceeding..."
            echo "   src       = '${src}'"
            echo "   rootDest  = '${rootDest}'"
            echo "   version   = '${version}'"
            echo "   date      = '${date}'"
            echo "   timestamp = '${timestamp}'"
        fi

        #Mk download destination dir (dest/nightly/<version>/<date>)
        downloadDest=${rootDest}/${version}/${date}
        createPath ${downloadDest}

        #force <date> dir's date attribute to date of handoff
        touch -t${timestamp} ${downloadDest}

        #count number of jars exported, and copy them preserving date
        srcJarCount=`ls ${src} | grep -c [.]jar$`
        if [ "${srcJarCount}" -gt 0 ] ; then
            if [ "${DEBUG}" = "true" ] ; then
                echo "publishBuildArtifacts: Copying ${srcJarCount} jar(s)"
                echo "                       from: '${src}'"
                echo "                         to: '${downloadDest}'"
            fi
            cp --preserve=timestamps ${src}/*.jar ${downloadDest}/.
        fi
        destJarCount=`ls ${downloadDest} | grep -c [.]jar$`
        if [ "${DEBUG}" = "true" ] ; then
            echo "publishBuildArtifacts: ${destJarCount} jar(s) copied."
        fi

        #count number of archives (zips) exported, and copy them preserving date
        srcZipCount=`ls ${src} | grep -c [.]zip$`
        if [ "${srcZipCount}" -gt 0 ] ; then
            if [ "${DEBUG}" = "true" ] ; then
                echo "publishBuildArtifacts: Copying ${srcZipCount} zip(s)"
                echo "                       from: '${src}'"
                echo "                         to: '${downloadDest}'"
            fi
            cp --preserve=timestamps ${src}/*.zip ${downloadDest}/.
        fi
        destZipCount=`ls ${downloadDest} | grep -c [.]zip$`
        if [ "${DEBUG}" = "true" ] ; then
            echo "publishBuildArtifacts: ${destZipCount} zips copied."
        fi

        #verify everything copied correctly
        if [ \( "${srcJarCount}" = "${destJarCount}" \) -a \( "${srcZipCount}" = "${destZipCount}" \) ] ; then
            echo "    Published ${destJarCount} jar(s) and ${destZipCount} zip(s) successfully."
            PUB_SCOPE_COMPLETED=`expr ${PUB_SCOPE_COMPLETED} + 100`
        else
            echo "    Published ${destJarCount} jar(s) and ${destZipCount} zip(s), but Src and Dest numbers don't match."
            echo "    Expected ${srcJarCount} jar(s) and ${srcZipCount} zip(s) to copy. Publish failed!"
            Error=true
        fi
    else
        # Something is not right! skipping.."
        echo "    Required locations and data failed to verify... skipping Artifact publishing...."
        ERROR=true
        if [ "${DEBUG}" = "true" ] ; then
            echo "publishBuildArtifacts: Required locations and data:"
            echo "   src       = '${src}'"
            echo "   rootDest  = '${rootDest}'"
            echo "   version   = '${version}'"
            echo "   date      = '${date}'"
            echo "   timestamp = '${timestamp}'"
        fi
    fi
}

unset publishP2Repo
publishP2Repo() {
    #Need handoff_loc, download location, version, qualifier
    # Usage: publishP2Repo src dest version qualifier
    src=$1
    dest=$2
    version=$3
    qualifier=$4

    echo " "
    echo "Preparing to publish P2 repository...."
    rootDest=${dest}/nightly-updates

    #verify src, root dest, and needed variables exist before proceeding
    if [ \( -d "${src}" \) -a \( -d "${rootDest}" \) -a \( ! "${version}" = "" \) -a \( ! "${qualifier}" = "" \) ] ; then
        if [ "${DEBUG}" = "true" ] ; then
            echo "publishP2Repo: Required locations and data verified... proceeding..."
            echo "   src      = '${src}'"
            echo "   rootDest = '${rootDest}'"
            echo "   version  = '${version}'"
            echo "   qualifier= '${qualifier}'"
        fi

        #count number of repos exported (should be 0 or 1)
        #if not 0 and dir repo not already published, copy it preserving timestamps
        srcP2Count=`ls ${src} | grep -c p2repo`
        downloadDest=${rootDest}/${version}.${qualifier}
        if [ \( ! "${srcP2Count}" = "0" \) ] ; then
            srcP2jarCount=`ls -r ${src}/p2repo/* | grep -c [.]jar$`

            if [ \( ! -d ${downloadDest} \) ] ; then
                #Create download destination dir (dest/nightly-updates/<version>.<qualifier>)
                createPath ${downloadDest}

                if [ "${DEBUG}" = "true" ] ; then
                    echo "publishP2Repo: Copying ${srcP2Count} repo(s) (${srcP2jarCount} jars)"
                    echo "                       from: '${src}'"
                    echo "                         to: '${downloadDest}'"
                fi
                cp -r --preserve=timestamps ${src}/p2repo/* ${downloadDest}

                destP2jarCount=`ls -r ${downloadDest}/* | grep -c [.]jar$`
                if [ "${DEBUG}" = "true" ] ; then
                    echo "publishP2Repo: ${destP2jarCount} jars copied to repo."
                fi

                #verify everything copied correctly
                if [ "${destP2jarCount}" = "${srcP2jarCount}" ] ; then
                    echo "    Published ${destP2jarCount} of ${srcP2jarCount} jars to repo successfully."
                    PUB_SCOPE_COMPLETED=`expr ${PUB_SCOPE_COMPLETED} + 10`
                else
                    echo "    Publish failed for P2 Repo. Only copied ${destP2jarCount} of ${srcP2jarCount} jars to repo."
                    ERROR=true
                    rm -r ${downloadDest}
                fi
            else
                destP2jarCount=`ls -r ${downloadDest}/* | grep -c [.]jar$`
                echo "   P2 repo already exists, ${destP2jarCount} of ${srcP2jarCount} jars published. Skipping..."
                PUB_SCOPE_COMPLETED=`expr ${PUB_SCOPE_COMPLETED} + 10`
            fi
        else
            echo "    No P2 repo to publish..."
            PUB_SCOPE_COMPLETED=`expr ${PUB_SCOPE_COMPLETED} + 10`
        fi
    else
        # Something is not right! skipping.."
        echo "    Required locations and data failed to verify... skipping Repo publishing...."
        ERROR=true
        if [ "${DEBUG}" = "true" ] ; then
            echo "publishP2Repo: Required locations and data:"
            echo "   src      = '${src}'"
            echo "   rootDest = '${rootDest}'"
            echo "   version  = '${version}'"
            echo "   qualifier= '${qualifier}'"
        fi
    fi
}

unset publishMavenRepo
publishMavenRepo() {
    #Need handoff_loc, branch, date, version, qualifier
    src=$1
    branch=$2
    blddate=$3
    version=$4
    qualifier=$5

    error_cnt=0

    # Define SYSTEM variables needed
    BldDepsDir=${HOME_DIR}/bld_deps/${branch}

    # unzip necessary files to 'upload dir'/plugins
    srczip=`ls ${src}/eclipselink-src*`
    installzip=`ls ${src}/eclipselink-${version}*`
    nosqlpluginzip=`ls ${src}/eclipselink-plugins-nosql*`
    pluginzip=`ls ${src}/eclipselink-plugins-${version}*`
    if [ -f ${pluginzip} ] ; then
       unzip -o ${pluginzip} -d ${src}/maven
    else
       echo "${pluginzip} not found!"
       error_cnt=`expr ${error_cnt} + 1`
    fi
    if [ -f ${nosqlpluginzip} ] ; then
       unzip -o ${nosqlpluginzip} -d ${src}/maven
    else
       echo "${nosqlpluginzip} not found!"
       error_cnt=`expr ${error_cnt} + 1`
    fi
    if [ -f ${src}/eclipselink.jar ] ; then
       cp ${src}/eclipselink.jar ${src}/maven/.
    else
       if [ -f ${installzip} ] ; then
          unzip -o -j ${installzip} eclipselink/jlib/eclipselink.jar -d ${src}/maven
       else
       echo "${installzip} not found!"
          error_cnt=`expr ${error_cnt} + 1`
       fi
    fi
    if [ -f ${srczip} ] ; then
       cp ${srczip} ${src}/maven/eclipselink-src.zip
    else
       echo "${srczip} not found!"
       error_cnt=`expr ${error_cnt} + 1`
    fi
    ls ${src}/maven

    #Invoke Antscript for Maven upload
    arguments="-Dbuild.deps.dir=${BldDepsDir} -Dcustom.tasks.lib=${RELENG_REPO}/ant_customizations.jar -Dversion.string=${version}.${qualifier}"
    arguments="${arguments} -Drelease.version=${version} -Dbuild.date=${blddate} -Dbuild.type=SNAPSHOT -Dbundle.dir=${src}/maven"

    # Run Ant from ${exec_location} using ${buildfile} ${arguments}
    echo "ant ${RELENG_REPO}/upload${branch}ToMaven.xml ${arguments}"
    ant -f ${RELENG_REPO}/upload${branch}ToMaven.xml ${arguments}
    if [ "$?" = "0" ]
    then
       # if successful, cleanup and set "COMPLETE"
       echo "Maven publish complete."
    else
       # if encountered error, increment error_cnt
       error_cnt=`expr ${error_cnt} + 1`
    fi
    if [ "$error_cnt" = "0" ]
    then
       # if successful, cleanup and set "COMPLETE"
       PUB_SCOPE_COMPLETED=`expr ${PUB_SCOPE_COMPLETED} + 1`
    fi
}

unset publishTestArtifacts
publishTestArtifacts() {
    # Usage: publishTestArtifacts src dest version date host
    src=$1
    dest=$2
    version=$3
    date=$4
    host=$5

    echo "Processing Test Results for publishing..."
    rootDest=${dest}/nightly

    #verify src, root dest, and needed variables exist before proceeding
    if [ \( -d "${src}" \) -a \( -d "${rootDest}" \) -a \( ! "${version}" = "" \) -a \( ! "${date}" = "" \) -a \( ! "${host}" = "" \) ] ; then
        if [ "${DEBUG}" = "true" ] ; then
            echo "publishTestArtifacts: Required locations and data verified... proceeding..."
            echo "   src      = '${src}'"
            echo "   rootDest = '${rootDest}'"
            echo "   version  = '${version}'"
            echo "   date     = '${date}'"
            echo "   host     = '${host}'"
        fi

        #Mk download destination dir (dest/nightly/<version>/<date>)
        downloadDest=${rootDest}/${version}/${date}/${host}
        createPath ${downloadDest}

        #count number of pages (html) exported, and copy them preserving date
        srcHtmlCount=`ls ${src} | grep -c [.]html$`
        if [ ! "${srcHtmlCount}" = "0" ] ; then
            if [ "${DEBUG}" = "true" ] ; then
                echo "publishTestArtifacts: Copying ${srcHtmlCount} html(s)"
                echo "                       from: '${src}'"
                echo "                         to: '${downloadDest}'"
            fi
            cp --preserve=timestamps ${src}/*.html ${downloadDest}/.
        fi
        destHtmlCount=`ls ${downloadDest} | grep -c [.]html$`
        if [ "${DEBUG}" = "true" ] ; then
            echo "publishTestArtifacts: ${destHtmlCount} htmls copied."
        fi

        #verify everything copied correctly
        if [ "${srcHtmlCount}" = "${destHtmlCount}" ] ; then
            echo "    Published ${destHtmlCount} html(s) successfully."
            # can clean up.
            rm -r ${src}
        else
            echo "    Publish failed for Test Results."
            ERROR=true
        fi
    else
        # Something is not right! skipping.."
        echo "    Required locations and data failed to verify... skipping Test Artifact publishing...."
        ERROR=true
        if [ "${DEBUG}" = "true" ] ; then
            echo "publishTestArtifacts: Required locations and data:"
            echo "   src       = '${src}'"
            echo "   rootDest  = '${rootDest}'"
            echo "   version   = '${version}'"
            echo "   date      = '${date}'"
            echo "   timestamp = '${timestamp}'"
        fi
    fi
}


#==========================
#   Main Begins
#
#==========================
#  If anything is in ARG1 then do a dummy "DEBUG"
#  run (Don't call ant, don't remove handoff, do report variable states
DEBUG=false
if [ -n "$ARG1" ] ; then
    DEBUG=true
    echo "Debug is on!"
fi

#==========================
#     Test for handoff
#        if not exit with minimal work done.
curdir=`pwd`
cd $HOME_DIR
NEW_RESULTS=false
ERROR=false
handoff_cnt=0
for handoff in `ls handoff-file*.dat` ; do
    ERROR=false
    handoff_cnt=`expr ${handoff_cnt} + 1`
    if [ "$handoff_cnt" -gt "1" ] ; then
        echo " "
        echo " "
    fi
    echo "Detected handoff file:'${handoff}'. Process starting..."
    # Do stuff
    parseHandoff ${handoff}
    if [ "$PROC" = "build" ] ; then
       establishPublishScope ${BUILD_ARCHIVE_LOC}
       if [ "${PUB_SCOPE_EXPECTED}" -ge 100 ] ; then
           publishBuildArtifacts ${BUILD_ARCHIVE_LOC} ${DNLD_DIR} ${VERSION} ${BLDDATE} ${TIMESTAMP}
       fi
       if [ "${PUB_SCOPE_EXPECTED}" -ge 10 ] ; then
           publishP2Repo ${BUILD_ARCHIVE_LOC} ${DNLD_DIR} ${VERSION} ${QUALIFIER}
       fi
       if [ "${PUB_SCOPE_EXPECTED}" -ge 1 ] ; then
           #publishMavenRepo ${BUILD_ARCHIVE_LOC} ${BRANCH} ${BLDDATE} ${VERSION} ${QUALIFIER}
           echo "run maven if ready... (not yet. need to isolate branch specific dependencies)"
       fi
       if [ "${PUB_SCOPE_EXPECTED}" = "${PUB_SCOPE_COMPLETED}" ] ; then
          echo "Success: can now delete '${handoff}' and '${BUILD_ARCHIVE_LOC}'"
       else
          echo "Full processing failed: Cannot remove '${handoff}' and '${BUILD_ARCHIVE_LOC}'"
          echo "    Deletion aborted..."
       fi
    else
       publishTestArtifacts ${BUILD_ARCHIVE_LOC} ${DNLD_DIR} ${VERSION} ${BLDDATE} ${HOST}
       # Can combine when build publish complete.
       if [ "${ERROR}" = "false" ] ; then
           echo "Processing of '${handoff}' complete."
           # remove handoff
           echo "   Removing '${handoff}'."
           rm ${handoff}
       else
           # Report error
           echo "Error processing of '${handoff}'."
           echo "    Deletion aborted..."
       fi
    fi
    echo "   Finished."
done
echo "Completed processing of all (${handoff_cnt}) handoff files."
if [ "${NEW_RESULTS}" = "true" ] ; then
    # clean up old artifacts
    echo "Could now run '${EXEC_DIR}/cleanNightly.sh' to remove old artifacts."
    # Will need to accumulate branches effected in this run before can know which ones to clean
    # regen web
    echo "Could now run '${EXEC_DIR}/buildNightlyList-cron.sh' to regenerate nightly download page."
    # regen P2 composite
    echo "Could now run '${EXEC_DIR}/buildCompositeP2.sh' to rebuild composite P2."
    #regen composite repo after new child copied.
    echo "TODO: regen composite at ${downloadDest}"
    if [ -f ${EXEC_DIR}/buildCompositeP2.sh ] ; then
        ./buildCompositeP2.sh ${rootDest} "&quot;EclipseLink Nightly Build Repository&quot;"
    fi
fi
echo "Publish complete."

