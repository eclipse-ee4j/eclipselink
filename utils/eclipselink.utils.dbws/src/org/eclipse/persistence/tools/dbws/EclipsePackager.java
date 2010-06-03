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
 *     Mike Norman - May 05 2010
 *       fix for https://bugs.eclipse.org/bugs/show_bug.cgi?id=307897
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;

import java.io.File;
import java.io.FileNotFoundException;

public class EclipsePackager extends JDevPackager {
    
    public EclipsePackager() {
        super(null, "eclipse", noArchive);
    }

    public static final String PUBLIC_HTML_DIR = "WebContent";
    
    protected void buildPublicHTMLDir() throws FileNotFoundException {
        publicHTMLDir = new File(stageDir, PUBLIC_HTML_DIR);
        if (!publicHTMLDir.exists()) {
            boolean worked = publicHTMLDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    PUBLIC_HTML_DIR + " under " + stageDir);
            }
        }
    }
}
