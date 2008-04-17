# !/bin/sh

umask 0002

HOME_DIR=/shared/technology/eclipselink
DATED_CB_LOG=$HOME_DIR/logs/cb/cb_`date '+%y%m%d'`.log
$HOME_DIR/cb_builder.sh >> $DATED_CB_LOG

