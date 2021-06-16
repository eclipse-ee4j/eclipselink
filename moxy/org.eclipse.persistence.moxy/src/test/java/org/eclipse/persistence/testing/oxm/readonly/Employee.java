/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
