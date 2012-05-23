/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/18/2010-2.2 Guy Pelletier 
 *       - 326973: TABLE_PER_CLASS with EmbeddedId results in DescriptorException EclipseLink-74 
 *                 "The primary key fields are not set for this descriptor"
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inheritance;

import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Table;

/**
 * Model class added to test metadata processing error for bug 326973.
 * Model not added to the InheritanceTableCreator.
 */
@Entity
@Inheritance(strategy=TABLE_PER_CLASS)
@Table(name="TPC_HOTEL")
public class Hotel {
    @EmbeddedId 
    HotelId hotelId;

    public HotelId getHotelId() {
        return hotelId;
    }

    public void setHotelId(HotelId hotelId) {
        this.hotelId = hotelId;
    }
}
