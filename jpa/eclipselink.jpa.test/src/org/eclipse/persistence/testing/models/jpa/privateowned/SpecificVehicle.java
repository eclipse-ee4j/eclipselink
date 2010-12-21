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
 *     02/19/09 dminsky - initial API and implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.privateowned;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="PO_SpecificVehicle")
@DiscriminatorValue("SV")
public class SpecificVehicle extends Vehicle{
    
    public SpecificVehicle() {
        super();
    }
    
    public SpecificVehicle(String model) {
        super(model);
    }
}
