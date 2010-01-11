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
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement;

import java.util.ArrayList;

public class Wrapper {    
    private Root theRoot;

    public Wrapper() {
    }

    public boolean equals(Object object) {
        if (object instanceof Wrapper) {
            return theRoot.equals(((Wrapper)object).getTheRoot());
        }
        return false;
    }

    public void setTheRoot(Root theRoot) {
        this.theRoot = theRoot;
    }

    public Root getTheRoot() {
        return theRoot;
    }
}
