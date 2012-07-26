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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="root")
public class EnumRoot {
    
    @XmlJavaTypeAdapter(ByteToExampleEnumAdapter.class)
    public Byte single;
    
    @XmlJavaTypeAdapter(ByteToExampleEnumAdapter.class)
    public List<Byte> multi;
    
    public boolean equals(Object obj) {
        EnumRoot root = (EnumRoot)obj;
        return root.single.equals(single) && root.multi.equals(multi);
    }

}
