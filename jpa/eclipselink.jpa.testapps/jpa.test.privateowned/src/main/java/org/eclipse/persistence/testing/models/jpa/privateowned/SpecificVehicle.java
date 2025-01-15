/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     02/19/09 dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.privateowned;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity(name="PO_SpecificVehicle")
@DiscriminatorValue("SV")
public class SpecificVehicle extends Vehicle{

    public SpecificVehicle() {
        super();
    }

    public SpecificVehicle(String model) {
        super(model);
    }
}
