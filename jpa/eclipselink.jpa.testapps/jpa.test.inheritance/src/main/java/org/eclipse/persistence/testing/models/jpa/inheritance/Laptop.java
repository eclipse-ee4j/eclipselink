/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumns;
import jakarta.persistence.Table;

@Entity
@Table(name="CMP3_LAPTOP")
@PrimaryKeyJoinColumns({
    @PrimaryKeyJoinColumn(name="MFR", referencedColumnName="MFR"),
    @PrimaryKeyJoinColumn(name="SNO", referencedColumnName="SNO")
})
public class Laptop extends Computer {

    public Laptop() {
    }

    public Laptop(ComputerPK computerPK) {
        super(computerPK);
    }

}
