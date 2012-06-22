/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Gordon Yorke - Initial Contribution
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.cacheable;

import static javax.persistence.GenerationType.TABLE;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.QueryHint;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.Noncacheable;
import org.eclipse.persistence.config.QueryHints;

public class ProtectedRelationshipsEntity {

    protected int id;
    protected String name;
    
    protected CacheableTrueEntity cacheableFalse;
    
    protected CacheableTrueEntity cacheableProtected;

    protected List<CacheableTrueEntity> cacheableProtecteds;

    protected List<CacheableTrueEntity> cacheableProtecteds2;
    
    protected List<String> elementCollection;
    
    protected List<String> basicCollection;
    
    public ProtectedRelationshipsEntity() {
        cacheableProtecteds = new ArrayList<CacheableTrueEntity>();
        cacheableProtecteds2 = new ArrayList<CacheableTrueEntity>();
        elementCollection = new ArrayList<String>();
        basicCollection = new ArrayList<String>();
    }
    
}
