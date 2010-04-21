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
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name="CMP3_HPROJECT")
@DiscriminatorValue("H")
public class HugeProject extends LargeProject {
    private Employee evangelist;
    
    public HugeProject() {
        super();
    }
    
    public HugeProject(String name) {
        super(name);
    }
    
    @OneToOne(fetch=LAZY)
    @JoinColumn(name="EVANGELIST_ID")
    public Employee getEvangelist() { 
        return this.evangelist; 
    }
    
    public void setEvangelist(Employee employee) {
        this.evangelist = employee;
    }
}
