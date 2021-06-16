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


