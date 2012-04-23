# !/bin/sh
#set -x

export PATH=/usr/bin:/usr/local/bin:${PATH}

GeneratedDownloadPage=nightly.xml
BaseDownloadURL="http://www.eclipse.org/downloads/download.php?file=/rt/eclipselink/nightly"
BaseDisplayURL="http://download.eclipse.org/rt/eclipselink/nightly"
BaseDownloadNFSDir="/home/data/httpd/download.eclipse.org/rt/eclipselink"
pattern_list="eclipselink-core-[l,s]rg-[0-9] eclipselink-jpa-[l,s]rg-[0-9] eclipselink-jpa-wdf-[l,s]rg-[0-9] eclipselink-jaxb-[l,s]rg-[0-9] eclipselink-oxm-[l,s]rg-[0-9] eclipselink-jpars-[l,s]rg-[0-9] eclipselink-sdo-[l,s]rg-[0-9] eclipselink-dbws-[l,s]rg-[0-9] eclipselink-dbws-util-[l,s]rg-[0-9]"
summaryfile=ResultSummary.dat

WarningImg="<img src=\"http://download.eclipse.org/rt/eclipselink/img/warning.gif\" style=\"text-align:center;\" border=\"0\" alt=\"warn\"/>"
PassImg="<img src=\"http://download.eclipse.org/rt/eclipselink/img/pass.gif\" style=\"text-align:center;\" border=\"0\" alt=\"pass\"/>"
FailImg="<img src=\"http://download.eclipse.org/rt/eclipselink/img/fail.gif\" style=\"text-align:center;\" border=\"0\" alt=\"fail\"/>"
NewImg="<img src=\"http://download.eclipse.org/rt/eclipselink/img/new.gif\" style=\"text-align:center;\" border=\"0\" alt=\"new\"/>"
NaImg="<img src=\"http://download.eclipse.org/rt/eclipselink/img/na.gif\" style=\"text-align:center;\" border=\"0\" alt=\"na\"/>"

#   Generate the results summary file (is a hack just to allow script to generate properly)
#      Results summary in form of: <result filename>:<expected tests>:<tests run>:<errors+failures>
unset genResultSummary
genResultSummary() {
    #    Need to save current location
    currdir=`pwd`
    #    Need to be in dir to generate proper strings
    cd ${BaseDownloadNFSDir}/nightly/${version}
    content_dir_index=`ls -dr * | grep -n ${contentdir} | cut -d: -f1`
    prev_content_index=`expr ${content_dir_index} + 1`
    last=`ls -dr * | grep -n . | grep ${prev_content_index}: | cut -d: -f2`
    if [ "${last}" = "" ] ; then
        last=${contentdir}
    fi
    result_file=${BaseDownloadNFSDir}/nightly/${version}/${contentdir}/${hostdir}/${summaryfile}
    if [ -f ${result_file} ] ; then
        rm ${result_file}
    fi
    echo "    Creating ${result_file}..."
    echo "        Previous run dir='$last'"
    touch ${result_file}
    cd ${BaseDownloadNFSDir}/nightly/${version}/${contentdir}/${hostdir}
    for pattern in ${pattern_list} ; do
        file=`ls | sort -r | grep -m1 ${pattern}`
        prev=`ls ${BaseDownloadNFSDir}/nightly/${version}/${last}/${hostdir} | sort -r | grep -m1 ${pattern}`
        if [ "${prev}" != "" ] ; then
            expected=`cat ${BaseDownloadNFSDir}/nightly/${version}/${last}/${hostdir}/${prev} | grep -m1 "^<td>[0-9]" | cut -d">" -f2 | cut -d"<" -f1`
        else
            # If the previous run didn't have this file, then expected result is 0
            expected=0
        fi
        if [ "${file}" != "" ] ; then
            actual=`cat ${file} | grep -m1 "^<td>[0-9]" | cut -d">" -f2 | cut -d"<" -f1`
            if [ ${expected} -eq 0 ] ; then
                expected=${actual}
            fi
            failures=`cat ${file} | grep -m1 "^<td>[0-9]" | cut -d">" -f4 | cut -d"<" -f1`
            errors=`cat ${file} | grep -m1 "^<td>[0-9]" | cut -d">" -f6 | cut -d"<" -f1`
            test_result=`expr ${failures} + ${errors}`
        else
            # If the file doesn't exist (tests weren't run yet) then all values should be zero
            expected=0
            actual=0
            failures=0
            errors=0
            test_result=0
        fi
        summary=${file}:${expected}:${actual}:${test_result}
        echo "    ${summary}(${failures}:${errors})"
        echo "${summary}" >> ${result_file}
    done
    cd ${currdir}
    echo "    done."
}

