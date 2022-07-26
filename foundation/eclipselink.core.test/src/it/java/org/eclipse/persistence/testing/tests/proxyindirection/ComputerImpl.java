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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.proxyindirection;

public class ComputerImpl implements Computer {
    public int id;
    public String description;
    public String serialNumber;

    public ComputerImpl() {
        setSerialNumber(new String());
        setDescription(new String());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setDescription(String aDescription) {
        description = aDescription;
    }

    @Override
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String toString()
    {
        return "Computer(" + description + "--" + serialNumber + ")" ;
    }
}
