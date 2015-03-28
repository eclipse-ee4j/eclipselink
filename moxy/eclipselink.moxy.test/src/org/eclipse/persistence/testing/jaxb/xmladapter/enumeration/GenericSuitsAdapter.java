/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public abstract class GenericSuitsAdapter<T extends Enum> extends XmlAdapter<String, Enum> {
    @Override
    public Enum unmarshal(String v) throws Exception {
        return convert(v);
    }

    public abstract T convert(String value);

    @Override
    public String marshal(Enum v) throws Exception {
        return v.name();
    }
}
