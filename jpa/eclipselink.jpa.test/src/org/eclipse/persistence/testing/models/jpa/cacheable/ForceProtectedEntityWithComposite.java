/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.cacheable;

import static javax.persistence.GenerationType.TABLE;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.config.QueryHints;

@Entity(name="JPA_CACHEABLE_FORCE_PROTECTED_WITH_COMPOSITE")
@Table(name="JPA_CACHEABLE_F_P_W_C")
public class ForceProtectedEntityWithComposite {
    protected int id;
    protected String name;
    protected ProtectedEmbeddable protectedEmbeddable;
    
    protected SharedEmbeddable sharedEmbeddable;
    
    public ForceProtectedEntityWithComposite() {
    }
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="CACHEABLE_TABLE_GENERATOR")
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    

    public String toString() {
        return "ForceProtectedEntityWithComposite: [" + name + "]";
    }

    /**
     * @return the protectedEmbeddable
     */
    @Embedded
    public ProtectedEmbeddable getProtectedEmbeddable() {
        return protectedEmbeddable;
    }

    /**
     * @param protectedEmbeddable the protectedEmbeddable to set
     */
    public void setProtectedEmbeddable(ProtectedEmbeddable protectedEmbeddable) {
        this.protectedEmbeddable = protectedEmbeddable;
    }

    /**
     * @return the sharedEmbeddable
     */
    @Embedded
    public SharedEmbeddable getSharedEmbeddable() {
        return sharedEmbeddable;
    }

    /**
     * @param sharedEmbeddable the sharedEmbeddable to set
     */
    public void setSharedEmbeddable(SharedEmbeddable sharedEmbeddable) {
        this.sharedEmbeddable = sharedEmbeddable;
    }
    
    

}
