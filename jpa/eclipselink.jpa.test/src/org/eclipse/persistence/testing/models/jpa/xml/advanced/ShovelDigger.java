/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     03/29/2010-2.1 Guy Pelletier
//       - 267217: Add Named Access Type to EclipseLink-ORM
//     04/09/2010-2.1 Guy Pelletier
//       - 307050: Add defaults for access methods of a VIRTUAL access type
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import static org.eclipse.persistence.annotations.ChangeTrackingType.DEFERRED;

import org.eclipse.persistence.annotations.ChangeTracking;

/**
 * This class is used to test the extended orm named-access setting.
 *
 * It is mapped as an entity in the following resource file:
 *
 *  resource/eclipselinkorm/eclipselink-xml-extended-model/eclipselink-orm.xml
 *
 * @author gpelleti
 */
@ChangeTracking(DEFERRED)
public class ShovelDigger extends ShovelPerson {
    // one-to-one
    private Shovel shovel;

    public ShovelDigger() {}

    public Object getX(String attribute) {
        try {
            return getClass().getDeclaredField(attribute).get(this);
        } catch (Exception e) {
            throw new RuntimeException("Error occured getting the value of attribute: " + attribute);
        }
    }

    public void setX(String attribute, Object value) {
        try {
            getClass().getDeclaredField(attribute).set(this, value);
        } catch (Exception e) {
            throw new RuntimeException("Error occured set the attribute: " + attribute + ", with value: " + value);
        }
    }
}
