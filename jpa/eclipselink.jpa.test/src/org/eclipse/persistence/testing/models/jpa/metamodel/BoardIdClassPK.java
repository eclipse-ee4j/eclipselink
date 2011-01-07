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
 *     09/22/2009-2.0  mobrien - JPA 2.0 Metadata API test model
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 ******************************************************************************/       
 package org.eclipse.persistence.testing.models.jpa.metamodel;

public class BoardIdClassPK {
    public String type;
    protected String length;
    private String width;

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

    public BoardIdClassPK() {}

    @Override
    public boolean equals(Object aBoardIdClassPK) {
        if (aBoardIdClassPK.getClass() != BoardIdClassPK.class) {
            return false;
        }        
        BoardIdClassPK boardIdClassPK = (BoardIdClassPK) aBoardIdClassPK;        
        return (boardIdClassPK.getLength().equals(this.getLength()) && 
                boardIdClassPK.getWidth().equals(this.getWidth()) &&
                boardIdClassPK.getType().equals(this.getType()));
    }

    @Override
    public int hashCode() {
        if (null != type && null != length && null != width) {
            return 9232 * type.hashCode() * length.hashCode() * width.hashCode();
        } else {
            return super.hashCode();
        }
    }
}
