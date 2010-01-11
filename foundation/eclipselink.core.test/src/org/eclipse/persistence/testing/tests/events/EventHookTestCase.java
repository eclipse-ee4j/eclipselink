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
        setAddressListener((AddressDescriptorEventListener)getSession().getDescriptor(Address.class).getEventManager().getEventListeners().firstElement());
    }
}
