/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     01/22/2010-2.0.1 Karen Moore
//       - 294361: incorrect generated table for element collection attribute overrides
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

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

