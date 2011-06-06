/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.model.dataobject;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import junit.framework.TestCase;

public class SDODataObjectConversionWithPathTestCases extends SDOTestCase {// TestCase {
    protected static final String URINAME = "uri";
    protected static final String TYPENAME_A = "TypeName-a";
    protected static final String TYPENAME_B = "TypeName-b";
    protected static final String TYPENAME_C = "TypeName-c";
    protected static final String PROPERTY_NAME_A = "PName-a";
    protected static final String PROPERTY_NAME_B = "PName-b";
    protected static final String PROPERTY_NAME_C = "PName-c";
    protected static final String property = "PName-a/PName-b/PName-c";
    protected static final String propertyPath_a_b_c = "PName-a/PName-b/PName-c";
    protected static final int PROPERTY_INDEX = 0;
    protected static final String PROPERTY_NAME_A_LENGTH_1 = "PName-a-length-1";
    protected SDODataObject dataObject_a;
    protected SDODataObject dataObject_a_bNotSDODataOject;
    protected SDODataObject dataObject_b;
    protected SDODataObject dataObject_b_bNotSDODataOject;
    protected SDODataObject dataObject_c;
    protected SDODataObject dataObject_c_bNotSDODataOject;
    protected SDODataObject dataObject_a_pathLength_1;
    protected SDOType type_a;
    protected SDOType type_a_bNotSDODataOject;
    protected SDOType type_b;
    protected SDOType type_b_bNotSDODataOject;
    protected SDOType type_c;
    protected SDOType type_c_bNotSDODataOject;
    protected SDOType type_a_pathLength_1;
    protected SDOProperty property_a;
    protected SDOProperty property_a_bNotSDODataOject;
    protected SDOProperty property_b;
    protected SDOProperty property_b_bNotSDODataOject;
    protected SDOProperty property_c;
    protected SDOProperty property_c_bNotSDODataOject;
    protected SDOProperty property_a_pathLength_1;

    public SDODataObjectConversionWithPathTestCases(String name) {
        super(name);
    }

    public void setUp() {// set up as a/b/c
        super.setUp();
        type_a = new SDOType(URINAME, TYPENAME_A);
        type_b = new SDOType(URINAME, TYPENAME_B);
        type_c = new SDOType(URINAME, TYPENAME_C);
        type_a_bNotSDODataOject = new SDOType(URINAME, TYPENAME_A);
        type_b_bNotSDODataOject = new SDOType(URINAME, TYPENAME_B);
        type_c_bNotSDODataOject = new SDOType(URINAME, TYPENAME_C);
        type_a_pathLength_1 = new SDOType(URINAME, TYPENAME_A);

        property_a = new SDOProperty(aHelperContext);
        property_a.setType(type_b);
        property_a.setName(PROPERTY_NAME_A);
        type_a.addDeclaredProperty(property_a);
        dataObject_a =  (SDODataObject)dataFactory.create(type_a);
                
        property_b = new SDOProperty(aHelperContext);
        property_b.setName(PROPERTY_NAME_B);
        property_b.setType(type_c);
        type_b.addDeclaredProperty(property_b);        
        dataObject_b = (SDODataObject)dataFactory.create(type_b);
        
        dataObject_c = (SDODataObject)dataFactory.create(type_c);
        dataObject_a.set(property_a, dataObject_b);// a dataobject's a property has value b dataobject
        dataObject_b.set(property_b, dataObject_c);// b dataobject's b property has value c dataobject
        
        

        property_a_bNotSDODataOject = new SDOProperty(aHelperContext);
        property_a_bNotSDODataOject.setName(PROPERTY_NAME_A);
        property_a_bNotSDODataOject.setType(type_b_bNotSDODataOject);
        type_a_bNotSDODataOject.addDeclaredProperty(property_a_bNotSDODataOject);
          
        dataObject_a_bNotSDODataOject = (SDODataObject)dataFactory.create(type_a_bNotSDODataOject);
        
        
        property_b_bNotSDODataOject = new SDOProperty(aHelperContext);
        property_b_bNotSDODataOject.setName(PROPERTY_NAME_B);
        property_b_bNotSDODataOject.setType(SDOConstants.SDO_STRING);
        type_b_bNotSDODataOject.addDeclaredProperty(property_b_bNotSDODataOject);        
        dataObject_b_bNotSDODataOject = (SDODataObject)dataFactory.create(type_b_bNotSDODataOject);

        
        dataObject_c_bNotSDODataOject = (SDODataObject)dataFactory.create(type_c);
        dataObject_a_bNotSDODataOject.set(property_a_bNotSDODataOject, dataObject_b_bNotSDODataOject);// a dataobject's a property has value b dataobject
        dataObject_b_bNotSDODataOject.set(property_b_bNotSDODataOject, "test");// b dataobject's b property has value c dataobject

        /*List basTypes_a = new ArrayList();
        basTypes_a.add(type_b);
        basTypes_a.add(type_c);

        List basTypes_b = new ArrayList();
        basTypes_b.add(type_c);

        type_a.setBaseTypes(basTypes_a);
        type_b.setBaseTypes(basTypes_b);*/
        
        property_a_pathLength_1 = new SDOProperty(aHelperContext);
        property_a_pathLength_1.setName(PROPERTY_NAME_A_LENGTH_1);
        type_a_pathLength_1.addDeclaredProperty(property_a_pathLength_1);
        
        dataObject_a_pathLength_1 = (SDODataObject)dataFactory.create(type_a_pathLength_1);

    }
}
