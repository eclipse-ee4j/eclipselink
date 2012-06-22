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
 *     ailitchev - 2010.01.08 Bug 299147 - em.find isolated read-only entity throws exception 
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_1;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.ReadOnly;
import org.eclipse.persistence.config.CacheIsolationType;

@Entity
@Table(name="MBR1_READONLY_ISOLATED")
@ReadOnly
@Cache(isolation=CacheIsolationType.ISOLATED)
public class ReadOnlyIsolated  {
    private Integer id;
    private String code;

    public ReadOnlyIsolated() {}

    public String getCode() { 
        return code; 
    }
    
    @Id
    public Integer getId() { 
        return id; 
    }
    
    public void setCode(String code) { 
        this.code = code; 
    }
    
    public void setId(Integer id) { 
        this.id = id; 
    }
}
