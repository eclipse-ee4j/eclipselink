/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* dmccann - July 24/2008 - 1.0.1 - Initial implementation
******************************************************************************/
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

public class ExtendTypeWithSimpleContentTest extends XSDHelperDefineTestCases {
    public ExtendTypeWithSimpleContentTest(String name) {
        super(name);
    }

    public void testDefine() {
        try {
            DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
            schemaResolver.setBaseSchemaLocation(getSchemaLocation());
            Source xsdSource = new StreamSource(getSchemaToDefine());
            ((SDOXSDHelper) HelperProvider.getDefaultContext().getXSDHelper()).define(xsdSource, schemaResolver);
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
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/ExtendTypeWithSimpleContent/SchemaA.xsd";
    }

    @Override
    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/ExtendTypeWithSimpleContent/";
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.ExtendTypeWithSimpleContentTest" };
        TestRunner.main(arguments);
    }

}
