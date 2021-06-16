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
//     12/14/2017-3.0 Tomas Kraus
//       - 291546: Performance degradation due to usage of Vector in DescriptorEventManager
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.testing.models.events.Address;
import org.eclipse.persistence.testing.models.events.AddressDescriptorEventListener;
import org.eclipse.persistence.testing.models.events.EmailAccount;
import org.eclipse.persistence.testing.models.events.Phone;
import org.eclipse.persistence.testing.framework.*;

public class EventHookTestCase extends AutoVerifyTestCase {
    public EmailAccount emailAccount;
    public Phone phoneNo;
    public AddressDescriptorEventListener addressListener;
    public Address address;

    protected Address getAddress()
  {
    return address;
  }

    protected AddressDescriptorEventListener getAddressListener()
  {
    return addressListener;
  }

    protected EmailAccount getEmailAccount()
  {
    return emailAccount;
  }

    protected Phone getPhoneNumber()
  {
    return phoneNo;
  }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }

    protected void setAddress(Address address) {
        this.address = address;
    }

    protected void setAddressListener(AddressDescriptorEventListener addressListener) {
        this.addressListener = addressListener;
    }

    protected void setEmailAccount(EmailAccount newAccount) {
        this.emailAccount = newAccount;
    }

    protected void setPhoneNumber(Phone phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setup() {
        beginTransaction();
        setEmailAccount(EmailAccount.example1());
        setPhoneNumber(Phone.example1());
        setAddress(Address.example1());
        setAddressListener((AddressDescriptorEventListener)getSession().getDescriptor(Address.class).getEventManager().getEventListeners().get(0));
    }
}
