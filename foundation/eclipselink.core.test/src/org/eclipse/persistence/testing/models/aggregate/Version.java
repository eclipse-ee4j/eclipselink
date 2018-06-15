/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
