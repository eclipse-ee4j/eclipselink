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

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Use Case: IdClass identifiers declared across multiple mappedSuperclasses in an inheritance hierarchy.
 * Note: The following Entity inherits 4 of 4 of the Id fields declared across 2 mappedSuperclasses above as part of the IdClass MSIdClassPK.
 * The IdClass annotation can go on the first mappedSuperclass superclass or this entity but not on the root.
 * As long as resolution of all fields in the IdClass are available - the configuration is good. 
 */
@Entity(name="MS_MS_EntityLeafMetamodel")
@Table(name="CMP3_MM_MSMSENTITY_LEAF")
public class MS_MS_Entity_Leaf extends MS_MS_Entity_Center {
    
    private String declaredLeafStringField;

    public String getDeclaredLeafStringField() {
        return declaredLeafStringField;
    }

    public void setDeclaredLeafStringField(String declaredLeafStringField) {
        this.declaredLeafStringField = declaredLeafStringField;
    }
}