#   Generate the HTML for appropriate image links based upon results summary file
#      Results summary in form of: <result filename>:<expected tests>:<tests run>:<errors+failures>
unset genResultEntry
genResultEntry() {
    pattern=$1

    Image=${NaImg}
    file=`ls | sort -r | grep -m1 ${pattern}`
    echo "            <td ${borderstyle}>" >> $tmp/index.xml
    if [ "${file}" != "" ] ; then
        summary=`cat ${BaseDownloadNFSDir}/nightly/${version}/${contentdir}/${hostdir}/ResultSummary.dat | grep ${pattern}`
        expected=`echo ${summary} | cut -d: -f2`
        actual=`echo ${summary} | cut -d: -f3`
        test_result=`echo ${summary} | cut -d: -f4`
        if [ $test_result -eq 0 ] ; then
            if [ $expected -gt $actual ] ; then
                Image=${WarningImg}
            fi
            if [ $expected -lt $actual ] ; then
                Image=${NewImg}
            fi
            if [ $expected -eq $actual ] ; then
                Image=${PassImg}
            fi
        else
            Image=${FailImg}
        fi
        echo "              <a href=\"${BaseDisplayURL}/${version}/${contentdir}/${hostdir}/${file}\">" >> $tmp/index.xml
#        echo "                ${Image}<span>er:${expected} ar:${actual} f+e:${test_result}</span>" >> $tmp/index.xml
        echo "                ${Image}" >> $tmp/index.xml
        echo "              </a>" >> $tmp/index.xml
    else
        echo "              ${Image}" >> $tmp/index.xml
    fi
    echo "            </td>" >> $tmp/index.xml
}

