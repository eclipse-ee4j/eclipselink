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
//     03/08/2010-2.1 Guy Pelletier
//       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to test the extended orm attribute-type settings.
 *
 * It is mapped as an entity in the following resource file:
 *
 *  resource/eclipselinkorm/eclipselink-xml-extended-model/eclipselink-orm.xml
 *
 * @author gpelleti
 */
public class Loner {
    // Basic mapping
    private Object id;

    // Version mapping
    private Object version;

    // One to many mapping
    private Object confidants;

    // Element collection mapping
    private Object characteristics;

    // Embedded
    private Object name;

    public Loner() {
        confidants = new ArrayList<Confidant>();
        characteristics  = new ArrayList<String>();
    }

    public void addCharacteristic(Object characteristic) {
        ((List) characteristics).add(characteristic);
    }

    public void addConfidant(Confidant confidant) {
        confidant.setLoner(this);
        ((List) confidants).add(confidant);
    }

    public Object getCharacteristics() {
        return characteristics;
    }

    public Object getConfidants() {
        return confidants;
    }

    public Object getId() {
        return id;
    }

    public Object getName() {
        return name;
    }

    public Object getVersion() {
        return version;
    }

    public void setCharacteristics(Object characteristics) {
        this.characteristics = characteristics;
    }

    public void setConfidants(Object confidants) {
        this.confidants = confidants;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public void setVersion(Object version) {
        this.version = version;
    }
}
