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
package org.eclipse.persistence.testing.oxm.xmlbinder.basictests;

/**
 *  @version $Header: PhoneNumber.java 11-nov-2003.17:02:38 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class PhoneNumber
{
    public int areaCode;
    public int exchange;
    public int number;

    public int getAreaCode()
    {
        return areaCode;
    }
    public int getExchange()
    {
        return exchange;
    }
    public int getNumber()
    {
        return number;
    }

    public void setAreaCode(int ac)
    {
        areaCode = ac;
    }
    public void setExchange(int exch)
    {
        exchange = exch;
    }
    public void setNumber(int num)
    {
        number = num;
    }
}
