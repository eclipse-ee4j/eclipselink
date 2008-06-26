# !/bin/sh
#set -x

export JAVA_HOME=/shared/common/ibm-java2-ppc-50
export PATH=${JAVA_HOME}/bin:/usr/bin:/usr/local/bin:${PATH}

BaseDownloadURL="http://www.eclipse.org/downloads/download.php?file=/rt/eclipselink/nightly"
BaseDisplayURL="http://download.eclipse.org/rt/eclipselink/nightly"
BaseDownloadNFSDir="/home/data/httpd/download.eclipse.org/rt/eclipselink"
buildir=/shared/rt/eclipselink
cd ${buildir}

echo "generating webpage..."

# safe temp directory
tmp=${TMPDIR-/tmp}
tmp=$tmp/somedir.$RANDOM.$RANDOM.$RANDOM.$$
(umask 077 && mkdir $tmp) || {
  echo "Could not create temporary directory! Exiting." 1>&2 
  exit 1
}

cd ${BaseDownloadNFSDir}/nightly

# Generate the nightly build table
#    Dump out the table header html
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>                                   " >> $tmp/index.xml
echo "<sections title=\"Eclipse Persistence Services Project (EclipseLink) : Nightly Builds\">" >> $tmp/index.xml
echo "    <description>                                                            " >> $tmp/index.xml
echo "      <p> Automated builds and the corresponding Javadocs are created every Sunday - Thursday and are made available for download.  The process is kicked off shortly after midnight Eastern Time.</p>" >> $tmp/index.xml
echo "    </description>                                                           " >> $tmp/index.xml
echo "  <section class=\"main\" name=\"Nightly Builds\">                           " >> $tmp/index.xml
echo "    <description>                                                            " >> $tmp/index.xml
echo "      <p>                                                                    " >> $tmp/index.xml

curdir=`pwd`
for version in `ls -dr [0-9]*` ; do
    cd ${version}
    echo "        <table border=\"1\">                                                 " >> $tmp/index.xml
    echo "          <tr>                                                               " >> $tmp/index.xml
    echo "            <th colspan=\"9\" align=\"center\"> ${version} Nightly Build Results </th>" >> $tmp/index.xml
    echo "          </tr>                                                              " >> $tmp/index.xml
    echo "          <tr>                                                               " >> $tmp/index.xml
    echo "            <th align=\"center\"> Build ID </th>                             " >> $tmp/index.xml
    echo "            <th align=\"center\"> Archives </th>                             " >> $tmp/index.xml
    echo "            <th align=\"center\"> </th>                                      " >> $tmp/index.xml
    echo "            <th colspan=\"5\" align=\"center\"> Nightly Testing Results </th>" >> $tmp/index.xml
    echo "          </tr>                                                              " >> $tmp/index.xml
    
    #    Generate each table row depending upon available content
    for contentdir in `ls -dr [0-9]*` ; do
        echo "          <tr>"  >> $tmp/index.xml
        echo "            <td align=\"center\"> ${contentdir} </td>" >> $tmp/index.xml
        echo "            <td align=\"center\">" >> $tmp/index.xml
        if [ -f ${contentdir}/eclipselink-incubation-${contentdir}.zip ] ; then
            echo "              <a href=\"${BaseDownloadURL}/${version}/${contentdir}/eclipselink-incubation-${contentdir}.zip\"> Install Archive </a> <br/>" >> $tmp/index.xml
        else
            echo "              Install archive not available <br/>" >> $tmp/index.xml
        fi
        if [ -f ${contentdir}/eclipselink-incubation-src-${contentdir}.zip ] ; then
            echo "              <a href=\"${BaseDownloadURL}/${version}/${contentdir}/eclipselink-incubation-src-${contentdir}.zip\"> Source Archive </a> <br/>" >> $tmp/index.xml
        else
            echo "              Source archive not available <br/>" >> $tmp/index.xml
        fi
        if [ -f ${contentdir}/eclipselink-plugins-incubation-${contentdir}.zip ] ; then
            echo "              <a href=\"${BaseDownloadURL}/${version}/${contentdir}/eclipselink-plugins-incubation-${contentdir}.zip\"> OSGi Plugins Archive </a> <br/>" >> $tmp/index.xml
        else
            echo "              OSGi Plugins archive not available <br/>" >> $tmp/index.xml
        fi
        echo "            </td>" >> $tmp/index.xml
        echo "            <td align=\"center\"> </td>" >> $tmp/index.xml
        if [ -f ${contentdir}/eclipselink-core-lrg-${contentdir}.html ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/eclipselink-core-lrg-${contentdir}.html\"> CoreLRG </a> </td>" >> $tmp/index.xml
        else
            if [ -f ${contentdir}/eclipselink-core-srg-${contentdir}.html ] ; then
                echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/eclipselink-core-srg-${contentdir}.html\"> CoreSRG </a> </td>" >> $tmp/index.xml
            else
                echo "            <td align=\"center\"> Core </td>" >> $tmp/index.xml
            fi
        fi
        if [ -f ${contentdir}/eclipselink-jpa-lrg-${contentdir}.html ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/eclipselink-jpa-lrg-${contentdir}.html\"> JPA </a> </td>" >> $tmp/index.xml
        else
            echo "            <td align=\"center\"> JPA </td>" >> $tmp/index.xml
        fi
        if [ -f ${contentdir}/eclipselink-jaxb-lrg-${contentdir}.html ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/eclipselink-jaxb-lrg-${contentdir}.html\"> Moxy (JAXB) </a> </td>" >> $tmp/index.xml
        else
            echo "            <td align=\"center\"> Moxy (JAXB) </td>" >> $tmp/index.xml
        fi
        if [ -f ${contentdir}/eclipselink-oxm-lrg-${contentdir}.html ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/eclipselink-oxm-lrg-${contentdir}.html\"> Moxy (OXM) </a> </td>" >> $tmp/index.xml
        else
            echo "            <td align=\"center\"> Moxy (OXM) </td>" >> $tmp/index.xml
        fi
        if [ -f ${contentdir}/eclipselink-sdo-lrg-${contentdir}.html ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/eclipselink-sdo-lrg-${contentdir}.html\"> SDO </a> </td>" >> $tmp/index.xml
        else
            echo "            <td align=\"center\"> SDO </td>" >> $tmp/index.xml
        fi
        echo "          </tr>" >> $tmp/index.xml
    done
    echo "        </table>                                                                          " >> $tmp/index.xml
    echo "      </p>                                                                                " >> $tmp/index.xml
    cd ${curdir}
done

# Dump the static footer into place
echo "      <script src=\"http://www.google-analytics.com/urchin.js\" type=\"text/javascript\"/>" >> $tmp/index.xml
echo "      <script type=\"text/javascript\">                                                   " >> $tmp/index.xml
echo "        _uacct = \"UA-1608008-2\";                                                        " >> $tmp/index.xml
echo "        urchinTracker();                                                                  " >> $tmp/index.xml
echo "      </script>                                                                           " >> $tmp/index.xml
echo "    </description>                                                                        " >> $tmp/index.xml
echo "  </section>                                                                              " >> $tmp/index.xml
echo "</sections>                                                                               " >> $tmp/index.xml

# Copy the completed file to the server, and cleanup
mv -f $tmp/index.xml  ${BaseDownloadNFSDir}/downloads.xml
rm -rf $tmp
