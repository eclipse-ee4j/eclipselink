/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 01 November 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class GetByXPathTests extends junit.framework.TestCase {

    public String getName() {
        return "JAXB Context getByXPath Tests: " + super.getName();
    }

    public void testGetByXPathPositionArray() throws JAXBException {
        JAXBContext eCtx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { TestBean.class }, null);

        TestBean t1 = new TestBean();
        t1.name = new String[3];
        t1.subBean = new TestBean[2];
        t1.name[0] = "Alfred"; t1.name[1] = "E"; t1.name[2] = "Newman";

        TestBean t2 = new TestBean();
        t2.name = new String[3];
        t2.name[0] = "Franklin"; t2.name[1] = "D"; t2.name[2] = "Roosevelt";

        TestBean t3 = new TestBean();
        t3.name = new String[3];
        t3.name[0] = "Malcolm"; t3.name[1] = null; t3.name[2] = "McDowell";

        t1.subBean[0] = t2;
        t1.subBean[1] = t3;

        Object o = eCtx.getValueByXPath(t1, "name[1]/text()", null, Object.class);
        assertEquals("Alfred", o);
        o = eCtx.getValueByXPath(t1, "subBean[2]/name[1]/text()", null, Object.class);
        assertEquals("Malcolm", o);
        o = eCtx.getValueByXPath(t1, "subBean[2]/name[2]/text()", null, Object.class);
        assertNull(o);
    }

}