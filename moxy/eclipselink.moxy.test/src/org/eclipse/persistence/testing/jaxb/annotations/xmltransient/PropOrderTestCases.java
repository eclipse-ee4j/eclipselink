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
//     Rick Barkhouse - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import javax.xml.bind.JAXBContext;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

/**
 * <p>Ensure that a "transient property not allowed in propOrder" exception is not thrown for the following case:</p>
 *
 * <ul>
 * <li>@XmlAccessorType(XmlAccessType.FIELD)</li>
 * <li>shipTo property specified in propOrder</li>
 * <li>shipTo field is marked @XmlTransient</li>
 * <li>getShipTo() method is marked as @XmlElement (thereby overriding the transient property)
 * </ul>
 *
 */
public class PropOrderTestCases extends TestCase {

    public String getName() {
        return "XmlTransient PropOrderTestCases: " + super.getName();
    }

    public void testContextCreation() throws Exception {
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { PurchaseOrder.class }, null);
    }

}
