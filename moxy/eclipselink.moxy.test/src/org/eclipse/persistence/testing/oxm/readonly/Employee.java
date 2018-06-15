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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.readonly;

import java.util.Vector;
/**
 *  @version 1.0
 *  @author  mmacivor
 *  @since   10g
 */

public class Employee
{
    public String firstName;
    public String firstName2; //non-readonly mapping

    public Address primaryAddress;
    public Address primaryAddress2;

    public Vector primaryResponsibilities;
    public Vector primaryResponsibilities2;

    public Vector otherAddresses;
    public Vector otherAddresses2;

    public Vector normalHours;
    public Vector normalHours2;
}
