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

/**
 * This class is used to test the extended orm attribute-type settings.
 *
 * It is mapped as an entity in the following resource file:
 *
 *  resource/eclipselinkorm/eclipselink-xml-extended-model/eclipselink-orm.xml
 *
 * @author gpelleti
 */
public class Confidant {
    // Id mapping
    private Object id;

    // One to one
    private Object loner;

    public Object getId() {
        return id;
    }

    public Object getLoner() {
        return loner;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public void setLoner(Object loner) {
        this.loner = loner;
    }
}
