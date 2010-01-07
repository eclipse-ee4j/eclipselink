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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.exceptions.SDOException;

public class DefineFailsDontRegisterTypesTestCases extends XSDHelperDefineTestCases {
    public DefineFailsDontRegisterTypesTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(DefineFailsDontRegisterTypesTestCases.class);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/DefineFails.xsd";
    }

    public void testDefine() {
        InputStream is = getSchemaInputStream(getSchemaToDefine());
        int sizeBefore = ((SDOTypeHelper)typeHelper).getTypesHashMap().size();
        
        boolean exceptionCase = false;
        List types = null;
        try {
            types = xsdHelper.define(is, getSchemaLocation());
        } catch (SDOException e) {
            exceptionCase = true;
            //do nothing
        }
        assertTrue(exceptionCase);

        assertEquals(sizeBefore, ((SDOTypeHelper)typeHelper).getTypesHashMap().size());
        assertNull(types);
        assertNull(typeHelper.getType("http://www.example.org", "address-type"));
        assertNull(typeHelper.getType("http://www.example.org", "customer-type"));
        assertNull(typeHelper.getType("http://www.example.org", "bad-type"));
        
        assertNull(((SDOXSDHelper)xsdHelper).getGlobalProperty("http://www.example.org", "customer", true));
    }

    public List getControlTypes() {
        List types = new ArrayList();
        return types;
    }
}