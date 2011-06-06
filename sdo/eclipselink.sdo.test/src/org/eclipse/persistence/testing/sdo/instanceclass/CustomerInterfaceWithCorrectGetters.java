/*******************************************************************************
* Copyright (c) 1998, 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - August 18/2009 - 1.2 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.instanceclass;

import java.util.List;

public interface CustomerInterfaceWithCorrectGetters {

    public String getName();
    public void setName(String name);

    public Address getAddress();
    public void setAddress(Address address);

    public List getPhoneNumber();
    public void setPhoneNumber(List phoneNumber);

}