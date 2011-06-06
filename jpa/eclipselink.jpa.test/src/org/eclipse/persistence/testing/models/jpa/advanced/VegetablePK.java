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
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class VegetablePK implements Serializable {
    private String name;
    private String color;
    
    public VegetablePK() {}

    public VegetablePK(String name, String color) {
        setName(name);
        setColor(color);
    }

    public boolean equals(Object otherVegetablePK) {
        if (otherVegetablePK instanceof VegetablePK) {
            if (! getName().equals(((VegetablePK) otherVegetablePK).getName())) {
                return false;
            }
            
            return ( getColor().equals(((VegetablePK) otherVegetablePK).getColor()));
        }
        
        return false;
    }
    
    public String getColor() {
        return color;
    }
    
    public String getName() {
        return name;
    }
    
    public int hashCode() {
        int hash = 0;
        hash += (this.getName() != null ? this.getName().hashCode() : 0);
        hash += (this.getColor() != null ? this.getColor().hashCode() : 0);
        return hash;
    }

    public void setColor(String color) {
        this.color = color;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "VegetablePK [id=" + getName() + " - " + getColor() + "]";
    }
}
