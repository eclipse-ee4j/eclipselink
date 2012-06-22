/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     07/16/2009-2.0  mobrien - JPA 2.0 Metadata API test model
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CompositePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "pk_field1", nullable = false)
    private int pk_field1;

    @Basic(optional = false)
    @Column(name = "pk_field2", nullable = false)
    private int pk_field2;

    public CompositePK() {
    }
    
    @Override
    public boolean equals(Object aCompositePK) {
        if (aCompositePK.getClass() != CompositePK.class) {
            return false;
        }        
        CompositePK compositePK = (CompositePK) aCompositePK;        
        return (compositePK.pk_field1 == this.pk_field1) && 
                (compositePK.pk_field2 == this.pk_field2);
    }

    @Override
    public int hashCode() {
        return 9232 * pk_field1 * pk_field2;
    }
}