unset validateSummaryFile
validateSummaryFile() {
    host=$1

    # create summary file if one doesn't exist (should only be true if host=Eclipse and tests not published yet)
    if [ ! -f ${BaseDownloadNFSDir}/nightly/${version}/${contentdir}/${hostdir}/${summaryfile} ] ; then
        echo "No ${summaryfile} file found in '${BaseDownloadNFSDir}/nightly/${version}/${contentdir}/${host}' directory... creating."
        genResultSummary
        echo "done."
    fi
    # test summary files exist in host dir
    if [ `ls | grep -c .` -gt 1 ] ; then
        #DEBUG
        #echo "Test Summaries exist in Host: '${host}'"
        #echo "pwd="`pwd`
        #echo "host='$host'"
        #echo "sf='${summaryfile}'"

        message=""
        initialSuiteTestCount=`cat ${summaryfile} | grep -m1 . | cut -d: -f2`
        summaryVersion=`cat ${summaryfile} | grep -m1 . | cut -d: -f1 | cut -d. -f4`
        latestTestedBuild=`ls *core* | sort -r | grep -m1 . |  cut -d. -f4` # need to fix other 'hosts' may not publish core

        #DEBUG
        #echo "initialSuiteTestCount='${initialSuiteTestCount}'"
        #echo "summaryVersion='${summaryVersion}'"
        #echo "latestTestedBuild='${latestTestedBuild}'"

        #    if summary is 'blank' (no results), then regen
        if [ ${initialSuiteTestCount} -eq 0 ] ; then
            message="Result Summary 'blank', but test results now exist. Recreating..."
        fi
        #    if summary is 'old' (based upon an older build) then regen
        if [ ! "${summaryVersion}" == "${latestTestedBuild}" ] ; then
            message="Result Summary is old (${summaryVersion}), newer test results now exist (${latestTestedBuild}). Recreating..."
        fi
        if [ ! "${message}" = "" ] ; then
            echo ${message}
            genResultSummary
            echo "done."
        fi
    fi
}

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
#    Need in Php instead
#echo "<style>" >> $tmp/index.xml
#echo "a.info{" >> $tmp/index.xml
#echo "    position:relative; /*this is the key*/" >> $tmp/index.xml
#echo "    z-index:24;" >> $tmp/index.xml
#echo "    text-decoration:none}" >> $tmp/index.xml
#echo "" >> $tmp/index.xml
#echo "a.info:hover{z-index:25; background-color:#ccc}" >> $tmp/index.xml
#echo "" >> $tmp/index.xml
#echo "a.info span{display: none}" >> $tmp/index.xml
#echo "" >> $tmp/index.xml
#echo "a.info:hover span{ /*the span will display just on :hover state*/" >> $tmp/index.xml
#echo "    display:block;" >> $tmp/index.xml
#echo "    position:absolute;" >> $tmp/index.xml
#echo "    top:2em; left:-4em; width:8em;" >> $tmp/index.xml
#echo "    border:1px solid #000;" >> $tmp/index.xml
#echo "    background-color:#eee; color:#000;" >> $tmp/index.xml
#echo "    text-align: center}" >> $tmp/index.xml
#echo "" >> $tmp/index.xml
#echo "th {" >> $tmp/index.xml
#echo " padding-left: 4px;" >> $tmp/index.xml
#echo " padding-right: 4px;" >> $tmp/index.xml
#echo "}" >> $tmp/index.xml
#echo "</style>" >> $tmp/index.xml
echo "<sections title=\"Eclipse Persistence Services Project (EclipseLink) : Nightly Builds\">" >> $tmp/index.xml
echo "    <description>                                                            " >> $tmp/index.xml
echo "      <p> Automated builds and the corresponding Javadocs are created every day, if code has changed, and are made available for download.  The process is kicked off shortly after midnight Eastern Time.</p>" >> $tmp/index.xml
echo "    </description>                                                           " >> $tmp/index.xml
echo "  <section class=\"main\" name=\"Nightly Builds\">                           " >> $tmp/index.xml
echo "    <description>                                                            " >> $tmp/index.xml
echo "      <p><a href=\"http://download.eclipse.org/rt/eclipselink/nightly-updates\">Nightly Build P2 Update Site</a></p>" >> $tmp/index.xml
echo "      <p>                                                                    " >> $tmp/index.xml
echo "        <table border=\"1\">                                                 " >> $tmp/index.xml
echo "          <tr>                                                               " >> $tmp/index.xml
echo "            <th colspan=\"5\" style=\"text-align:left;\"> Symbol Key </th>            " >> $tmp/index.xml
echo "          </tr>                                                              " >> $tmp/index.xml
echo "          <tr>                                                               " >> $tmp/index.xml
echo "            <td style=\"text-align:center;\">                                            " >> $tmp/index.xml
echo "              ${PassImg} = clean, as expected     " >> $tmp/index.xml
echo "            </td>                                                                                                                            " >> $tmp/index.xml
echo "            <td style=\"text-align:center;\">                                                                                                            " >> $tmp/index.xml
echo "              ${NewImg} = clean, new tests added  " >> $tmp/index.xml
echo "            </td>                                                                                                                            " >> $tmp/index.xml
echo "            <td style=\"text-align:center;\">                                                                                                            " >> $tmp/index.xml
echo "              ${WarningImg} = clean, fewer tests run" >> $tmp/index.xml
echo "            </td>                                                                                                                            " >> $tmp/index.xml
echo "            <td style=\"text-align:center;\">                                                                                                            " >> $tmp/index.xml
echo "              ${FailImg} = test failures or errors" >> $tmp/index.xml
echo "            </td>                                                                                                                            " >> $tmp/index.xml
echo "            <td style=\"text-align:center;\">                                                                                                            " >> $tmp/index.xml
echo "              ${NaImg} = no results                 " >> $tmp/index.xml
echo "            </td>" >> $tmp/index.xml
echo "          </tr>" >> $tmp/index.xml
echo "        </table>" >> $tmp/index.xml
echo "      </p>" >> $tmp/index.xml

