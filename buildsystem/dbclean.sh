# !/bin/sh

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
for dbLoginFile in `ls | grep db-` ; do
    loginfound=true
    DB_USER=`cat $dbLoginFile | cut -d'*' -f1`
    DB_PWD=`cat $dbLoginFile | cut -d'*' -f2`
    DB_URL=`cat $dbLoginFile | cut -d'*' -f3`
    DB_NAME=`cat $dbLoginFile | cut -d'*' -f4`

    echo "-------------------------------------"
    echo "Processing login from $dbLoginFile..."
    cleanSchema
done
echo "-------------------------------------"

if [ "$loginfound" = "false" ]
then
    echo "No db Login info available!"
fi
