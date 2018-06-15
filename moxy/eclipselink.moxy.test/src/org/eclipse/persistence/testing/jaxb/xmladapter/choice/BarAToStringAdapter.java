/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Matt MacIvor - July 4th 2011
package org.eclipse.persistence.testing.jaxb.xmladapter.choice;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BarAToStringAdapter extends XmlAdapter<String, BarA> {

    @Override
    public String marshal(BarA arg0) throws Exception {
        return "Adapted String";
    }

    @Override
    public BarA unmarshal(String arg0) throws Exception {
        return new BarA();
    }

}
