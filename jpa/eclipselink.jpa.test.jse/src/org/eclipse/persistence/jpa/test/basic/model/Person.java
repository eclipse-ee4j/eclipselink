/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/04/2014 - Rick Curtis  
 *       - 450010 : Add java se test bucket
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.basic.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;

@Entity
public class Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @Version
    int version;
    
    @OneToOne(fetch=FetchType.LAZY)
    Dog dog;

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }

    public int getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }
    
    

}
