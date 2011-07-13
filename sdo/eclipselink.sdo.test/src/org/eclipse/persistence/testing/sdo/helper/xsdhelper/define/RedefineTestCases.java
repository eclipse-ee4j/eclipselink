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
 *     Blaise Doughan - 2.1.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.io.InputStream;
import java.util.List;

import org.eclipse.persistence.sdo.helper.SDOHelperContext;

import commonj.sdo.helper.XSDHelper;

import junit.framework.TestCase;

public class RedefineTestCases extends TestCase {

    private static final String ANONYMOUS_COMPLEX_TYPES = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/redefine/AnonymousComplexTypes.xsd";
    private static final String GLOBAL_COMPLEX_TYPES = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/redefine/GlobalComplexTypes.xsd";
    private static final String GLOBAL_ELEMENTS = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/redefine/GlobalElements.xsd";

    public void testAnonymousComplexTypes() throws Exception {
        doTest(ANONYMOUS_COMPLEX_TYPES, 2);
    }

    public void testGlobalComplexTypes() throws Exception {
        doTest(GLOBAL_COMPLEX_TYPES, 2);
    }

    public void testGlobalElements() throws Exception {
        doTest(GLOBAL_ELEMENTS, 2);
    }

    private void doTest(String resource, int numberOfTypes) throws Exception {
        SDOHelperContext sdoHelperContext = new SDOHelperContext();
        XSDHelper xsdHelper = sdoHelperContext.getXSDHelper();

        InputStream xsd;
        List types;

        xsd = getSchema(resource);
        types = xsdHelper.define(xsd, null);
        assertEquals(numberOfTypes, types.size());
        xsd.close();

        xsd = getSchema(resource);
        types = xsdHelper.define(xsd, null);
        assertEquals(0, types.size());
        xsd.close();
    }

    private InputStream getSchema(String resource) {
        return Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(resource);
    }

}
