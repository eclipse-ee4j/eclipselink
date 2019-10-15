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
//     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
//       of the Metamodel implementation for EclipseLink 2.0 release involving
//       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
package org.eclipse.persistence.testing.models.jpa.metamodel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity(name="UserMetamodel")
@Table(name="CMP3_MM_USER")
public class User extends Person {

    private static final long serialVersionUID = -1421643624357753266L;
    @Version
    @Column(name="USER_VERSION")
    private int version;

    public User() {}

    public int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }
}
