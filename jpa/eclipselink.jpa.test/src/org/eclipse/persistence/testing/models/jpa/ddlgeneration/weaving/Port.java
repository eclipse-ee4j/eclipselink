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
//     01/12/2009-1.1 Daniel Lo
//       - 247041: Null element inserted in the ArrayList
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving;

public interface Port {

    public long getEntityId();

    public String getId();
    public void setId( String id );

    public int getPortOrder();
    public void setPortOrder(int portOrder);

    public Equipment getEquipment();
    public void setEquipment(Equipment equipment);

    public Equipment getVirtualEquipment();
    public void setVirtualEquipment(Equipment equipment);
}
