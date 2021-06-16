/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.4.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class ByteToExampleEnumAdapter extends XmlAdapter<ExampleEnum, Byte> {
    public ByteToExampleEnumAdapter(){}

    public ExampleEnum marshal(Byte v) throws Exception {
        ExampleEnum[] exArray = ExampleEnum.values();
        for(ExampleEnum ex : exArray) {
            if(ex.getValue() == (int)v.byteValue()) {
                return ex;
            }
        }
        return null;
    }

    public Byte unmarshal(ExampleEnum v) throws Exception {
        return new Byte((byte)v.getValue());
    }
}
