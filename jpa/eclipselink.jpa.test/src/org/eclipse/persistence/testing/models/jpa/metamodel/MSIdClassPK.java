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
 *     11/11/2009-2.0  mobrien - JPA 2.0 Metadata API test model
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import java.io.Serializable;

public class MSIdClassPK implements Serializable {
    private static final long serialVersionUID = -5653212238687275498L;
    
    public String type;
    protected String length;
    private String width;
    private Integer identity;

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public MSIdClassPK() {}

    @Override
    public boolean equals(Object anidClassPK) {
        if (anidClassPK instanceof MSIdClassPK) {
            return false;
        }        
        MSIdClassPK idClassPK = (MSIdClassPK) anidClassPK;        
        return (idClassPK.getLength().equals(this.getLength()) && 
                idClassPK.getWidth().equals(this.getWidth()) &&
                idClassPK.getType().equals(this.getType()) &&
                idClassPK.getIdentity().equals(this.getIdentity()));
    }

    @Override
    public int hashCode() {
        if (null != type && null != length && null != width) {
            return 9232 * type.hashCode() * length.hashCode() * width.hashCode();
        } else {
            return super.hashCode();
        }
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
