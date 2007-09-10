/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Embedded;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_MUD_TIRE")
@DiscriminatorValue("Mud")
public class MudTireInfo extends OffRoadTireInfo {
    protected TireRating tireRating;
    protected int treadDepth;

    public MudTireInfo() {}

	@Column(name="TREAD_DEPTH")
    public int getTreadDepth() {
        return treadDepth;
    }

    public void setTreadDepth(int treadDepth) {
        this.treadDepth = treadDepth;
    }
    
    @Embedded
    public TireRating getTireRating() {
        return tireRating;
    }
    
    public void setTireRating(TireRating rating) {
        this.tireRating = rating;
    }
}
