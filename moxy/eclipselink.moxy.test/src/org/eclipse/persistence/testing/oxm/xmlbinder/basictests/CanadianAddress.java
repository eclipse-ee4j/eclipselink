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
