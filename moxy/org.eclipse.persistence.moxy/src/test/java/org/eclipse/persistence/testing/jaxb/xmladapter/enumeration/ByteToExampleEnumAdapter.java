/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.4.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class ByteToExampleEnumAdapter extends XmlAdapter<ExampleEnum, Byte> {
    public ByteToExampleEnumAdapter(){}

    @Override
    public ExampleEnum marshal(Byte v) throws Exception {
        ExampleEnum[] exArray = ExampleEnum.values();
        for(ExampleEnum ex : exArray) {
            if(ex.getValue() == (int) v) {
                return ex;
            }
        }
        return null;
    }

    @Override
    public Byte unmarshal(ExampleEnum v) throws Exception {
        return (byte) v.getValue();
    }
}
