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
 *     09/23/2009-2.0  mobrien 
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;

/**
 * Use Case: IdClass identifiers declared across multiple mappedSuperclasses in an inheritance hierarchy.
 * Note: The following MappedSuperclass defines 1 of 4 of the Id fields as part of the IdClass MSIdClassPK.
 * The other 3 fields are declared on the superclass
 * The IdClass annotation can go on this class or the entity but not on the root mappedSuperclass.
 * As long as resolution of all fields in the IdClass are available - the configuration is good. 
 */
@MappedSuperclass
@IdClass(org.eclipse.persistence.testing.models.jpa.metamodel.MSIdClassPK.class)
public abstract class MS_MS_Entity_Center extends MS_MS_Entity_Root {
    
    @Id // see 288972
    private Integer identity;

    private String declaredCenterStringField;
    
    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public String getDeclaredCenterStringField() {
        return declaredCenterStringField;
    }

    public void setDeclaredCenterStringField(String declaredCenterStringField) {
        this.declaredCenterStringField = declaredCenterStringField;
    }
    
    public MS_MS_Entity_Center() {}
    
    public MSIdClassPK buildPK(){
        MSIdClassPK pk = new MSIdClassPK();
        pk.setLength(this.getLength());
        pk.setWidth(this.getWidth());
        pk.setType(this.getType());
        pk.setIdentity(this.getIdentity());
        return pk;
    }

    @Override
    public boolean equals(Object anMSMSEntity) {
        if (anMSMSEntity instanceof MS_MS_Entity_Center) {
            return false;
        }        
        return ((MS_MS_Entity_Center) anMSMSEntity).buildPK().equals(buildPK());
    }
    
    @Override
    public int hashCode() {
        if (null != type && null != length && null != width) {
            return 9232 * type.hashCode() * length.hashCode() * width.hashCode() * identity.hashCode();
        } else {
            return super.hashCode();
        }
    }
    
}
