/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//              dclarke - initial JPA Employee example using XML (bug 217884)
//              mbraeuer - annotated version
package org.eclipse.persistence.testing.models.jpa.performance2;

import javax.persistence.*;

/**
 * The SmallProject class demonstrates usage of a way to limit a subclass to its
 * parent's table when JOINED inheritance is used. This avoids having to have an
 * empty SMALLPROJECT table by setting the table to that of the superclass.
 */
@Entity
@Table(name = "P2_SPROJECT")
public class SmallProject extends Project {

    private SmallProject() {
        super();
    }

    public SmallProject(String name, String description) {
        this();
        setName(name);
        setDescription(description);
    }

}
