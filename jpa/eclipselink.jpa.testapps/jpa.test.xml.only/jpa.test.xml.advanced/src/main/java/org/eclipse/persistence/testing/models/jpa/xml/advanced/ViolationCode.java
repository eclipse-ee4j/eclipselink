/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
