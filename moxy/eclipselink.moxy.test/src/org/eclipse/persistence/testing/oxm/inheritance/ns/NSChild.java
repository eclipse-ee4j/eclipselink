/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.inheritance.ns;

public class NSChild extends NSParent {

    private String childProp;

    public String getChildProp() {
        return childProp;
    }

    public void setChildProp(String childProp) {
        this.childProp = childProp;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        NSChild test = (NSChild) obj;
        if(null == childProp) {
            return null == test.getChildProp();
        }
        return childProp.equals(test.getChildProp());
    }

}