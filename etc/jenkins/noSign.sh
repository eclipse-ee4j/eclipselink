#!/bin/bash
#****************************************************************************************
# Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
# which accompanies this distribution.
#
# The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
# and the Eclipse Distribution License is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#
# Contributors:
#  - Radek Felcman - 26 August 2019 - Initial implementation
#****************************************************************************************

#----------------------------------------------------------------------------------------
#    This script is there to handle/copy jar files to similar directory like sign.sh script.
#    It's called by promote.sh script if jar signing is not enabled by parameter (default state).
#----------------------------------------------------------------------------------------


PRESIGNED_ZIP=$1
SIGNED_OUTPUT=$3

rm -rf presign_jars signed_jars $SIGNED_OUTPUT

#extracting presigned zip
unzip -q $PRESIGNED_ZIP -d presign_jars
find presign_jars -type d |sed 's/presign_jars/signed_jars/g' |xargs mkdir -p

#not signing the jars just copy them to the output directory
for j in `find presign_jars -type f -name "*.jar"`
do
    echo "copying $j ..."
    signed_jar=`echo $j |sed 's/presign_jars/signed_jars/g'`
    cp ${j} ${signed_jar}
done

mkdir -p $SIGNED_OUTPUT
cd signed_jars
zip -q -r ../${SIGNED_OUTPUT}/${PRESIGNED_ZIP} *
