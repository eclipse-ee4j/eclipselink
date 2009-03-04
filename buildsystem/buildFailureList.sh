# !/bin/sh
#set -x

FailDownloadURL="http://www.eclipse.org/downloads/download.php?file=/rt/eclipselink/recent-failure-logs"
FailDisplayURL="http://download.eclipse.org/rt/eclipselink/recent-failure-logs"
BaseDownloadNFSDir="/home/data/httpd/download.eclipse.org/rt/eclipselink"
buildir=/shared/rt/eclipselink
cd ${buildir}

echo "generating build-failure webpage..."

# safe temp directory
tmp=${TMPDIR-/tmp}
tmp=$tmp/somedir.$RANDOM.$RANDOM.$RANDOM.$$
(umask 077 && mkdir $tmp) || {
  echo "Could not create temporary directory! Exiting." 1>&2 
  exit 1
}

curdir=`pwd`
cd ${BaseDownloadNFSDir}/recent-failure-logs

# Generate the "failed build" table
#    Dump out the table header html
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>                                   " >> $tmp/index.xml
echo "<sections title=\"Eclipse Persistence Services Project (EclipseLink) : Failed Builds logs\">" >> $tmp/index.xml
echo "    <description>                                                            " >> $tmp/index.xml
echo "      <p> Logs are stored for the last 10 failed builds, older logs are available on the build machine.</p>" >> $tmp/index.xml
echo "    </description>                                                           " >> $tmp/index.xml
echo "  <section class=\"main\" name=\"Failed Builds\">                            " >> $tmp/index.xml
echo "    <description>                                                            " >> $tmp/index.xml

echo "      <p>                                                                    " >> $tmp/index.xml
echo "        <table border=\"1\">                                                 " >> $tmp/index.xml
echo "          <tr>                                                               " >> $tmp/index.xml
echo "            <th colspan=\"3\" align=\"center\"> Failed Build Logs </th>      " >> $tmp/index.xml
echo "          </tr>                                                              " >> $tmp/index.xml
    
for file in `ls | grep log | sort -t_ -k3 -r` ; do
    echo "          <tr>"  >> $tmp/index.xml

    # list all files in dir, reverse sort to put newer on top
    # and look for the first matching filename to generate html link
    if [ "${file}" != "" ] ; then
        echo "            <td align=\"center\"> ${file} </td>" >> $tmp/index.xml
        echo "            <td align=\"center\"> <a href=\"${FailDownloadURL}/${file}\"> Download </a> </td>" >> $tmp/index.xml
        echo "            <td align=\"center\"> <a href=\"${FailDisplayURL}/${file}\"> View </a> </td>" >> $tmp/index.xml
    else
        echo "            <td colspan=\"3\" align=\"center\"> Ant log not available </td>" >> $tmp/index.xml
    fi
    
    echo "          </tr>" >> $tmp/index.xml
done

echo "        </table>                                                                          " >> $tmp/index.xml
echo "      </p>                                                                                " >> $tmp/index.xml

# Dump the static footer into place
echo "    </description>                                                                        " >> $tmp/index.xml
echo "  </section>                                                                              " >> $tmp/index.xml
echo "</sections>                                                                               " >> $tmp/index.xml

# Copy the completed file to the server, and cleanup
mv -f $tmp/index.xml  ${BaseDownloadNFSDir}/build-failures.xml
rm -rf $tmp

cd ${curdir}
