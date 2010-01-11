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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_ROCK_TIRE")
@DiscriminatorValue("Rock")
public class RockTireInfo extends OffRoadTireInfo {
    public enum Grip { REGULAR, SUPER, MEGA }
    
    protected Grip grip;

    public RockTireInfo() {}

    public Grip getGrip() {
        return grip;
    }

    public void setGrip(Grip grip) {
        this.grip = grip;
    }
}
