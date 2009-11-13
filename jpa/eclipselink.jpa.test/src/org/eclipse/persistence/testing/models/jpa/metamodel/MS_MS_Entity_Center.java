/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/23/2009-2.0  mobrien 
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class MS_MS_Entity_Center extends MS_MS_Entity_Root {
    
    //@Id // see 288972
    // InstanceVariableAttributeAccessor testing
    @Column(name="MSMSENTITY_ID")    
    private Integer ident;

    private String declaredCenterStringField;
    
    public Integer getIdent() {
        return ident;
    }

    public void setIdent(Integer ident) {
        this.ident = ident;
    }

    public String getDeclaredCenterStringField() {
        return declaredCenterStringField;
    }

    public void setDeclaredCenterStringField(String declaredCenterStringField) {
        this.declaredCenterStringField = declaredCenterStringField;
    }
    
    
}
