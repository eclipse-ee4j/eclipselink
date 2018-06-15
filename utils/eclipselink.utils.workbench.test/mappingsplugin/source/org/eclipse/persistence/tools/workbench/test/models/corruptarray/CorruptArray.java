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
package org.eclipse.persistence.tools.workbench.test.models.corruptarray;

public class CorruptArray {

    private Byte[] blobField;
    private Character[] clobField;

    public CorruptArray() {
        super();
    }

    public Byte[] getBlobField() {
        return this.blobField;
    }

    public void setBlobField(Byte[] blobField) {
        this.blobField = blobField;
    }

    public Character[] getClobField() {
        return this.clobField;
    }

    public void setClobField(Character[] clobField) {
        this.clobField = clobField;
    }
}
