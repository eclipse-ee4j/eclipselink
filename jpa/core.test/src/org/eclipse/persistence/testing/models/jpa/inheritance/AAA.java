/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.*;

@Entity
@Table(name="CMP3_AAA")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="DTYPES", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("1")
public  class AAA {
    String id;
    String foo;
    
    AAA(){}

    AAA(String id) {
        this.id = id;
    }
    
    @Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CMP3_AAA_GENERATOR")
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
}
