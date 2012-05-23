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
 * Matt MacIvor - July 4th 2011
 ******************************************************************************/
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
