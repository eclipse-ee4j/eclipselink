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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;

public class AttributeGroupTestCases  extends XSDHelperDefineTestCases {
    public AttributeGroupTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(AttributeGroupTestCases.class);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/soap_encoding_1.1.xsd";
    }

      public void testDefine() {
        //String xsdSchema = getSchema(getSchemaToDefine());
        InputStream is = getSchemaInputStream(getSchemaToDefine());
        List types = xsdHelper.define(is, getSchemaLocation());

        //List types = xsdHelper.define(xsdSchema, getSchemaLocation());
        log("\nExpected:\n");
        //List controlTypes = getControlTypes();
        //log(controlTypes);

        log("\nActual:\n");
        log(types);

        //compare(getControlTypes(), types);
    }

    public List getControlTypes()
    {
      return new ArrayList();
    }

}