curdir=`pwd`
for version in `ls -dr [0-9]*` ; do
    cd ${BaseDownloadNFSDir}/nightly/${version}
    echo "      <p>                                                                              " >> $tmp/index.xml
    echo "      <a name=\"${version}\"> </a>                                                     " >> $tmp/index.xml
    echo "        <table border=\"1\">                                                           " >> $tmp/index.xml
    echo "          <tr>                                                                         " >> $tmp/index.xml
    echo "            <th colspan=\"13\" style=\"text-align:left;\"><b>${version} Nightly Build Results</b></th>" >> $tmp/index.xml
    echo "          </tr>                                                                        " >> $tmp/index.xml
    echo "          <tr>                                                                         " >> $tmp/index.xml
    echo "            <th rowspan=\"3\" style=\"text-align:center;border-top: 2px solid #444;\"> Build ID </th>                         " >> $tmp/index.xml
    echo "            <th rowspan=\"3\" style=\"text-align:center;border-top: 2px solid #444;\"> Downloadable Archives </th>                         " >> $tmp/index.xml
    echo "            <th rowspan=\"3\" style=\"text-align:center;\"> </th>                                  " >> $tmp/index.xml
    echo "            <th colspan=\"10\" style=\"text-align:center;border-top: 2px solid #444;\"> Nightly Testing Results </th>          " >> $tmp/index.xml
    echo "          </tr>                                                                        " >> $tmp/index.xml
    echo "          <tr>                                                                         " >> $tmp/index.xml
    echo "            <th rowspan=\"2\" style=\"text-align:center;\"> Host </th>                             " >> $tmp/index.xml
    echo "            <th rowspan=\"2\" style=\"text-align:center;\"> Core </th>                             " >> $tmp/index.xml
    echo "            <th colspan=\"2\" style=\"text-align:center;\"> JPA </th>                              " >> $tmp/index.xml
    echo "            <th colspan=\"2\" style=\"text-align:center;\"> MOXy </th>                             " >> $tmp/index.xml
    echo "            <th rowspan=\"1\" style=\"text-align:center;\"> JPA </th>                              " >> $tmp/index.xml
    echo "            <th rowspan=\"2\" style=\"text-align:center;\"> SDO </th>                              " >> $tmp/index.xml
    echo "            <th colspan=\"2\" style=\"text-align:center;\"> DBWS </th>                             " >> $tmp/index.xml
    echo "          </tr>                                                                        " >> $tmp/index.xml
    echo "          <tr>                                                                         " >> $tmp/index.xml
    echo "            <th style=\"text-align:center;\"> OTT </th>                                            " >> $tmp/index.xml
    echo "            <th style=\"text-align:center;\"> WDF </th>                                            " >> $tmp/index.xml
    echo "            <th style=\"text-align:center;\"> JAXB </th>                                           " >> $tmp/index.xml
    echo "            <th style=\"text-align:center;\"> OXM </th>                                            " >> $tmp/index.xml
    echo "            <th style=\"text-align:center;\"> RS </th>                                             " >> $tmp/index.xml
    echo "            <th style=\"text-align:center;\"> RT </th>                                             " >> $tmp/index.xml
    echo "            <th style=\"text-align:center;\"> Util </th>                                           " >> $tmp/index.xml
    echo "          </tr>                                                                        " >> $tmp/index.xml

    #    Generate each table row depending upon available content
    for contentdir in `ls -dr [0-9]*` ; do
        cd ${BaseDownloadNFSDir}/nightly/${version}/${contentdir}
        #    Determine number of "host" result dirs - since we will always report for "eclipse", even if none result needs to be 1
        num_hosts=`ls -Fd * | grep -c /`
        if [ ${num_hosts} -eq 0 ] ; then
            num_hosts=1
        fi

        echo "          <tr>"  >> $tmp/index.xml
        echo "            <td rowspan=\"${num_hosts}\" style=\"text-align:center;border-top: 2px solid #444;\"> ${contentdir} </td>" >> $tmp/index.xml
        echo "            <td rowspan=\"${num_hosts}\" style=\"text-align:center;border-top: 2px solid #444;\">" >> $tmp/index.xml

        #    List all files in dir, reverse sort to put newer on top
        #    and look for the first matching filename to generate html link
        file=`ls | sort -r | grep -m1 eclipselink-[0-9]`
        if [ "${file}" != "" ] ; then
