/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.5.1 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement
public class HexRoot {

    @XmlSchemaType(name="hexBinary")
    public byte[] bytes;

    public boolean equals(Object obj) {
        HexRoot r = (HexRoot)obj;

        return new String(this.bytes).equals(new String(r.bytes));
    }

}
