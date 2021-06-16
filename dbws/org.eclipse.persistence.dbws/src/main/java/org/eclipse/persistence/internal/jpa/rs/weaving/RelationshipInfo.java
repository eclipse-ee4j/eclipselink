/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//      tware - initial
package org.eclipse.persistence.internal.jpa.rs.weaving;

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

