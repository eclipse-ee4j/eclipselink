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
 *     05/1/2009-2.0 Guy Pelletier/David Minsky
 *       - 249033: JPA 2.0 Orphan removal
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.orphanremoval;

import javax.persistence.Embeddable;

@Embeddable
public class Tire {
    protected String type;
    protected String manufacturer;
    
    public Tire() {
        super();
    }

    public String getManufacturer() {
        return manufacturer;
    }
    
    public String getType() {
        return type;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}
