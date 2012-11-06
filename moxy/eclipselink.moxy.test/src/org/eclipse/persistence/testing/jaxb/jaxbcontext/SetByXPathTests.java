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
 *  - rbarkhouse - 06 November 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class SetByXPathTests extends junit.framework.TestCase {

    public String getName() {
        return "JAXB Context setByXPath Tests: " + super.getName();
    }

    public void testSetByXPathPositionArray() throws JAXBException {
        JAXBContext eCtx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { TestBean.class }, null);

        TestBean t1 = new TestBean();
        t1.name = new String[3];
        t1.subBean = new TestBean[2];

        TestBean t2 = new TestBean();
        t2.name = new String[3];

        TestBean t3 = new TestBean();
        t3.name = new String[3];
        t3.name[1] = "MIDDLE_NAME";

        t1.subBean[0] = t2;
        t1.subBean[1] = t3;
        
        eCtx.setValueByXPath(t1, "name[1]/text()", null, "Alfred");
        assertEquals("Alfred", t1.name[0]);
        
        eCtx.setValueByXPath(t1, "subBean[2]/name[1]/text()", null, "Malcolm");
        assertEquals("Malcolm", t1.subBean[1].name[0]);
        
        Object o = eCtx.getValueByXPath(t1, "subBean[2]/name[2]/text()", null, Object.class);
        assertNotNull(o);
        eCtx.setValueByXPath(t1, "subBean[2]/name[2]/text()", null, null);
        assertNull(t1.subBean[1].name[1]);
    }

}