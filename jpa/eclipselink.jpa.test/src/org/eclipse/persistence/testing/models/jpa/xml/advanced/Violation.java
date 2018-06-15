/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     06/16/2010-2.2 Guy Pelletier
//       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import java.util.ArrayList;
import java.util.List;

public class Violation {
    public enum ViolationID {V1, V2, V3, V4}

    public ViolationID id;

    public List<ViolationCode> violationCodes;

    public Violation() {
        violationCodes = new ArrayList<ViolationCode>();
    }

    public ViolationID getId() {
        return id;
    }

    public List<ViolationCode> getViolationCodes() {
        return violationCodes;
    }

    public void setId(ViolationID id) {
        this.id = id;
    }

    public void setViolationCodes(List<ViolationCode> violationCodes) {
        this.violationCodes = violationCodes;
    }
}
