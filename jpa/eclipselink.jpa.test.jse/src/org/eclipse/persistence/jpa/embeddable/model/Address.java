/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/24/2016 - Will Dazey
 *       - 500145 : Nested Embeddables Test
 ******************************************************************************/
package org.eclipse.persistence.jpa.embeddable.model;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Address {
    
    @Embedded 
    protected Zipcode zipcode;
    
    public Address() { }
    
    public Address(Zipcode zipcode) {
        this.setZipcode(zipcode);
    }
    
    public Zipcode getZipcode() {
        return zipcode;
    }
    public void setZipcode(Zipcode zipcode) {
        this.zipcode = zipcode;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((zipcode == null) ? 0 : zipcode.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Address other = (Address) obj;
        if (zipcode == null) {
            if (other.zipcode != null)
                return false;
        } else if (!zipcode.equals(other.zipcode))
            return false;
        return true;
    }
}
