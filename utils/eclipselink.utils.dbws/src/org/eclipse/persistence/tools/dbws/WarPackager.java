/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 12 2010:
 *         refactor WAR packaging: generic and for WLS 10.3
 ******************************************************************************/

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