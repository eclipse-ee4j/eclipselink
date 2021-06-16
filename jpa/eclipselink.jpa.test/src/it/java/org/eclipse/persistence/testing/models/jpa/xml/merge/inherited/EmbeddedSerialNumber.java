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
//     Oracle - initial API and implementation from Oracle TopLink
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import jakarta.persistence.Access;
import static jakarta.persistence.AccessType.FIELD;
import static jakarta.persistence.AccessType.PROPERTY;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * The owning class has field access, and so to process this class as
 * property access we must specify the Access(PROPERTY)
 */
@Embeddable
@Access(PROPERTY)
public class EmbeddedSerialNumber {
    @Access(FIELD)
    @Column(name="ES_NUMBER")
    public Integer number;

    private String breweryCode;

    public EmbeddedSerialNumber() {}

    public String getBreweryCode() {
        return this.breweryCode;
    }

    public void setBreweryCode(String breweryCode) {
        this.breweryCode = breweryCode;
    }
}
