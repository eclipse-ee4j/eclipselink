/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.aggregate.nested;

public class Guardian {
    protected String firstName;
    protected String lastName;
    protected MailingAddress mailingAddress;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public MailingAddress getMailingAddress() {
        return mailingAddress;
    }

    public void setFirstName(String param1) {
        firstName = param1;
    }

    public void setLastName(String param1) {
        lastName = param1;
    }

    public void setMailingAddress(MailingAddress param1) {
        mailingAddress = param1;
    }
}
