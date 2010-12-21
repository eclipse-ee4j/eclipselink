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
 *     01/22/2010-2.0.1 Karen Moore 
 *       - 294361: incorrect generated table for element collection attribute overrides
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.eclipse.persistence.annotations.CascadeOnDelete;

@Entity
public class PropertyRecord {
    @Id 
    @GeneratedValue
    public Integer id;

    @AttributeOverrides({
        @AttributeOverride(name="key.street",
                column=@Column(name="STREET_NAME")),
        @AttributeOverride(name="key.zipcode.zip",
            column=@Column(name="ZIPNUM")),
        @AttributeOverride(name="value.parcelNumber",
                    column=@Column(name="PARCEL_NUMBER", nullable=false)),
        @AttributeOverride(name="value.size",
                column=@Column(name="SQUARE_FEET")),
        @AttributeOverride(name="value.tax",
                column=@Column(name="ASSESSMENT"))
        })
    @ElementCollection
    @CascadeOnDelete
    public Map<Address, PropertyInfo> propertyInfos = new HashMap<Address, PropertyInfo>();
}

