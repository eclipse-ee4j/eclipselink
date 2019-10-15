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
// Matt MacIvor - July 4th 2011
package org.eclipse.persistence.testing.jaxb.xmladapter.choice;

public class BarBToBarCAdapter extends javax.xml.bind.annotation.adapters.XmlAdapter<BarC, BarB>{

    @Override
    public BarC marshal(BarB arg0) throws Exception {
        BarC barC = new BarC();
        barC.a = "adapted-a";
        barC.b = "adapted-c";
        return barC;
    }

    @Override
    public BarB unmarshal(BarC arg0) throws Exception {
        return new BarB();
    }

}
