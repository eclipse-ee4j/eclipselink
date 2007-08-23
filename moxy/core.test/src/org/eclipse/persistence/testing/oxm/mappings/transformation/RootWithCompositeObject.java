/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.transformation;

public class RootWithCompositeObject {
    public Employee employee;

    public boolean equals(Object object) {
        try {
            RootWithCompositeObject rootWithCompositeObject = (RootWithCompositeObject)object;
            if (this.employee == rootWithCompositeObject.employee) {
                return true;
            }
            if ((null == this.employee) || (null == rootWithCompositeObject.employee)) {
                return false;
            }
            return this.employee.equals(rootWithCompositeObject.employee);
        } catch (ClassCastException e) {
            return false;
        }
    }
    
    public String toString() {
        String string = "RootWithCompositeObject(";
        string += employee.toString();
        string += ")";
        return string;
    }        
}