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
//     06/16/2010-2.2 Guy Pelletier
//       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

public class ViolationCode {
    public enum ViolationCodeId {A, B, C, D}

    public ViolationCodeId id;

    public String description;

    public String getDescription() {
        return description;
    }

    public ViolationCodeId getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(ViolationCodeId id) {
        this.id = id;
    }
}
