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
package org.eclipse.persistence.tools.workbench.test.models.crimescene;

/**
 * Insert the type's description here.
 * Creation date: (11/6/2000 8:52:26 AM)
 * @author: Christopher Garrett
 */
public class AddressSubClass extends Address {
    private String country;
/**
 * AddressSubClass constructor comment.
 */
public AddressSubClass() {
    super();
}
public String getCountry() {
    return this.country;
}
public void setCountry(String newCountry) {
    this.country = newCountry;
}
@Override
public String toString() {
    return super.toString() + " " + this.country;
}
}
