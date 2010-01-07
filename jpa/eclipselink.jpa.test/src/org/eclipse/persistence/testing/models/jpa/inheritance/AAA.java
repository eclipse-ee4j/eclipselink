/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inheritance;

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
    
    AAA(){}

    AAA(String id) {
        this.id = id;
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CMP3_AAA_GENERATOR")
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
}
