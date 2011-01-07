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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.returning.model;

import java.math.BigDecimal;

public class Class1 extends BaseClass {
    public BigDecimal id;

    public Class2 aggregate;

    public Class1() {
        super();
    }

    public Class1(Class2 aggregate) {
        super();
        this.aggregate = aggregate;
    }

    public Class1(double a, double b) {
        super(a, b);
    }

    public Class1(double a, double b, double c) {
        super(a, b, c);
    }

    public Class1(double c) {
        super(c);
    }

    public Class1(double a, double b, Class2 aggregate) {
        super(a, b);
        this.aggregate = aggregate;
    }

    public Class1(double a, double b, double c, Class2 aggregate) {
        super(a, b, c);
        this.aggregate = aggregate;
    }

    public Class1(double c, Class2 aggregate) {
        super(c);
        this.aggregate = aggregate;
    }

    public String getFieldAName() {
        return "A1";
    }

    public String getFieldBName() {
        return "B1";
    }

    public void updateWith(Class1 other) {
        super.updateWith(other);
        if (aggregate != null && other.aggregate == null) {
            aggregate = null;
        } else if (aggregate == null && other.aggregate != null) {
            aggregate = new Class2();
        }

        if (aggregate != null && other.aggregate != null) {
            aggregate.updateWith(other.aggregate);
        }
    }

    protected boolean compareWithoutId(Class1 other) {
        if (super.compareWithoutId(other)) {
            if (aggregate == null && other.aggregate == null) {
                return true;
            } else if (aggregate == null && other.aggregate != null) {
                return false;
            } else if (aggregate != null && other.aggregate == null) {
                return false;
            } else {
                return aggregate.compareWithoutId(other.aggregate);
            }
        } else {
            return false;
        }
    }

    public Object clone() {
        Class1 clone = new Class1();
        clone.id = id;
        clone.setAB(getA(), getB());
        clone.setC(getC());
        if (aggregate != null) {
            clone.aggregate = (Class2)aggregate.clone();
        }
        return clone;
    }

    public boolean isValid() {
        if (super.isValid()) {
            if (aggregate == null) {
                return true;
            } else {
                return aggregate.isValid();
            }
        } else {
            return false;
        }
    }

    public String toString() {
        String str = super.toString();
        if (aggregate != null) {
            str = str + " Aggregate: " + aggregate;
        }
        return str;
    }
}
