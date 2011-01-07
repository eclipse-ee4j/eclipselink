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
 *     Jun 17, 2009-2.0 Chris Delahunt 
 *       - Bug#280350: NoSuchFieldException on deploy when using parent's compound PK class as derived ID
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;

@Entity
@Table(name="JPA_LACKEY")
@IdClass(MajorId.class)
public class Lackey {
    @Column(name="NAME")
    String name;
    
    @OneToOne 
    @Id
    @JoinColumns({
        @JoinColumn(name="FIRSTNAME", referencedColumnName="F_NAME"),
        @JoinColumn(name="LASTNAME", referencedColumnName="L_NAME")
    })
    Major major;
    
    
    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

}
