/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - July 17 2008, creating packager for WLS 10.3 version of JAX-WS RI
 ******************************************************************************/

 package org.eclipse.persistence.tools.dbws;

//javase imports

//EclipseLink imports
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.archive;

/**
 * <p>
 * <b>PUBLIC:</b> WeblogicPackager extends {@link WebServicePackager}. It is responsible for generating <br>
 * the WebLogic-specific deployment information - specifically, the settings in the sessions.xml file <br>
 * that require WebLogic-specific platform information
 * 
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class WeblogicPackager extends WarPackager {

    public WeblogicPackager() {
        this(new WarArchiver(),"wls", archive);
    }
    protected WeblogicPackager(Archiver archiver, String packagerLabel, ArchiveUse useJavaArchive) {
        super(archiver, packagerLabel, useJavaArchive);
    }

}