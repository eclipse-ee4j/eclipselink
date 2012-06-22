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
 *     tware - fix for bug 352533
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.cacheable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CacheableTrueDerivedIDPK {

    protected String description;
    
    @Column(name = "CF_ID", insertable = false, updatable = false)
    protected int cacheableFalseID;

    public CacheableTrueDerivedIDPK(){}
    
    public CacheableTrueDerivedIDPK(String description, int cacheableFalseID){
        this.description = description;
        this.cacheableFalseID = cacheableFalseID;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCacheableFalseID() {
        return cacheableFalseID;
    }

    public void setCacheableFalseID(int cacheableFalseID) {
        this.cacheableFalseID = cacheableFalseID;
    }
}