#            echo "              <a href=\"${BaseDownloadURL}/${version}/${contentdir}/${file}\" class=\"info\"><img src=\"http://dev.eclipse.org/large_icons/actions/go-bottom.png\"/><span>Download Install Archive</span></a>" >> $tmp/index.xml
            echo "              <a href=\"${BaseDownloadURL}/${version}/${contentdir}/${file}\">Installer</a>" >> $tmp/index.xml
        else
            echo "              Z" >> $tmp/index.xml
        fi
        file=`ls | sort -r | grep -m1 eclipselink-src-[0-9]`
        if [ "${file}" != "" ] ; then
#            echo "              <a href=\"${BaseDownloadURL}/${version}/${contentdir}/${file}\" class=\"info\">S<span>Download Source Archive</span></a>" >> $tmp/index.xml
            echo "              <a href=\"${BaseDownloadURL}/${version}/${contentdir}/${file}\">Source</a>" >> $tmp/index.xml
        else
            echo "              S" >> $tmp/index.xml
        fi
        file=`ls | sort -r | grep -m1 eclipselink-plugins-[0-9]`
        if [ "${file}" != "" ] ; then
#            echo "              <a href=\"${BaseDownloadURL}/${version}/${contentdir}/${file}\" class=\"info\">B<span>Download OSGi Plugins Archive</span></a>" >> $tmp/index.xml
            echo "              <a href=\"${BaseDownloadURL}/${version}/${contentdir}/${file}\">Bundle</a>" >> $tmp/index.xml
        else
            echo "              B" >> $tmp/index.xml
        fi
        echo "            </td>" >> $tmp/index.xml
        echo "            <td rowspan=\"${num_hosts}\" style=\"text-align:center;\"> </td>" >> $tmp/index.xml

        #    Verify existence of the Eclipse host dir. If not present create and populate as appropriate
        hostdir=Eclipse
        if [ ! -d ${hostdir} ] ; then
            echo "No ${hostdir} dir... creating."
            mkdir ${hostdir}
            mv eclipselink-*.html ${hostdir}/.
            echo "done."
        fi

        #   Set a counter to track the number of times through the "hosts" loop
        count=0
        borderstyle="style=\"text-align:center;border-top: 2px solid #444;\""
        #parse through host dir's ResultSummary.dat to generate "host results" table entries
        for hostdir in `ls -Fd * | grep / | cut -d"/" -f1` ; do
            #    Need to be in dir to generate proper strings
            cd ${BaseDownloadNFSDir}/nightly/${version}/${contentdir}/${hostdir}
            #    increment count for number of times through (which host is this)
            count=`expr $count + 1`
            #    Set border to none, and Add row if this is after the first time through
            if [ ${count} -gt 1 ] ; then
                borderstyle="style=\"text-align:center;\""
                echo "            <tr>" >> $tmp/index.xml
            fi

            # Check summary file is up-to-date and valid
            validateSummaryFile ${hostdir}

            #   Add "Host" entry
            echo "            <td ${borderstyle}>" >> $tmp/index.xml
            if [ -f "${BaseDownloadNFSDir}/nightly/${version}/${contentdir}/${hostdir}/TestConfiguration.html" ] ; then
                echo "              <a href=\"${BaseDisplayURL}/${version}/${contentdir}/${hostdir}/TestConfiguration.html\"> ${hostdir} </a>" >> $tmp/index.xml
            else
                echo "              ${hostdir}" >> $tmp/index.xml
            fi
            echo "            </td>" >> $tmp/index.xml
            #   Generate the image links
            genResultEntry eclipselink-core-[l,s]rg-[0-9]
            genResultEntry eclipselink-jpa-[l,s]rg-[0-9]
            genResultEntry eclipselink-jpa-wdf-[l,s]rg-[0-9]
            genResultEntry eclipselink-jaxb-[l,s]rg-[0-9]
            genResultEntry eclipselink-oxm-[l,s]rg-[0-9]
            genResultEntry eclipselink-jpars-[l,s]rg-[0-9]
            genResultEntry eclipselink-sdo-[l,s]rg-[0-9]
            genResultEntry eclipselink-dbws-[l,s]rg-[0-9]
            genResultEntry eclipselink-dbws-util-[l,s]rg-[0-9]
            #    Close row if this is after the first time through
            if [ ${count} -gt 1 ] ; then
                echo "            </tr>" >> $tmp/index.xml
            fi
        done

        echo "          </tr>" >> $tmp/index.xml
    done
    echo "        </table>" >> $tmp/index.xml
    echo "      </p>      " >> $tmp/index.xml
    cd ${curdir}
