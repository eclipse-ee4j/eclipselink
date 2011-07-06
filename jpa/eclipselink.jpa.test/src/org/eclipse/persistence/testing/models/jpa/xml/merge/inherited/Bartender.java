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
 *     07/06/2011-2.3.1 Guy Pelletier 
 *       - 349906: NPE while using eclipselink in the application
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import javax.persistence.Basic;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * This class is mapped in:
 * resource/eclipselink-ddl-generation-model/merge-inherited-consumer.xml
 */
@Entity
public class Bartender {
    @Id
    @GeneratedValue
    public int id;
    
    @Basic
    public String firstName;
    
    @Basic
    public String lastName;
    
    @Embedded
    public ProbationaryPeriod probationaryPeriod;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
    public ProbationaryPeriod getProbationaryPeriod() {
        return probationaryPeriod;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setProbationaryPeriod(ProbationaryPeriod probationaryPeriod) {
        this.probationaryPeriod = probationaryPeriod;
    }
    
}
