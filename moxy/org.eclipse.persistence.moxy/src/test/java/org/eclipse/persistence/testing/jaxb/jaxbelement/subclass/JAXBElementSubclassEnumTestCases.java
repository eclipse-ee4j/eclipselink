/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - January 2012 - 2.3
package org.eclipse.persistence.testing.jaxb.jaxbelement.subclass;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.enumeration.Coin;

public class JAXBElementSubclassEnumTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/subclass/enum.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/subclass/enum.json";

    public JAXBElementSubclassEnumTestCases(String name) throws Exception {
        super(name);
        this.setClasses(new Class[] {SubClassEnum.class, ObjectFactoryEnum.class});
        this.setControlDocument(XML_RESOURCE);
        this.setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        SubClassEnum subClassEnum = new SubClassEnum(new QName("root"), Coin.class, Coin.NICKEL);
        return subClassEnum;
    }
}
