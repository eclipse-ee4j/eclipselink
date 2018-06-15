/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     11/13/2009-2.0 Guy Pelletier
//       - 293629: An attribute referenced from orm.xml is not recognized correctly
package org.eclipse.persistence.testing.models.jpa.xml.relationships;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Human {

    // Added for bug 293629 (marked as transient in mapping file)
    public boolean isNew() {
        return true;
    }
}
