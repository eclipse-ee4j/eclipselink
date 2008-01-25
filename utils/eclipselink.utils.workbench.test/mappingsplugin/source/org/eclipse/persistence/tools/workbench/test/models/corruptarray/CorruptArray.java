/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
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
