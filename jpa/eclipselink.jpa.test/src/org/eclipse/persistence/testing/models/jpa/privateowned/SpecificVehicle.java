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
//     02/19/09 dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.privateowned;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

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
