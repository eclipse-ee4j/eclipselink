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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.aggregate;


/**
 *  The following is the object structure of this model:
 *  GolfClub - AggregateObject -> GolfClubShaft
 *  GolfClub - One-To-One -> Manufacturer
 *  GolfClubShaft - One-To-One -> Manufacturer
 *  This structure is special because both GolfClub and GolfClubShaft have an attribute
 *  called manufacturer.  This is useful for joining tests.
 */
public class GolfClubShaft {
    protected String stiffnessRating;
    protected Manufacturer manufacturer;

    public GolfClubShaft() {
        super();
    }

    public Manufacturer getManufacturer() {
        return this.manufacturer;
    }

    public String getStiffnessRating() {
        return this.stiffnessRating;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setStiffnessRating(String stiffnessRating) {
        this.stiffnessRating = stiffnessRating;
    }
}
