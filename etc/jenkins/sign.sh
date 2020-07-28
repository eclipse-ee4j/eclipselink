#!/bin/bash
#
# Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

PRESIGNED_ZIP=$1
SIGNED_OUTPUT=$3

rm -rf presign_jars signed_jars $SIGNED_OUTPUT

#extracting presigned zip
unzip -q $PRESIGNED_ZIP -d presign_jars
find presign_jars -type d |sed 's/presign_jars/signed_jars/g' |xargs mkdir -p

#signing the jars
for j in `find presign_jars -type f -name "*.jar"`
do
    echo "signing $j ..."
    signed_jar=`echo $j |sed 's/presign_jars/signed_jars/g'`
    curl -s -o ${signed_jar} -F file=@${j} http://build.eclipse.org:31338/sign
done

mkdir -p $SIGNED_OUTPUT
cd signed_jars
zip -q -r ../${SIGNED_OUTPUT}/${PRESIGNED_ZIP} *
