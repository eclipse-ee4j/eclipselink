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
// dmccann - July 24/2008 - 1.0.1 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;

import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

public class ImportTypeWithSameNameAsElementTestSuite extends XSDHelperDefineTestCases {
    public ImportTypeWithSameNameAsElementTestSuite(String name) {
        super(name);
    }

    public void testDefine() {
        try {
            Source xsdSource = new StreamSource(getSchemaToDefine());
            ((SDOXSDHelper) HelperProvider.getDefaultContext().getXSDHelper()).define(xsdSource, new CustomSchemaResolver());
        } catch (Exception x) {
            fail(x.getMessage());
            throw new RuntimeException(x);
        }
    }

    @Override
    public List getControlTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSchemaToDefine() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/importTypeWithSameNameAsElement/SchemaA.xsd";
    }

    @Override
    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/importTypeWithSameNameAsElement/";
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.ImportTypeWithSameNameAsElementTestSuite" };
        TestRunner.main(arguments);
    }

    // SchemaResolver implementation
    class CustomSchemaResolver extends DefaultSchemaResolver {
        public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
            setBaseSchemaLocation(getSchemaLocation());
            return super.resolveSchema(sourceXSD, namespace, schemaLocation);
        }
    }
}
