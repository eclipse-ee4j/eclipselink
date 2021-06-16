/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     03/29/2010-2.1 Guy Pelletier
//       - 267217: Add Named Access Type to EclipseLink-ORM
//     04/09/2010-2.1 Guy Pelletier
//       - 307050: Add defaults for access methods of a VIRTUAL access type
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import java.util.List;

import org.eclipse.persistence.annotations.ChangeTracking;

import static org.eclipse.persistence.annotations.ChangeTrackingType.DEFERRED;

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
public class Shovel {
    // id
    private Integer id;

    // embedded
    private ShovelSections sections;

    // basic
    private Double cost;

    // version
    private Integer version;

    // Many to one
    private ShovelOwner owner;

    // one to many
    private List<ShovelDigger> operators;

    // many to many
    private List<ShovelProject> projects;

    public Shovel() {}

    public Object getMy(String attribute) {
        try {
            return getClass().getDeclaredField(attribute).get(this);
        } catch (Exception e) {
            throw new RuntimeException("Error occured getting the value of attributee: " + attribute);
        }
    }

    public void setMy(String attribute, Object value) {
        try {
            getClass().getDeclaredField(attribute).set(this, value);
        } catch (Exception e) {
            throw new RuntimeException("Error occured set the attribute: " + attribute + ", with value: " + value);
        }
    }
}
