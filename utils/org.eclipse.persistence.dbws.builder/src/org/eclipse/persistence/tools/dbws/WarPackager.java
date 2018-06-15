/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Mike Norman - May 12 2010:
//         refactor WAR packaging: generic and for WLS 10.3

package org.eclipse.persistence.tools.dbws;

//EclipseLink imports
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.archive;

public class WarPackager extends JSR109WebServicePackager {

    public WarPackager() {
        this(new WarArchiver(), "war", archive);
    }
    protected WarPackager(Archiver archiver, String packagerLabel, ArchiveUse useJavaArchive) {
        super(archiver, packagerLabel, useJavaArchive);
    }

    @Override
    public String getAdditionalUsage() {
        return " [warFilename] [options]";
    }
}
