# !/bin/sh

BRANCH=$1
COMMON_BLD_ROOT=/shared/rt/eclipselink
MYSQL_EXEC=/usr/bin/mysql

unset usage
usage() {
    echo " "
    echo "Usage: `basename $0` [branch]"
    echo "  branch  Name of dev branch for which the Test DB will be cleaned."
    echo "          Used to parse the corresponding db-<branch>.properties"
    echo "          file which holds the db connection parameters"
}

if [ ! "${BRANCH}" = "" ] ; then
    if [ -f "${COMMON_BLD_ROOT}/db-${BRANCH}.properties" ] ; then
    # Load properties for specified BRANCH
    DB_USER=`cat ${COMMON_BLD_ROOT}/db-${BRANCH}.properties | grep db[.]user | cut -d'=' -f2`
    DB_PWD=`cat ${COMMON_BLD_ROOT}/db-${BRANCH}.properties | grep db[.]pwd | cut -d'=' -f2`
    DB_NAME=`cat ${COMMON_BLD_ROOT}/db-${BRANCH}.properties | grep db[.]name | cut -d'=' -f2`

    #Clean test db for BRANCH
    echo "'${DB_USER}' user cleaning TestDB (${DB_NAME}) for '${BRANCH}' Branch..."
    echo "drop schema ${DB_NAME};" > ${COMMON_BLD_ROOT}/sql.sql
    echo "create schema ${DB_NAME};" >> ${COMMON_BLD_ROOT}/sql.sql
    ${MYSQL_EXEC} -u${DB_USER} -p${DB_PWD} < ${COMMON_BLD_ROOT}/sql.sql
    rm ${COMMON_BLD_ROOT}/sql.sql
    echo "Complete."
    else
        echo " "
        echo "Error: '${COMMON_BLD_ROOT}/db-${BRANCH}.properties' not found!"
        usage
    fi
else
    usage
fi