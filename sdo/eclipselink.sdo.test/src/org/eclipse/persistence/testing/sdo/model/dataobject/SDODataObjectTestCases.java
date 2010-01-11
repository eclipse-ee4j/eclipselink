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
package org.eclipse.persistence.testing.sdo.model.dataobject;

import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

//import org.eclipse.persistence.sdo.SDO;
public class SDODataObjectTestCases extends SDOTestCase {//TestCase {
    protected SDODataObject dataObject_Path_a_b;
    protected SDODataObject dataObject_Path_b;
    protected SDODataObject dataObject;
    protected SDODataObject dataObject_Not_Open;
    protected SDODataObject dataObject_WithReadOnlyProperty;
    private static final String URINAME = "uri";
    private static final String TYPENAME = "TypeName";
    protected static final String DEFINED_PROPERTY_READONLY_NAME = "readonly";
    protected static final int DEFINED_PROPERTY_READONLY_INDEX = 0;
    protected static final String DEFINED_PROPERTY_NAME = "propertyName";
    protected static final String DEFINED_PROPERTY_NAME_a = "propertyName_a";
    protected static final String DEFINED_PROPERTY_NAME_A_B = "propertyName_a/propertyName";
    protected static final int DEFINED_PROPERTY_INDEX = 0;
    protected static final String UNDEFINED_PROPERTY_NAME = "unPropertyName";
    protected static final int UNDEFINED_PROPERTY_INDEX = 2;
    protected static final String DEFINED_ReadOnly_PROPERTY_NAME = "ReadOnlyPropertyName";
    protected static final String CONTROL_STRING_1 = "test1";
    protected static final String CONTROL_STRING_2 = "test2";
    protected static final String DEFAULT_VALUE = "default";
    protected static final int MINUS_ONE = -1;
    protected static final String DEFINED_MANY_PROPERTY_NAME = "manyPropertyName";
    protected static final int DEFINED_MANY_PROPERTY_INDEX = 1;

    // move this up to SDOTestCase
    //protected HelperContext aHelperContext;
    public SDODataObjectTestCases(String name) {
        super(name);

    }

    public void setUp() {
        super.setUp();
        SDOType type = new SDOType(URINAME, TYPENAME);
        type.setOpen(true);
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(DEFINED_PROPERTY_NAME);
        property.setDefault(DEFAULT_VALUE);
        type.addDeclaredProperty(property);

        SDOProperty manyProperty = new SDOProperty(aHelperContext);
        manyProperty.setName(DEFINED_MANY_PROPERTY_NAME);
        manyProperty.setDefault(DEFAULT_VALUE);
        manyProperty.setMany(true);
        type.addDeclaredProperty(manyProperty);

        SDOType type_Not_Open = new SDOType(URINAME, TYPENAME);
        type_Not_Open.setOpen(false);
        SDOProperty property_ = new SDOProperty(aHelperContext);
        property.setName(DEFINED_PROPERTY_NAME);
        //type_Not_Open.addDeclaredProperty(property_);
        SDOType type_ = new SDOType(URINAME, TYPENAME);

        SDOProperty _property = new SDOProperty(aHelperContext);
        _property.setReadOnly(true);
        _property.setName(DEFINED_PROPERTY_READONLY_NAME);
        type_.addDeclaredProperty(_property);

        SDOType type_Path_a_b = new SDOType(URINAME, TYPENAME);
        type_Path_a_b.setOpen(true);

        SDOProperty property_Path_a_b = new SDOProperty(aHelperContext);
        property_Path_a_b.setName(DEFINED_PROPERTY_NAME_a);
        property_Path_a_b.setDefault(DEFAULT_VALUE);
        type_Path_a_b.addDeclaredProperty(property_Path_a_b);

        SDOType type_Path_b = new SDOType(URINAME, "type_b");
        type_Path_b.setOpen(true);

        SDOProperty property_Path_b = new SDOProperty(aHelperContext);
        property_Path_b.setName(DEFINED_PROPERTY_NAME);
        property_Path_b.setDefault(DEFAULT_VALUE);
        type_Path_b.addDeclaredProperty(property_Path_b);

        dataObject_Path_b = (SDODataObject)dataFactory.create(type_Path_b);

        dataObject_Path_a_b = (SDODataObject)dataFactory.create(type_Path_a_b);
        dataObject_Path_a_b.set(property_Path_a_b, dataObject_Path_b);

        dataObject = (SDODataObject)dataFactory.create(type);
        dataObject_Not_Open = (SDODataObject)dataFactory.create(type_Not_Open);
        dataObject_WithReadOnlyProperty = (SDODataObject)dataFactory.create(type_);

    }
}
