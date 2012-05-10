/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CaptainId {
    @Column(name="IGNORED")
    String name;
    MajorId major;
    
    public String getName() {
        return name;
    }
    
    public MajorId getMajor() {
        return major;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setMajor(MajorId major) {
        this.major = major;
    }
}
