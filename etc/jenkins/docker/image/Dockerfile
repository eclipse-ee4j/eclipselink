# Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.

# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.

# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

# Eclipse Foundation build infrastructure Docker image for EclipseLiunk builds
FROM ${docker.source}

ADD ${java.pkg} ${ant.pkg} ${maven.pkg} ${install.java}/

ADD mongo-start.sh mongo-stop.sh mysql-start.sh mysql-stop.sh ${install.scripts}/

ADD install.sh ${mysql.pkg} /tmp/

RUN chmod u+x /tmp/install.sh \
    && /tmp/install.sh > /tmp/install.log 2>&1 \
    && rm -vf /tmp/install.sh /tmp/${mysql.pkg}
