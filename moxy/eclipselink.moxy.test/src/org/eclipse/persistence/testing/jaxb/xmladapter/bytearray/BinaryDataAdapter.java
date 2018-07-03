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
//     Blaise Doughan - 2.3.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.bytearray;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BinaryDataAdapter extends XmlAdapter<byte[], BinaryData>{

    @Override
    public BinaryData unmarshal(byte[] v) throws Exception {
        BinaryData binaryData = new BinaryData();
        binaryData.setBytes(v);
        return binaryData;
    }

    @Override
    public byte[] marshal(BinaryData v) throws Exception {
         return v.getBytes();
    }

}
