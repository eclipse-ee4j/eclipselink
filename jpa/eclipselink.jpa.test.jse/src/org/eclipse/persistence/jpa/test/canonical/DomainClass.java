/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
//     12/18/2018: Jan Vissers
//       - #539822: JPA Canonical metamodel not processing metamodelMappedSuperclasses
package org.eclipse.persistence.jpa.test.canonical;

import javax.persistence.Entity;

@Entity
public class DomainClass extends DomainPersistable {

    // Holds additional features that one can specify on the DomainInterface
    // Essentially this is an enrichment of the persistable

    @Override
    public Long doSomeBusinessOperation() {

        return null;
    }

}
