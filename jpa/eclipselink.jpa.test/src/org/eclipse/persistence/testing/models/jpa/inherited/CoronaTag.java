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
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CoronaTag {
    private Integer number;
    private String code;
    
    public CoronaTag() {}
    
    public boolean equals(Object anotherCoronaTag) {
        if (anotherCoronaTag.getClass() != CoronaTag.class) {
            return false;
        }
        
        return getCode().equals(((CoronaTag)anotherCoronaTag).getCode()) && 
               getNumber().equals(((CoronaTag)anotherCoronaTag).getNumber());
    }
    
    @Column(name="TAG_CODE")
    public String getCode() {
        return code;
    }
    
    // We will specify an attribute override for this one, otherwise this is an invalid column name
    @Column(name="NUMBER_INVALID")
    public Integer getNumber() {
        return number;
    }
    
    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

