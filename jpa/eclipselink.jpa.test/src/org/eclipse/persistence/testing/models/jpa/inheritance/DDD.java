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
 *     01/26/2011-2.3 Guy Pelletier 
 *       - 307664:  Lifecycle callbacks not called for object from IndirectSet
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.testing.models.jpa.inheritance.listeners.DDDListener;;

@Entity
@EntityListeners(DDDListener.class)
@Table(name="CMP3_DDD")
public class DDD {
    int id;
    int count;
    int count2;
    AAA aaa;
    
    public DDD() {
    }

    DDD(int id) {
        this.id = id;
    }
    
    @PostLoad
    public void postLoad() {
        count++;
    }
    
    @Id
    @GeneratedValue
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    @ManyToOne
    @JoinColumn(name="AAA_ID")
    public AAA getAaa() {
        return aaa;
    }

    public void setAaa(AAA aaa) {
        this.aaa = aaa;
    }

    @Column(name="POST_LOAD_COUNT")
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Column(name="EL_POST_LOAD_COUNT")
    public int getCount2() {
        return count2;
    }

    public void setCount2(int count2) {
        this.count2 = count2;
    }
}
