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
package org.eclipse.persistence.tools.workbench.test.models.phone;

/**
 * Insert the type's description here.
 * Creation date: (6/1/00 2:50:55 PM)
 * @author: Christopher Garrett
 */
public class PhoneNumber implements Contact {
    int id;
    String number;
/**
 * PhoneNumber constructor comment.
 */
public PhoneNumber() {
    super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:56:26 PM)
 * @return int
 */
public int getId() {
    return id;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:56:26 PM)
 * @return java.lang.String
 */
public java.lang.String getNumber() {
    return number;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:56:26 PM)
 * @param newId int
 */
public void setId(int newId) {
    id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:56:26 PM)
 * @param newNumber java.lang.String
 */
public void setNumber(java.lang.String newNumber) {
    number = newNumber;
}
}
