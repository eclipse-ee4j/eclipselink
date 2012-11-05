# !/bin/sh
#set -x

FailDownloadURL="http://www.eclipse.org/downloads/download.php?file=/rt/eclipselink/recent-failure-logs"
FailDisplayURL="http://download.eclipse.org/rt/eclipselink/recent-failure-logs"
NightlyBuildyURL="http://www.eclipse.org/eclipselink/downloads/nightly.php"
BaseDownloadNFSDir="/home/data/httpd/download.eclipse.org/rt/eclipselink"
buildir=/shared/rt/eclipselink

curyear=`date +%Y`
curmonth=`date +%m`
curdate=`date +%y%m`
curdir=`pwd`

if [ "${curmonth}" -eq 1 ] ; then
   prevmonth=12
   decyear=true
else
    prevmonth=`expr "${curmonth}" - 1`
fi
#echo "prevmonth=${prevmonth}"

if [ "${decyear}" = "true" ] ; then
    prevyear=`expr "${curyear}" - 1`
else
    prevyear=${curyear}
fi
#echo "prevyear=${prevyear}"

prevdate=`date -d "${prevyear}-${prevmonth}-7 11:22:00" +%y%m`
cutoffdate=`date -d "${prevyear}-${prevmonth}-7 11:22:00" +"%b %Y"`

# **** MAIN ****
cd ${BaseDownloadNFSDir}/recent-failure-logs
rmfiles=`ls *.log *.zip | grep -v ${curdate} | grep -v ${prevdate}`
if [ ! "${rmfiles}" = "" ] ; then
    echo "Removing files older than ${cutoffdate} from build-failure site..."
    rm ${rmfiles}
fi

cd ${buildir}
echo "generating build-failure webpage..."

# safe temp directory
tmp=${TMPDIR-/tmp}
tmp=$tmp/somedir.$RANDOM.$RANDOM.$RANDOM.$$
(umask 077 && mkdir $tmp) || {
  echo "Could not create temporary directory! Exiting." 1>&2
  exit 1
}

cd ${BaseDownloadNFSDir}/recent-failure-logs
# Generate the "failed build" table
#    Dump out the table header html
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"                                    >> $tmp/index.xml
echo "<sections title=\"Eclipse Persistence Services Project (EclipseLink) : 'Failed Builds' logs\">" >> $tmp/index.xml
echo "    <description>"                                                             >> $tmp/index.xml
echo "      <p> Logs and cb Test results for the last 2 months 'failed' builds are stored, older logs are available on the build machine, older test results are not.</p>" >> $tmp/index.xml
echo "    </description>"                                                            >> $tmp/index.xml
echo "  <section class=\"main\" name=\"Failed Builds\">"                             >> $tmp/index.xml
echo "    <description>"                                                             >> $tmp/index.xml
echo "      <p>"                                                                     >> $tmp/index.xml
echo "        <table border=\"1\">"                                                  >> $tmp/index.xml
echo "          <tr>"                                                                >> $tmp/index.xml
echo "            <th colspan=\"5\" align=\"center\"> Failed Build Artifacts </th>"  >> $tmp/index.xml
echo "          </tr>"                                                               >> $tmp/index.xml
echo "          <tr>"                                                                >> $tmp/index.xml
echo "            <th colspan=\"3\" align=\"center\"> Dated Build Log </th>"         >> $tmp/index.xml
echo "            <th colspan=\"2\" align=\"center\"> Test results Archive </th>"    >> $tmp/index.xml
echo "          </tr>"                                                               >> $tmp/index.xml

for file in `ls | grep log | sort -t_ -k3 -r` ; do
    echo "          <tr>"   >> $tmp/index.xml
    # Checks to determine failure and build types
    if [ ! "`echo ${file} | grep TestFail`" = "" ] ; then
        TESTFAIL=true
        if [ ! "`echo ${file} | grep nightly`" = "" ] ; then
            NIGHTLYTESTFAIL=true
        fi
    else
        if [ "`echo ${file} | grep BuildFail`" = "" ] ; then
            UNKNOWN_CAUSE=true
        fi
    fi

    # list all files in dir, reverse sort to put newer on top
    # and look for the first matching filename to generate html link
    if [ ! "${file}" = "" ] ; then
        buildtime=`echo ${file} | cut -d'.' -f1 | cut -d_ -f3`
        zipfile=`ls | grep zip | grep ${buildtime}`
        echo "            <td align=\"center\"> ${file} </td>"                                               >> $tmp/index.xml
        echo "            <td align=\"center\"> <a href=\"${FailDownloadURL}/${file}\"> Download </a> </td>" >> $tmp/index.xml
        echo "            <td align=\"center\"> <a href=\"${FailDisplayURL}/${file}\"> View </a> </td>"      >> $tmp/index.xml
    else
        echo "            <td colspan=\"3\" align=\"center\"> Ant log not available </td>"                   >> $tmp/index.xml
    fi
    if [ ! "${zipfile}" = "" ] ; then
        echo "            <td align=\"center\"> ${zipfile} </td>"                                               >> $tmp/index.xml
        echo "            <td align=\"center\"> <a href=\"${FailDownloadURL}/${zipfile}\"> Download </a> </td>" >> $tmp/index.xml
    else
        if [ "${TESTFAIL}" = "true" ] ; then
            if [ "${NIGHTLYTESTFAIL}" = "true" ] ; then
                echo "            <td align=\"center\"> <a href=\"${NightlyBuildyURL}\"> Results on Nightly Site </a> </td>" >> $tmp/index.xml
                echo "            <td align=\"center\"> N/A </td>"                                                           >> $tmp/index.xml
            else
                # Red to indicate a problem 
                echo "            <font color=\"#cc0000\">"                                                     >> $tmp/index.xml
                echo "                <td align=\"center\"> Not Available </td>"                                >> $tmp/index.xml
                echo "                <td align=\"center\"> N/A </td>"                                          >> $tmp/index.xml
                echo "            </font>"                                                                      >> $tmp/index.xml
            fi
        else
            echo "            <td align=\"center\"> Not Available </td>"                                        >> $tmp/index.xml
            echo "            <td align=\"center\"> N/A </td>"                                                  >> $tmp/index.xml
        fi
    fi

    echo "          </tr>"  >> $tmp/index.xml
done

# Dump the static footer into place
echo "        </table>"     >> $tmp/index.xml
echo "      </p>"           >> $tmp/index.xml
echo "    </description>"   >> $tmp/index.xml
echo "  </section>"         >> $tmp/index.xml
echo "</sections>"          >> $tmp/index.xml

# Copy the completed file to the server, and cleanup
mv -f $tmp/index.xml  ${BaseDownloadNFSDir}/build-failures.xml
rm -rf $tmp

cd ${curdir}
