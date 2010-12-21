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
package org.eclipse.persistence.testing.tests.validation;

import java.io.StringWriter;


//Created by Vesna
//Feb 2k3
//Used in test: org.eclipse.persistence.testing.tests.validation.NullPointerWhileGettingValueThruMethodAccessorTest (TL-ERROR 70)
//used in test: org.eclipse.persistence.testing.tests.validation.IllegalArgumentWhileGettingValueThruMethodAccessorTest (TL-ERROR 27)
//used in test: org.eclipse.persistence.testing.tests.validation.IllegalArgumentWhileSettingValueThruMethodAccessorTest (TL-ERROR 33)

public class PersonMethodAccess {
    //public long p_id;
    public String p_name;
    public long p_id;
    //private String p_name;

    public PersonMethodAccess() {
        this.p_name = "";
    }

    public long getId() {
        return p_id;
    }

    public void setId(long id) {
        this.p_id = id;
    }

    private String getName() {
        return p_name;
    }

    public void setName(String name) {
        this.p_name = name;
    }

    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write("Person: ");
        //writer.write(getName());
        writer.write(" ");
        return writer.toString();
    }
}


