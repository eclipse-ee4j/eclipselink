/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.namespaces;

public class Manager extends Employee {
    String title;

    public Manager() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean equals(Object o) {
        try {
            boolean equals = super.equals(o);
            if (!equals) {
                return false;
            }

            Manager manager = (Manager)o;            
            if (!this.getTitle().equals(manager.getTitle())) {
                return false;
            }
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }

    public String toString() {
        String string = "Manager(id=";
        string += this.getId();
        string += ", name='";
        string += this.getName();
        string += ", title='";
        string += this.getTitle();
        string += "')";
        return string;
    }
}
