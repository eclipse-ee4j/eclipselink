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
package org.eclipse.persistence.testing.oxm.xmlbinder.basictests;

/**
 *  @version $Header: CanadianAddress.java 11-nov-2003.17:02:38 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class CanadianAddress extends Address
{
    public String province;
    public String postalCode;

    public String getProvince()
    {
        return province;
    }
    public String getPostalCode()
    {
        return postalCode;
    }

    public void setProvince(String prov)
    {
        province = prov;
    }
    public void setPostalCode(String postal)
    {
        postalCode = postal;
    }
}
