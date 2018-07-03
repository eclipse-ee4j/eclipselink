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
package org.eclipse.persistence.tools.workbench.test.models.complexaggregate;

import java.io.Serializable;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

public class AddressDescription implements Serializable
{
    public ValueHolderInterface address;
    public PeriodDescription periodDescription;
public AddressDescription()
{
    this.address = new ValueHolder();
}
public static AddressDescription example1()
{
    AddressDescription example = new AddressDescription();

    example.setPeriodDescription(PeriodDescription.example1());
    example.getAddress().setValue(Address.example1());
    return example;
}
public static AddressDescription example2()
{
    AddressDescription example = new AddressDescription();

    example.setPeriodDescription(PeriodDescription.example2());
    example.getAddress().setValue(Address.example2());
    return example;
}
public static AddressDescription example3()
{
    AddressDescription example = new AddressDescription();

    example.setPeriodDescription(PeriodDescription.example3());
    example.getAddress().setValue(Address.example3());
    return example;
}
public static AddressDescription example4()
{
    AddressDescription example = new AddressDescription();

    example.setPeriodDescription(PeriodDescription.example4());
    example.getAddress().setValue(Address.example4());
    return example;
}
public static AddressDescription example5()
{
    AddressDescription example = new AddressDescription();

    example.setPeriodDescription(PeriodDescription.example5());
    example.getAddress().setValue(Address.example5());
    return example;
}
public static AddressDescription example6()
{
    AddressDescription example = new AddressDescription();

    example.setPeriodDescription(PeriodDescription.example6());
    example.getAddress().setValue(Address.example6());
    return example;
}
public ValueHolderInterface getAddress()
{
    return this.address;
}
public PeriodDescription getPeriodDescription()
{
    return this.periodDescription;
}
public void setPeriodDescription(PeriodDescription aPeriodDescription)
{
    this.periodDescription = aPeriodDescription;
}
}
