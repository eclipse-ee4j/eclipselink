/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     01/26/2011-2.3 Guy Pelletier 
 *       - 307664:  Lifecycle callbacks not called for object from IndirectSet
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.TypeConverter;

@Entity
@Table(name="CMP3_AAA")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="DTYPES", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("1")
public  class AAA {
    String id;
    String foo;
    
    Set<DDD> ddds;
    
    public AAA() {
        ddds = new HashSet<DDD>();
    }

    AAA(String id) {
        this();
        this.id = id;
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CMP3_AAA_GENERATOR")
    @TableGenerator(name = "CMP3_AAA_GENERATOR", table = "CMP3_AAA_SEQ")
    @Convert("stringToInt")
    @TypeConverter(name="stringToInt", dataType=Integer.class, objectType=String.class)
    public String getId() { 
        return id; 
    }
    
    public void setId(String id) { 
        this.id = id; 
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    @OneToMany(mappedBy="aaa", cascade=CascadeType.ALL)
    public Set<DDD> getDdds() {
        return ddds;
    }

    public void setDdds(Set<DDD> ddds) {
        this.ddds = ddds;
    }
}
