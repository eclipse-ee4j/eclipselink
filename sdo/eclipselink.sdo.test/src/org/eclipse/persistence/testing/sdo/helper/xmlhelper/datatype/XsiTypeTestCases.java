/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - August 7/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype;

import java.io.FileInputStream;

import org.eclipse.persistence.sdo.helper.SDOHelperContext;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

import junit.framework.TestCase;

public class XsiTypeTestCases extends TestCase {

    private XMLHelper xmlHelper;

    public XsiTypeTestCases(String name) {
        super(name);
    }

    public void setUp() {
        HelperContext helperContext = new SDOHelperContext();
        xmlHelper = helperContext.getXMLHelper();
    }

    public void testInt() throws Exception {
        load("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/intXsiType.xml");
    }

    public void load(String resource) throws Exception {
        FileInputStream inputStream = new FileInputStream(resource);
        XMLDocument document = xmlHelper.load(inputStream, null, null);
    }

}
