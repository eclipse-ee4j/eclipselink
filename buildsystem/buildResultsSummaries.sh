# !/bin/sh
set -x

BaseDownloadNFSDir="/home/data/httpd/download.eclipse.org/rt/eclipselink"
pattern_list="eclipselink-core-[l,s]rg-[0-9] eclipselink-jpa-[l,s]rg-[0-9] eclipselink-jpa-wdf-[l,s]rg-[0-9] eclipselink-jaxb-[l,s]rg-[0-9] eclipselink-oxm-[l,s]rg-[0-9] eclipselink-sdo-[l,s]rg-[0-9] eclipselink-dbws-[l,s]rg-[0-9] eclipselink-dbws-util-[l,s]rg-[0-9]"

#   Generate the results summary file (is a hack just to allow script to generate properly)
#      Results summare in form of: <result filename>:<expected tests>:<tests run>:<errors+failures>
unset genResultSummary
genResultSummary() {
    #    Need to be in dir to generate proper strings
    cd ${BaseDownloadNFSDir}/nightly/${version}/${contentdir}/${hostdir}
    result_file=${BaseDownloadNFSDir}/nightly/${version}/${contentdir}/${hostdir}/ResultSummary.dat
    if [ -f ${result_file} ] ; then
        rm ${result_file}
    fi
    if [ "${last}" = "" ] ; then
        last=${contentdir}
    fi
    
    echo "Creating ${result_file}..."
    touch ${result_file}
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
        echo "${summary}(${failures}:${errors})"
        echo "${summary}" >> ${result_file}
    done 
    cd ${BaseDownloadNFSDir}/nightly/${version}/${contentdir}
    echo "done."
}

cd ${BaseDownloadNFSDir}/nightly
for version in `ls -d [0-9]*` ; do
    cd ${BaseDownloadNFSDir}/nightly/${version}
    last=

    #    Generate each table row depending upon available content
    for contentdir in `ls -d [0-9]*` ; do
        cd ${BaseDownloadNFSDir}/nightly/${version}/${contentdir}

        #    Verify existence of the Eclipse host dir. If not present create and populate as appropriate
        hostdir=Eclipse
        if [ ! -d ${hostdir} ] ; then
            echo "No ${hostdir} dir... creating."
            mkdir ${hostdir}
            cp *.html ${hostdir}/.
            echo "   done."
#        else
#            for migration (clean old html files from nightly content dirs)
#            rm eclipselink-*.html    
        fi

        #parse through host dir's ResultSummary.dat to generate "host results" table entries
        for hostdir in `ls -Fd * | grep / | cut -d"/" -f1` ; do
            genResultSummary
            last=${contentdir}
        done
    done
done

