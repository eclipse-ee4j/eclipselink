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
package org.eclipse.persistence.testing.sdo.helper.equalityhelper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDOEqualityHelperEqualTestCases extends SDOTestCase {
    protected String rootTypeName = "rootTypeName";
    protected String rootTypeUri = "rootTypeTypeUri";
    protected String containedTypeName = "containedTypeName";
    protected String containedTypeUri = "containedTypeTypeUri";
    protected String containedType1Name = "containedType1Name";
    protected String containedType1Uri = "containedType1TypeUri";
    protected String containedByContainedTypeName = "containedByContainedTypeName";
    protected String containedBycontainedType1Uri = "containedBycontainedTypeTypeUri";
    protected SDODataObject root;
    protected SDODataObject root1;
    protected SDODataObject containedDataObject;
    protected SDODataObject containedDataObject_1;
    protected SDODataObject containedDataObject1;
    protected SDODataObject containedByContainedDataObject;
    protected SDOType rootType;
    protected SDOType containedType;
    protected SDOType containedType_1;
    protected SDOType containedType1;
    protected SDOType containedByContainedType;
    protected SDOProperty rootProperty1;
    protected SDOProperty rootProperty2;
    protected SDOProperty rootProperty3;
    protected SDOProperty rootProperty4;
    protected SDOProperty containedProperty1;
    protected SDOProperty containedProperty2;
    protected SDOProperty containedProperty_ChangeSummary;
    protected SDOProperty contained1Property1;
    protected SDOProperty containedByContainedProperty1;
    protected SDOProperty containedByContainedProperty2;

    // Data structures for UC02xx (bidirectional property between internal nodes)
    protected String containedType3Name = "containedType3Name";
    protected String containedType3Uri = "containedType3TypeUri";
    protected SDOType containedByContainedType3;
    protected SDODataObject containedByContainedDataObject3;
    protected String containedByContainedTypeName3 = "containedByContainedTypeName3";
    protected String containedBycontainedType3Uri = "containedBycontainedTypeType3Uri";
    protected SDOProperty containedByContainedProperty3;
    protected SDOProperty contained1Property3;
    protected SDOType containedType3;
    protected SDOProperty containedProperty3;
    protected List objects = new ArrayList();
    protected List objects1 = new ArrayList();

    public SDOEqualityHelperEqualTestCases(String name) {
        super(name);
    }

    /**
    * Structure:
    *
    * root
    *    rootproperty1 --> is DataType; SDOString  <---> value: "test"
    *    rootproperty2 --> not DataType            <---> value: containedDataObject
    * root's child:
    *  containedDataObject1
    *      contained1Property1 --> not DataType; bidirectional   <---> value: containedByContainedDataObject
    *  containedDataObject
    *      containedProperty1  --> is DataType; SDOString  <---> value: "test1"
    *      containedProperty2  --> not DataType            <---> value: containedByContainedDataObject
    *      containedProperty_ChangeSummary  --> not DataType  <---> value: chSum(ChangeSummary)
    *  containedDataObject's child:
    *    containedByContainedDataObject
    *        containedByContainedProperty1  --> not DataType; bidirectional   <---> value: containedDataObejct1
    *
    * Covered test cases:
    * Test case 1: copied root's DataType property rootproperty1 has same value
    * Test Case 2: copied root's not DataType property rootproperty2 has a copied DataObject from containedDataObject
    * Test case 3: there exists dataobject's bidircetional not containment property has another DataObjcet as value
    * Test Case 4: there exists dataobject's unidircetional not containment property has another DataObjcet as value
    * Test Case 5: there exists dataobject's not containment property has another DataObjcet that is not in containment tree as value
    * Test Case 6: copied containedProperty's ChangeSummary Type property containedProperty_ChangeSummary has a copied ChangeSummary from source's ChangeSummary chSum
    */
    public void setUp() {
        super.setUp();

        SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);

        // first we set up root data object
        DataObject rootTypeDO = defineType(rootTypeUri, rootTypeName);
        rootType = (SDOType)typeHelper.define(rootTypeDO);
        rootType.setOpen(true);

        rootProperty1 = new SDOProperty(aHelperContext);// root's property1
        rootProperty1.setName("rootproperty1-datatype");
        SDOType rootProperty1_type = SDOConstants.SDO_STRING;// string type
        rootProperty1_type.setDataType(true);// datatype
        rootProperty1.setType(rootProperty1_type);
        rootType.addDeclaredProperty(rootProperty1);// add this property to root type's declared list

        rootProperty2 = new SDOProperty(aHelperContext);//root's property2
        rootProperty2.setName("rootproperty2-notdatatype");
        rootProperty2.setContainment(true);// containment property        
        DataObject rootProperty2_typeDO = defineType("notDataTypeUri", "notDataType");
        SDOType rootProperty2_type = (SDOType)typeHelper.define(rootProperty2_typeDO);

        rootProperty2_type.setDataType(false);// not datatype
        rootProperty2.setType(rootProperty2_type);
        rootType.addDeclaredProperty(rootProperty2);

        rootProperty3 = new SDOProperty(aHelperContext);// root's property3
        rootProperty3.setName("rootproperty3-notdatatype");
        rootProperty3.setContainment(true);// containment property

        DataObject rootProperty3_typeDO = defineType("notDataTypeUri1", "notDataType1");
        SDOType rootProperty3_type = (SDOType)typeHelper.define(rootProperty3_typeDO);

        rootProperty3_type.setDataType(false);// not datatype
        rootProperty3.setType(rootProperty3_type);
        rootType.addDeclaredProperty(rootProperty3);

        rootProperty4 = new SDOProperty(aHelperContext);
        rootProperty4.setName("rootproperty4-list");
        rootProperty4.setContainment(true);

        DataObject rootProperty4_typeDO = defineType("listPropertyUri", "listProperty");
        SDOType rootProperty4_type = (SDOType)typeHelper.define(rootProperty4_typeDO);

        rootProperty4_type.setDataType(false);
        rootProperty4.setType(rootProperty4_type);
        rootType.addDeclaredProperty(rootProperty4);

        DataObject containedTypeDO = defineType(containedTypeUri, containedTypeName);
        containedType = (SDOType)typeHelper.define(containedTypeDO);

        containedProperty1 = new SDOProperty(aHelperContext);// containedDataObject's property1
        containedProperty1.setName("containedProperty1-dataType");
        SDOType containedProperty1_type = SDOConstants.SDO_STRING;// String Type
        containedProperty1_type.setDataType(true);// dataType
        containedProperty1.setType(containedProperty1_type);
        containedType.addDeclaredProperty(containedProperty1);

        containedProperty2 = new SDOProperty(aHelperContext);// containedDataObject's property2
        containedProperty2.setName("containedProperty2-notdataType");
        containedProperty2.setContainment(true);// containment property

        DataObject containedProperty2_typeDO = defineType("containedProperty2Uri", "containedProperty2_notdataType");
        SDOType containedProperty2_type = (SDOType)typeHelper.define(containedProperty2_typeDO);

        containedProperty2_type.setDataType(false);// not datatype
        containedProperty2.setType(containedProperty2_type);
        containedType.addDeclaredProperty(containedProperty2);

        // then, we create one dataobject as root's noncontainment property' value
        containedDataObject1 = new SDODataObject();

        DataObject containedType1DO = defineType(containedType1Uri, containedType1Name);
        containedType1 = (SDOType)typeHelper.define(containedType1DO);

        containedDataObject1._setType(containedType1);

        contained1Property1 = new SDOProperty(aHelperContext);// containedDataObject1's property1
        contained1Property1.setName("contained1Property1-notdataType");
        contained1Property1.setContainment(false);// not containment property

        DataObject contained1Property1_typeDO = defineType("contained1Property1Uri", "contained1Property1_notdataType");
        SDOType contained1Property1_type = (SDOType)typeHelper.define(contained1Property1_typeDO);

        contained1Property1_type.setDataType(false);// not datatype
        contained1Property1.setType(contained1Property1_type);

        // bidirectional to containedByContainedDataObject        
        contained1Property1.setOpposite(containedByContainedProperty1);

        containedType1.addDeclaredProperty(contained1Property1);

        // finally, we create a dataobject contained by containedDataObject
        containedByContainedDataObject = new SDODataObject();

        DataObject containedByContainedTypeDO = defineType(containedBycontainedType1Uri, containedByContainedTypeName);
        containedByContainedType = (SDOType)typeHelper.define(containedByContainedTypeDO);

        containedByContainedDataObject._setType(containedByContainedType);

        containedByContainedProperty1 = new SDOProperty(aHelperContext);// containedByContainedDataObject's property1
        containedByContainedProperty1.setName("containedByContainedProperty1-notdataType");
        containedByContainedProperty1.setContainment(false);// not containment property        

        DataObject containedByContainedProperty1_typeDO = defineType("containedByContainedProperty1Uri", "containedByContainedProperty1_notdataType");
        SDOType containedByContainedProperty1_type = (SDOType)typeHelper.define(containedByContainedProperty1_typeDO);

        containedByContainedProperty1_type.setDataType(false);// not datatype
        containedByContainedProperty1.setType(containedByContainedProperty1_type);

        // bidirectional to containedDataObject1        
        containedByContainedProperty1.setOpposite(contained1Property1);

        containedByContainedType.addDeclaredProperty(containedByContainedProperty1);

        // set opposite property
        contained1Property1.setOpposite(containedByContainedProperty1);

        containedByContainedProperty2 = new SDOProperty(aHelperContext);// containedByContainedDataObject's property1
        containedByContainedProperty2.setName("containedByContainedProperty2-dataType");
        containedByContainedProperty2.setContainment(false);// not containment property

        DataObject containedByContainedProperty2_typeDO = defineType("containedByContainedProperty2Uri", "containedByContainedProperty2_notdataType");
        SDOType containedByContainedProperty2_type = (SDOType)typeHelper.define(containedByContainedProperty2_typeDO);

        containedByContainedProperty2_type.setDataType(false);// not datatype
        containedByContainedProperty2.setType(containedByContainedProperty2_type);
        containedByContainedType.addDeclaredProperty(containedByContainedProperty2);

        containedProperty_ChangeSummary = new SDOProperty(aHelperContext);
        containedProperty_ChangeSummary.setContainment(false);
        //SDOType containedProperty_ChangeSummary_type = new SDOType("containedProperty_ChangeSummaryuri", "containedProperty_ChangeSummary");
        //containedProperty_ChangeSummary_type.setDataType(false);
        containedProperty_ChangeSummary.setType(changeSummaryType);
        containedProperty_ChangeSummary.setName("containedProperty_ChangeSummary");
        containedType.addDeclaredProperty(containedProperty_ChangeSummary);

        root = (SDODataObject)dataFactory.create(rootType);
        root1 = (SDODataObject)dataFactory.create(rootType);

        containedDataObject = (SDODataObject)dataFactory.create(containedType);
        containedDataObject_1 = (SDODataObject)dataFactory.create(containedType);
        // set up relationship
        root.set(rootProperty1, "test");
        root1.set(rootProperty1, "test");
        root.set(rootProperty2, containedDataObject);// child: containedDataObject
        root1.set(rootProperty2, containedDataObject_1);
        //root.set(rootProperty3, containedDataObject1);// child: containedDataObject1
        //root1.set(rootProperty3, containedDataObject1);
        containedDataObject.set(containedProperty1, "test1");
        containedDataObject_1.set(containedProperty1, "test1");

        // enabled 20060914
        //containedDataObject.set(containedProperty2, containedByContainedDataObject);// child: containedByContainedDataObject
        //containedDataObject_1.set(containedProperty2, containedByContainedDataObject);// child: containedByContainedDataObject
        //containedDataObject1.set(contained1Property1, containedByContainedDataObject);// opposite
        //containedByContainedDataObject.set(containedByContainedProperty1, containedDataObject1);// opposite      

        /*
              // UC02xx: setup some bidirectional properties that have the same copy root
              // but are 1 level down from the source root so that copy root != source root
              // new CBCDO2 off off CDO
              containedByContainedType3 = new SDOType(containedBycontainedType3Uri, containedByContainedTypeName3);

              // add property to do
              //containedByContainedType3.addDeclaredProperty(containedByContainedProperty3);
              // 20060913
              //containedType3 = new SDOType(containedType3Uri, containedType3Name);
              // add property to do
              containedByContainedType3.addDeclaredProperty(contained1Property3);

              // new DO
              containedByContainedDataObject3 = (SDODataObject)dataFactory.create(//
                      containedByContainedType3);


              // 2 new opposite properties CBCP2 and C1P2 between CBCDO and CBCDO2
              // set opposite property
        //        containedByContainedDataObject3.set(contained1Property3, containedByContainedDataObject);
              // set opposite property
              containedByContainedDataObject.set(containedByContainedProperty3, containedByContainedDataObject3);


              // child: containedByContainedDataObject
        //        containedDataObject.set(containedProperty3, containedByContainedDataObject3);

              // attach opposite properties between CBCDO and CBCDO2
              // bidirectional to containedDataObject1
              containedByContainedProperty3.setOpposite(contained1Property3);
              // reset opposite set above when property paramenter used to be null
              contained1Property3.setOpposite(containedByContainedProperty3);
        */
    }
}
