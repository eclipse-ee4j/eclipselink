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
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import org.eclipse.persistence.indirection.*;

public class Award  {    
    public int id;
    public String description;
    public ValueHolderInterface qualification;

    public Award() {
        this.qualification = new ValueHolder();
    }

    public int getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Qualification getQualification() {
        return (Qualification) qualification.getValue();
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setQualification(Qualification qualification) {
        this.qualification.setValue(qualification);
    }
}
