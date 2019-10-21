/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 */
// Contributors:Suresh Balakrishnan

package org.eclipse.persistence.testing.models.employee.interfaces;

import java.io.Serializable;

import org.eclipse.persistence.testing.models.employee.domain.Contact;

public interface Customer extends Serializable {
    public void addContact(Contact c);

}
