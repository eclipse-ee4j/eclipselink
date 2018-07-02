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
package org.eclipse.persistence.tools.workbench.test.models.contact;

public class EmailAddress implements Contact {
    int id;
    String address;
/**
 * EmailAddress constructor comment.
 */
public EmailAddress() {
    super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:56:36 PM)
 * @return java.lang.String
 */
public java.lang.String getAddress() {
    return address;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:56:36 PM)
 * @return int
 */
public int getId() {
    return id;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:56:36 PM)
 * @param newAddress java.lang.String
 */
public void setAddress(java.lang.String newAddress) {
    address = newAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:56:36 PM)
 * @param newId int
 */
public void setId(int newId) {
    id = newId;
}
}
