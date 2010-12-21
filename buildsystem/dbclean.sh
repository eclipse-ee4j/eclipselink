# !/bin/sh

SETUP=FALSE
ARG1=$1

if [ "$ARG1" = "setup" ]
then 
    SETUP=true
fi

unset initSchema
initSchema() {
    echo "Removing db schema for ${DB_NAME}..."
    echo "DROP DATABASE IF EXISTS ${DB_NAME};" > sql.sql
    echo "Creating db schema for ${DB_NAME}..."
    echo "CREATE DATABASE IF NOT EXISTS ${DB_NAME};" >> sql.sql
    echo "Creating user rights for user ${DB_USER} on db ${DB_NAME}..."
    echo "GRANT ALL PRIVILEGES ON ${DB_NAME}.* to '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PWD}' WITH GRANT OPTION;" >> sql.sql
    echo "GRANT ALL PRIVILEGES ON ${DB_NAME}.* to '${DB_USER}'@'%' IDENTIFIED BY '${DB_PWD}' WITH GRANT OPTION;" >> sql.sql
    mysql -uroot -p < sql.sql
    rm sql.sql
    echo "init  complete."
}

unset cleanSchema
cleanSchema() {
    echo "Cleaning the db schema for ${DB_NAME}..."
    echo "drop schema ${DB_NAME};" > sql.sql
    echo "create schema ${DB_NAME};" >> sql.sql
    mysql -u${DB_USER} -p${DB_PWD} < sql.sql
    rm sql.sql
    echo "done."
}

loginfound=false
processed=""
for dbLoginFile in `ls | grep db-` ; do
    loginfound=true
    DB_USER=`cat $dbLoginFile | cut -d'*' -f1`
    DB_PWD=`cat $dbLoginFile | cut -d'*' -f2`
    DB_URL=`cat $dbLoginFile | cut -d'*' -f3`
    DB_NAME=`cat $dbLoginFile | cut -d'*' -f4`

    result=`echo ${processed} | grep $DB_URL`
    if [ "$result" = "" ]
    then
        echo "-------------------------------------"
        echo "Processing login from $dbLoginFile..."
        if [ "$SETUP" = "true" ]
        then
            initSchema
        else
            cleanSchema
        fi
        processed="${processed}${DB_URL} "
    fi
done
echo "-------------------------------------"

if [ "$loginfound" = "false" ]
then
    echo "No db Login info available!"
fi
