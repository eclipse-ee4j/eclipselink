/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.inheritance;

public class A_King2 {
    private int index;
    private String bar;

    public A_King2() {
        super();
    }

    public static A_King2 exp1() {
        A_King2 a1 = new A_King2();
        a1.setIndex(1);
        a1.setBar("this is bar");
        return a1;
    }

    public static A_King2 exp2() {
        A_King2 a2 = new A_King2();
        a2.setIndex(2);
        a2.setBar("this is bar two");
        return a2;
    }

    public String getBar() {
        return bar;
    }

    public int getIndex() {
        return index;
    }

    public void setBar(String theBar) {
        bar = theBar;
    }

    public void setIndex(int theIndex) {
        index = theIndex;
    }
}
