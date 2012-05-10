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
 *     09/22/2009-2.0  mobrien - JPA 2.0 Metadata API test model
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

public class EnclosureIdClassPK {
    public String type;
    protected String length;
    private String width;
    private Integer mappedManufacturerUC9;

    public EnclosureIdClassPK() {}

    @Override
    public boolean equals(Object anEnclosureIdClassPK) {
        if (anEnclosureIdClassPK.getClass() != EnclosureIdClassPK.class) {
            return false;
        }        
        EnclosureIdClassPK enclosureIdClassPK = (EnclosureIdClassPK) anEnclosureIdClassPK;        
        return (enclosureIdClassPK.getLength().equals(this.getLength()) && 
                enclosureIdClassPK.getWidth().equals(this.getWidth()) &&
                enclosureIdClassPK.getType().equals(this.getType()) &&
                enclosureIdClassPK.getMappedManufacturerUC9().equals(this.mappedManufacturerUC9));
    }

    @Override
    public int hashCode() {
        if (null != type && null != length && null != width && null != mappedManufacturerUC9) {
            return 9232 * type.hashCode() * length.hashCode() * width.hashCode() * mappedManufacturerUC9.hashCode();
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

    
    public Integer getMappedManufacturerUC9() {
        return mappedManufacturerUC9;
    }

    public void setMappedManufacturerUC9(Integer mappedManufacturerUC9) {
        this.mappedManufacturerUC9 = mappedManufacturerUC9;
    }
    
}
