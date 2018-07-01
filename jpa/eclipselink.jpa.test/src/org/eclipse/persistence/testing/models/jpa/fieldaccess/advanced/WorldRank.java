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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import javax.persistence.*;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.annotations.PrimaryKey;

@Entity(name="WorldRank")
@Table(name="CMP3_FA_WORLDRANK")
@PrimaryKey(validation=IdValidation.NULL)
public class WorldRank implements java.io.Serializable {
    @Id
    @Column(name="ID")
    private int id;

    public WorldRank() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

