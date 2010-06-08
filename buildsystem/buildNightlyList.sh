# !/bin/sh
#set -x

export JAVA_HOME=/shared/common/ibm-java-jdk-ppc-60
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

curdir=`pwd`
for version in `ls -dr [0-9]*` ; do
    cd ${version}
    echo "      <p>                                                                    " >> $tmp/index.xml
    echo "      <a name=\"${version}\"> </a>                                           " >> $tmp/index.xml
    echo "        <table border=\"1\">                                                 " >> $tmp/index.xml
    echo "          <tr>                                                               " >> $tmp/index.xml
    echo "            <th colspan=\"11\" align=\"center\"> ${version} Nightly Build Results </th>" >> $tmp/index.xml
    echo "          </tr>                                                              " >> $tmp/index.xml
    echo "          <tr>                                                               " >> $tmp/index.xml
    echo "            <th align=\"center\"> Build ID </th>                             " >> $tmp/index.xml
    echo "            <th align=\"center\"> Archives </th>                             " >> $tmp/index.xml
    echo "            <th align=\"center\"> </th>                                      " >> $tmp/index.xml
    echo "            <th colspan=\"8\" align=\"center\"> Nightly Testing Results </th>" >> $tmp/index.xml
    echo "          </tr>                                                              " >> $tmp/index.xml
    
    #    Generate each table row depending upon available content
    for contentdir in `ls -dr [0-9]*` ; do
        cd $contentdir
        echo "          <tr>"  >> $tmp/index.xml
        echo "            <td align=\"center\"> ${contentdir} </td>" >> $tmp/index.xml
        echo "            <td align=\"center\">" >> $tmp/index.xml

        # list all files in dir, reverse sort to put newer on top
        # and look for the first matching filename to generate html link
        file=`ls | sort -r | grep -m1 eclipselink-[0-9]`
        if [ "${file}" != "" ] ; then
            echo "              <a href=\"${BaseDownloadURL}/${version}/${contentdir}/${file}\"> Install Archive </a> <br/>" >> $tmp/index.xml
        else
            echo "              Install archive not available <br/>" >> $tmp/index.xml
        fi
        file=`ls | sort -r | grep -m1 eclipselink-src-[0-9]`
        if [ "${file}" != "" ] ; then
            echo "              <a href=\"${BaseDownloadURL}/${version}/${contentdir}/${file}\"> Source Archive </a> <br/>" >> $tmp/index.xml
        else
            echo "              Source archive not available <br/>" >> $tmp/index.xml
        fi
        file=`ls | sort -r | grep -m1 eclipselink-plugins-[0-9]`
        if [ "${file}" != "" ] ; then
            echo "              <a href=\"${BaseDownloadURL}/${version}/${contentdir}/${file}\"> OSGi Plugins Archive </a> <br/>" >> $tmp/index.xml
        else
            echo "              OSGi Plugins archive not available <br/>" >> $tmp/index.xml
        fi
        echo "            </td>" >> $tmp/index.xml
        echo "            <td align=\"center\"> </td>" >> $tmp/index.xml
        file=`ls | sort -r | grep -m1 eclipselink-core-[l,s]rg-[0-9]`
        if [ "${file}" != "" ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/${file}\"> CoreSRG </a> </td>" >> $tmp/index.xml
        else
            echo "            <td align=\"center\"> Core </td>" >> $tmp/index.xml
        fi
        file=`ls | sort -r | grep -m1 eclipselink-jpa-[l,s]rg-[0-9]`
        if [ "${file}" != "" ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/${file}\"> JPA </a> </td>" >> $tmp/index.xml
        else
            echo "            <td align=\"center\"> JPA </td>" >> $tmp/index.xml
        fi
        file=`ls | sort -r | grep -m1 eclipselink-jpa-wdf-[l,s]rg-[0-9]`
        if [ "${file}" != "" ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/${file}\"> JPA (WDF) </a> </td>" >> $tmp/index.xml
            #else
                #echo "            <td align=\"center\"> JPA (WDF) </td>" >> $tmp/index.xml
        fi
        fi
        file=`ls | sort -r | grep -m1 eclipselink-jaxb-[l,s]rg-[0-9]`
        if [ "${file}" != "" ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/${file}\"> MOXy (JAXB) </a> </td>" >> $tmp/index.xml
        else
            echo "            <td align=\"center\"> MOXy (JAXB) </td>" >> $tmp/index.xml
        fi
        file=`ls | sort -r | grep -m1 eclipselink-oxm-[l,s]rg-[0-9]`
        if [ "${file}" != "" ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/${file}\"> MOXy (OXM) </a> </td>" >> $tmp/index.xml
        else
            echo "            <td align=\"center\"> MOXy (OXM) </td>" >> $tmp/index.xml
        fi
        file=`ls | sort -r | grep -m1 eclipselink-sdo-[l,s]rg-[0-9]`
        if [ "${file}" != "" ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/${file}\"> SDO </a> </td>" >> $tmp/index.xml
        else
            echo "            <td align=\"center\"> SDO </td>" >> $tmp/index.xml
        fi
        file=`ls | sort -r | grep -m1 eclipselink-dbws-[l,s]rg-[0-9]`
        if [ "${file}" != "" ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/${file}\"> DBWS </a> </td>" >> $tmp/index.xml
        else
            echo "            <td align=\"center\"> DBWS </td>" >> $tmp/index.xml
        fi
        file=`ls | sort -r | grep -m1 eclipselink-dbws-util-[l,s]rg-[0-9]`
        if [ "${file}" != "" ] ; then
            echo "            <td align=\"center\"> <a href=\"${BaseDisplayURL}/${version}/${contentdir}/${file}\"> DBWS Util </a> </td>" >> $tmp/index.xml
        else
            echo "            <td align=\"center\"> DBWS Util </td>" >> $tmp/index.xml
        fi
        echo "          </tr>" >> $tmp/index.xml
        cd ..
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
