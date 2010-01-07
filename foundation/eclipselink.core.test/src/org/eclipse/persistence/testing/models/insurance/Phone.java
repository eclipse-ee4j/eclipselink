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
package org.eclipse.persistence.testing.models.insurance;

import java.io.*;

/**
 * <p><b>Purpose</b>: Represents a policyHolder's collection of phones
 * <p><b>Description</b>: Held in a 1-M from PolicyHolder and stored in a VArray in Oracle8i
 * @since TOPLink/Java 3.0
 */
public class Phone implements Serializable {
    private int areaCode;
    private int number;
    private String type;

    public static Phone example1() {
        Phone phone1 = new Phone();
        phone1.setType("fax");
        phone1.setAreaCode(123);
        phone1.setNumber(23456789);

        return phone1;
    }

    public static Phone example2() {
        Phone phone2 = new Phone();
        phone2.setType("mobile");
        phone2.setAreaCode(613);
        phone2.setNumber(5698855);

        return phone2;
    }

    public static Phone example3() {
        Phone phone3 = new Phone();
        phone3.setType("home");
        phone3.setAreaCode(613);
        phone3.setNumber(9999999);

        return phone3;
    }

    public static Phone example4() {
        Phone phone4 = new Phone();
        phone4.setType("work");
        phone4.setAreaCode(613);
        phone4.setNumber(3333333);

        return phone4;
    }

    public int getAreaCode() {
        return this.areaCode;
    }

    public int getNumber() {
        return this.number;
    }

    public String getType() {
        return this.type;
    }

    public void setAreaCode(int areaCode) {
        this.areaCode = areaCode;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }
}
