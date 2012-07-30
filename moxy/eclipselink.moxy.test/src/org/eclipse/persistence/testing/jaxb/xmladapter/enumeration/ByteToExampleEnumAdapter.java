/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.4.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

import javax.xml.bind.annotation.adapters.XmlAdapter;

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
