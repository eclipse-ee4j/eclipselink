/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2014, 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     11/04/2014 - Rick Curtis
//       - 450010 : Add java se test bucket
package org.eclipse.persistence.jpa.test.framework;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public enum DDLGen {
    DROP_CREATE, CREATE_UPDATE, NONE;

    public String toString() {
        switch (this) {
        case DROP_CREATE:
            return PersistenceUnitProperties.DROP_AND_CREATE;
        case CREATE_UPDATE:
            return PersistenceUnitProperties.CREATE_OR_EXTEND;
        case NONE:
            return "None";
        }
        // unexpected
        throw new RuntimeException("Unknown Table type.");
    };

}
