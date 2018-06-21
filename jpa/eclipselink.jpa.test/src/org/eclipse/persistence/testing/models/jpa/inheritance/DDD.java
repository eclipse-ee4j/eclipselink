/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     01/26/2011-2.3 Guy Pelletier
//       - 307664:  Lifecycle callbacks not called for object from IndirectSet
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

    public boolean equals(Object object){

        if (object == null){
            return false;
        }
        if (!(object instanceof DDD)){
            return false;
        }
        DDD ddd = (DDD)object;
        if (id == ddd.getId()){
            if (aaa == ddd.getAaa()){
                return true;
            }
            if (aaa != null){
                if (ddd.getAaa() != null){
                    return aaa.getId().equals(ddd.getAaa().getId());
                }
            }
        }
        return false;
    }

    public int hashCode(){
        return id;
    }
}
