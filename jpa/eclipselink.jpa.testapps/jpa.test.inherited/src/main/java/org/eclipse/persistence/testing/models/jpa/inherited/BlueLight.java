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
//     05/30/2008-1.0M8 Guy Pelletier
//       - 230213: ValidationException when mapping to attribute in MappedSuperClass
package org.eclipse.persistence.testing.models.jpa.inherited;

import jakarta.persistence.Entity;

@Entity
public class BlueLight extends Blue {
    public BlueLight() {}

    private int discount = 0;

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

}