done

# Dump the static footer into place
echo "    </description>                                                                        " >> $tmp/index.xml
echo "  </section>                                                                              " >> $tmp/index.xml
echo "  <section class=\"main\" name=\"EclipseLink Licenses\">                                  " >> $tmp/index.xml
echo "    <description>                                                                         " >> $tmp/index.xml
echo "      <p>                                                                                 " >> $tmp/index.xml
echo "        The EclipseLink Project produced contents are dual licensed under the terms of the" >> $tmp/index.xml
echo "        <a href=\"http://www.eclipse.org/legal/epl-v10.html\">Eclipse Public License v1.0</a>" >> $tmp/index.xml
echo "      and <a href=\"http://www.eclipse.org/org/documents/edl-v10.php\">Eclipse Distribution License v1.0</a>." >> $tmp/index.xml
echo "      </p><p>                                                                             " >> $tmp/index.xml
echo "        For the license of dependent libraries included within a distribution please refer to the about.html file within each distribution." >> $tmp/index.xml
echo "      </p>                                                                                " >> $tmp/index.xml
echo "    </description>                                                                        " >> $tmp/index.xml
echo "  </section>                                                                              " >> $tmp/index.xml
echo "  <section class=\"main\" name=\" \">                                                     " >> $tmp/index.xml
echo "    <description>                                                                         " >> $tmp/index.xml
echo "      <p>Generated on `date`</p>                                                          " >> $tmp/index.xml
echo "      <script src=\"http://www.google-analytics.com/urchin.js\" type=\"text/javascript\"/>" >> $tmp/index.xml
echo "      <script type=\"text/javascript\">                                                   " >> $tmp/index.xml
echo "        _uacct = \"UA-1608008-2\";                                                        " >> $tmp/index.xml
echo "        urchinTracker();                                                                  " >> $tmp/index.xml
echo "      </script>                                                                           " >> $tmp/index.xml
echo "    </description>                                                                        " >> $tmp/index.xml
echo "  </section>                                                                              " >> $tmp/index.xml
echo "</sections>                                                                               " >> $tmp/index.xml

# Copy the completed file to the server, and cleanup
mv -f $tmp/index.xml  ${BaseDownloadNFSDir}/${GeneratedDownloadPage}
rm -rf $tmp
