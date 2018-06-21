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
 * Creation date: (9/22/00 10:51:06 AM)
 * @author: Christopher Garrett
 */
public class Person {
    private int id;
    private String firstName;
    private String lastName;
/**
 * Person constructor comment.
 */
public Person() {
    super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 5:23:20 PM)
 * @return java.lang.String
 */
public java.lang.String getFirstName() {
    return firstName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 5:23:20 PM)
 * @return int
 */
public int getId() {
    return id;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 5:23:20 PM)
 * @return java.lang.String
 */
public java.lang.String getLastName() {
    return lastName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 5:23:20 PM)
 * @param newFirstName java.lang.String
 */
public void setFirstName(java.lang.String newFirstName) {
    firstName = newFirstName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 5:23:20 PM)
 * @param newId int
 */
public void setId(int newId) {
    id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 5:23:20 PM)
 * @param newLastName java.lang.String
 */
public void setLastName(java.lang.String newLastName) {
    lastName = newLastName;
}
}
