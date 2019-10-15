/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.dynamic.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;

public final class ComputerAdapter extends XmlAdapter<String, Computer> {

    private final String DELIM = "::";

    public String marshal(Computer arg0) throws Exception {
        String marshalString = "";

        marshalString += arg0.ipCode + DELIM;
        marshalString += arg0.macCode + DELIM;
        marshalString += arg0.workgroup;

        return marshalString;
    }

    public Computer unmarshal(String arg0) throws Exception {
        Computer c = new Computer();

        StringTokenizer tokenizer = new StringTokenizer(arg0, DELIM);
        if (tokenizer.countTokens() == 3) {
            c.ipCode = Integer.valueOf(tokenizer.nextToken());
            c.macCode = Integer.valueOf(tokenizer.nextToken());
            c.workgroup = tokenizer.nextToken().charAt(0);

            return c;
        } else {
            return null;
        }
    }

}
