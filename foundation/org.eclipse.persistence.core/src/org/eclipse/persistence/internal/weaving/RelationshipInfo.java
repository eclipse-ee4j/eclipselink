/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.internal.weaving;

/**
 * Stores information about a relationships mapping that is used by JPA-RS to build links for relationships.
 * 
 * @author tware
 */
public class RelationshipInfo {

    private Object primaryKey;
    private String owningEntityAlias;
    private String attributeName;
    private Object owningEntity;
    
    public void setPersistencePrimaryKey(Object primaryKey) {
        this.primaryKey = primaryKey;
    }
    
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;          
    }

    public Object getPersistencePrimaryKey() {
        return primaryKey;
    }
    
    public String getAttributeName() {
        return attributeName;
    }

    public Object getOwningEntity() {
        return owningEntity;
    }

    public void setOwningEntity(Object owningEntity) {
        this.owningEntity = owningEntity;
    }

    public String getOwningEntityAlias() {
        return owningEntityAlias;
    }

    public void setOwningEntityAlias(String owningEntityAlias) {
        this.owningEntityAlias = owningEntityAlias;
    }
}

