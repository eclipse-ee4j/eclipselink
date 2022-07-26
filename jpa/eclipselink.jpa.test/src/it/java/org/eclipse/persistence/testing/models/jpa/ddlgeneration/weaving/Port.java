/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     01/12/2009-1.1 Daniel Lo
//       - 247041: Null element inserted in the ArrayList
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving;

public interface Port {

    long getEntityId();

    String getId();
    void setId(String id);

    int getPortOrder();
    void setPortOrder(int portOrder);

    Equipment getEquipment();
    void setEquipment(Equipment equipment);

    Equipment getVirtualEquipment();
    void setVirtualEquipment(Equipment equipment);
}
