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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.aggregate;


/**
 *  Used to tested Optimistic locking when the version is stored in an Aggregate
 *  Added for bug 3443738
 */
public class Version implements java.io.Serializable {
    private int versionNumber = -1;

    public Version() {
        super();
    }

    public Version(int version) {
        super();
        setVersion(version);
    }

    public int getVersion() {
        return versionNumber;
    }

    public void setVersion(int newVersion) {
        versionNumber = newVersion;
    }
}
