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

#Files
ANT_BLDFILE=publishbuild.xml

# Need to export after parsing:
# BLD_DEPS_DIR
# BRANCH
# GITHASH
# BLDDATE
# handoff

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
    #Need handoff_loc, download location, version, date/time, qualifier
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
                else
                    echo "    Publish failed for P2 Repo. Only copied ${destP2jarCount} of ${srcP2jarCount} jars to repo."
                    ERROR=true
                    rm -r ${downloadDest}
                fi
            else
                destP2jarCount=`ls -r ${downloadDest}/* | grep -c [.]jar$`
                echo "   P2 repo already exists, ${destP2jarCount} of ${srcP2jarCount} jars published. Skipping..."
            fi
        else
            echo "    No P2 repo to publish..."
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
    #Need handoff_loc, branch, date, version
    src=$1
    branch=$2
    blddate=$3
    version=$4

    # Define SYSTEM variables needed
    CLASSPATH

    # verify latest git repo, if none exist, create it.
    if [ -d ${RUNTIME_REPO} ] ; then
       cd ${RUNTIME_REPO}
       git pull
    else
       git clone http://git.eclipse.org/gitroot/eclipselink/eclipselink.runtime.git ${RUNTIME_REPO}
       cd ${RUNTIME_REPO}
    fi

    # checkout branch from runtime git repo
    git checkout ${branch}

    # unzip necessary files to 'upload dir'/plugins
    #unzip ${handoff_loc}/ # better in ant task??? (autobuild? - populate variables, then invoke?

    #Invoke Antscript for Maven upload
    arguments="-Drelease.version=${version} -Dbuild.date=${blddate} -Dbuild.type=SNAPSHOT"

    # Run Ant from ${exec_location} using ${buildfile} ${arguments}
    runAnt ${exec_location} ${upload_src_dir}/uploadToMaven.xml "${arguments}"

    # if encountered error, increment error_cnt, otherwise, cleanup ${upload_src_dir}
    if [ "${local_err}" = "true" ] ; then
       error_cnt=`expr ${error_cnt} + 1`
    else
       echo "should 'rm -r ${upload_src_dir}', but won't for debugging for now."
       #rm -r ${upload_src_dir}
    fi

    # return to HOME_DIR
    cd $HOME_DIR
}

unset runAnt
runAnt() {
    #Define run specific variables
    run_dir=$1
    buildfile=$2
    args=$3

    # Store current location

    # Change to run_dir

    # Invoke Ant

    #Directories
#    BRANCH_PATH=${WORKSPACE}
#    BLD_DEPS_DIR=${HOME_DIR}/bld_deps/${BRANCH}
#    JUNIT_HOME=${BLD_DEPS_DIR}/junit

    #Files
#    JDBC_LOGIN_INFO_FILE=${HOME_DIR}/db-${BRANCH_NM}.dat
    LOGFILE_NAME=bsb-${BRANCH_NM}_pub${PROC}_${START_DATE}.log
    DATED_LOG=${LOG_DIR}/${LOGFILE_NAME}

    # Define build dependency paths (build needs em, NOT compile dependencies)
#    OLD_CLASSPATH=${CLASSPATH}
#   CLASSPATH=${JUNIT_HOME}/junit.jar:${ANT_HOME}/lib/ant-junit.jar

    # Export run specific variables
    export BLD_DEPS_DIR BRANCH_NM BRANCH_PATH CLASSPATH JUNIT_HOME TARGET

    # Setup parameters for Ant build
    ANT_BASEARG="-f \"${ant.buildfile}\" -Dbranch.name=\"${BRANCH}\" -Dgit.hash=${GITHASH}"
    ANT_BASEARG="${ANT_BASEARG} -Dbuild.date=${BLDDATE} -Dhandoff.file=${handoff}"

    cd ${HOME_DIR}
    echo "Results logged to: ${DATED_LOG}"
    touch ${DATED_LOG}

    echo ""
    echo "${PROC} publish starting for ${BRANCH} build:${QUALIFIER} from '`pwd`'..."
    echo "${PROC} publish started at: '`date`' for ${BRANCH} build:${QUALIFIER} from '`pwd`'..." >> ${DATED_LOG}
    echo "   ant ${ANT_BASEARG} publish-${PROC}"
    echo "ant ${ANT_BASEARG} publish-${PROC}" >> ${DATED_LOG}
    if [ ! "${DEBUG}" = "true" ] ; then
        #ant ${ANT_BASEARG} publish-${PROC} >> ${DATED_LOG} 2>&1
        echo "Not running: ant ${ANT_BASEARG} publish-${PROC}"
    fi
    echo "Publish completed (skipped) at: `date`" >> ${DATED_LOG}
    echo "Publish complete."
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
        else
            echo "    Publish failed for Test Results."
        fi
    else
        # Something is not right! skipping.."
        echo "    Required locations and data failed to verify... skipping Artifact publishing...."
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
    handoff_cnt=`expr ${handoff_cnt} + 1`
    if [ "$handoff_cnt" -gt "1" ] ; then
        echo " "
        echo " "
    fi
    echo "Detected handoff file:'${handoff}'. Process starting..."
    # Do stuff
    parseHandoff ${handoff}
    if [ "$PROC" = "build" ] ; then
       publishBuildArtifacts ${BUILD_ARCHIVE_LOC} ${DNLD_DIR} ${VERSION} ${BLDDATE} ${TIMESTAMP}
       publishP2Repo ${BUILD_ARCHIVE_LOC} ${DNLD_DIR} ${VERSION} ${QUALIFIER}

    else
       publishTestArtifacts ${BUILD_ARCHIVE_LOC} ${DNLD_DIR} ${VERSION} ${BLDDATE} ${HOST}
    fi
    #runAnt ${BRANCH} ${QUALIFIER} ${PROC} ${BUILD_ARCHIVE_LOC} ${HOST}
 # ${DNLD_DIR}
 # ${BRANCH}
 # ${QUALIFIER}
 # ${PROC}
 # ${BLDDATE}
 # ${GITHASH}
 # ${BUILD_ARCHIVE_LOC}
 # ${HOST}
 # ${MAVEN_TAG}
 # ${VERSION}
 # ${TIMESTAMP}
    if [ "${ERROR}" = "false" ] ; then
        echo "Processing of '${handoff}' complete."
        # remove handoff
        #rm ${handoff}
        echo "   Removing '${handoff}'. (not!)"
        # remove exported artifacts
        #rm -r ${BUILD_ARCHIVE_LOC} # Currently cannot because build/test are not separate locations (build would delete tests before published...
        #rm ${BUILD_ARCHIVE_LOC}/*  # Gets rid of the bulk of the artifacts, but will leave the dir heirarchy
        echo "   Removing '${BUILD_ARCHIVE_LOC}/*'. (not!)"
    else
        # Repost error
        echo "Error processing of '${handoff}'."
        echo "    Deletion aborted..."
    fi
    echo "   Finished."
done
echo "Completed processing of all (${handoff_cnt}) handoff files."
if [ "${NEW_RESULTS}" = "true" ] ; then
    # clean up old artifacts
    echo "Could now run '${EXEC_DIR}/cleanNightly.sh' to remove old artifacts."
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

