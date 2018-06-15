/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.exceptions.SDOException;

public class CyclicElementRefErrorTestCases extends XSDHelperDefineTestCases {
    String uri = "my.uri";
    String uri2 = "my.uri2";

    public CyclicElementRefErrorTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(CyclicElementRefErrorTestCases.class);
    }

    public String getSchemaToDefine() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/Cyclic1ElementRefError.xsd";
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
    }

     public void testDefine() {

        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();

        schemaResolver.setBaseSchemaLocation(getSchemaLocation());

        try{
          ((SDOXSDHelper)xsdHelper).define(new StreamSource(getSchemaToDefine()), schemaResolver);
        }catch(SDOException e){
          assertEquals(SDOException.REFERENCED_PROPERTY_NOT_FOUND, e.getErrorCode());
          return;
        }
        fail("An exception should have occurred but didn't");

    }

     public List<SDOType> getControlTypes() {
      return new ArrayList<SDOType>();
     }
   }
