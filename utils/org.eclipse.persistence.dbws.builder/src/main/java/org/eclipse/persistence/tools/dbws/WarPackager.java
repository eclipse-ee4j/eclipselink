/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
