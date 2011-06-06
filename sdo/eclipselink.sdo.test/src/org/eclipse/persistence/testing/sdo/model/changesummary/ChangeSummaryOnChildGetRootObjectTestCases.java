/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.model.changesummary;

import java.io.InputStream;

import org.eclipse.persistence.sdo.helper.SDOHelperContext;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

import junit.framework.TestCase;

public class ChangeSummaryOnChildGetRootObjectTestCases extends TestCase {

    private static final String XSD_RESOURCE_1 = "org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug335242/schema1.xsd";
    private static final String XSD_RESOURCE_2 = "org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug335242/schema2.xsd";
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug335242/input.xml";

    private HelperContext helperContext;

    @Override
    protected void setUp() throws Exception {
        helperContext = new SDOHelperContext();
        setUpSchema(XSD_RESOURCE_1);
        setUpSchema(XSD_RESOURCE_2);
    }

    private void setUpSchema(String xsdResource) throws Exception {
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(xsdResource);
        helperContext.getXSDHelper().define(xsd, null);
        xsd.close();
    }

    public void testGetRootObjectOnChildCS() throws Exception {
        InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_RESOURCE);
        XMLDocument doc = helperContext.getXMLHelper().load(xml);
        xml.close();

        DataObject rootDO = doc.getRootObject();
        DataObject childDO = rootDO.getDataObject("currentProductInstance");
        ChangeSummary changeSummary = childDO.getChangeSummary();
        assertSame(childDO, changeSummary.getRootObject());
    }

}