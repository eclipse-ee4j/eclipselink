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
package org.eclipse.persistence.tools.workbench.test.models.contact;

import java.io.Serializable;

public class PersonImpl implements Person, Serializable {
    int id;
    Contact contact;
/**
 * PersonImpl constructor comment.
 */
public PersonImpl() {
    super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:53:26 PM)
 * @return org.eclipse.persistence.tools.workbench.test.models.contact.Contact
 */
public Contact getContact() {
    return contact;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:53:26 PM)
 * @return int
 */
public int getId() {
    return id;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:53:26 PM)
 * @param newContact org.eclipse.persistence.tools.workbench.test.models.contact.Contact
 */
public void setContact(Contact newContact) {
    contact = newContact;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 2:53:26 PM)
 * @param newId int
 */
public void setId(int newId) {
    id = newId;
}
}
