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


package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.sql.Date;

/**
 * Used by ReportQueryConstructorExpressionTest to test different constructors
 */
public class DataHolder  {

    protected String string;
    protected Date date;
    protected Integer integer;
    protected int primitiveInt;

    public DataHolder() {
    }

    public DataHolder(String string, Date date, Integer integer){
        this.string = string;
        this.date = date;
        this.integer = integer;
    }

    public DataHolder(int primitiveInt){
        this.primitiveInt = primitiveInt;
    }

    public String getString(){
        return string;
    }

    public Date getDate(){
        return date;
    }

    public Integer getInteger(){
        return integer;
    }

    public int getPrimitiveInt(){
        return primitiveInt;
    }
}
