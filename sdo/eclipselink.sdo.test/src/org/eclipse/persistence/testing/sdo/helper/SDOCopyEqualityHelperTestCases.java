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
/*
   DESCRIPTION
    Perform copy/equality junit test operations on DataObjects.
    References:
      SDO59-DeepCopy.doc
      SDO_Ref_BiDir_Relationships_DesignSpec.doc
      http://files.oraclecorp.com/content/MySharedFolders/ST%20Functional%20Specs/AS11gR1/TopLink/SDO/SDO_Ref_BiDir_Relationships_DesignSpec.doc

 */
package org.eclipse.persistence.testing.sdo.helper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDOCopyEqualityHelperTestCases extends SDOTestCase {
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

    // UC4
    protected SDODataObject rootUC4;
    protected SDODataObject rootUC4m;
    protected SDOType rootUC4Type;
    protected SDOType rootUC4Typem;
    protected SDOType homeType;
    protected SDOType homeTypem;
    protected SDOType workType;
    protected SDOType workTypem;
    protected SDOType addressType;
    protected SDOType addressTypem;
    protected SDODataObject workObject;
    protected SDODataObject workObjectm;
    protected SDODataObject homeObject;
    protected SDODataObject homeObjectm;
    protected SDODataObject addressObject;
    protected SDODataObject addressObjectm;
    protected SDOProperty homeAddress;
    protected SDOProperty homeAddressm;
    protected SDOProperty rootHome;
    protected SDOProperty rootHomem;
    protected SDOProperty rootWork;
    protected SDOProperty rootWorkm;
    protected SDOProperty workAddress;
    protected SDOProperty workAddressm;
    protected SDOProperty addressWork;
    protected SDOProperty addressWorkm;
    protected String workObjectName = "workObjectName";
    protected String homeObjectName = "homeObjectName";
    protected String addressObjectName = "addressObjectName";
    protected String workObjectUri = "workObjectUri";
    protected String homeObjectUri = "homeObjectUri";
    protected String addressObjectUri = "addressObjectUri";
    protected String workObjectUrim = "workObjectUrim";
    protected String homeObjectUrim = "homeObjectUrim";
    protected String addressObjectUrim = "addressObjectUrim";

    // property strings
    protected String rootUC4TypeName = "rootUC4TypeName";
    protected String rootUC4TypeUri = "rootUC4TypeTypeUri";
    protected String rootUC4TypeUrim = "rootUC4TypeTypeUrim";
    protected String rootHomeName = "rootHome";
    protected String rootWorkName = "rootWork";
    protected String homeAddressName = "homeAddress";
    protected String workAddressName = "workAddress";
    protected String addressWorkName = "addressWork";
    protected String rootHomeUri = "rootHomeUri";
    protected String rootHomeUrim = "rootHomeUrim";
    protected String rootWorkUri = "rootWorkUri";
    protected String rootWorkUrim = "rootWorkUrim";
    protected String homeAddressUri = "homeAddressUri";
    protected String workAddressUri = "workAddressUri";
    protected String addressWorkUri = "addressWorkUri";
    protected String homeAddressUrim = "homeAddressUrim";
    protected String workAddressUrim = "workAddressUrim";
    protected String addressWorkUrim = "addressWorkUrim";

    // UCUniOutside
    protected SDODataObject rootUCUniOutside;
    protected SDOType rootUCUniOutsideType;
    protected SDOType homeTypeUCUniOutside;
    protected SDOType workTypeUCUniOutside;
    protected SDOType addressTypeUCUniOutside;
    protected SDODataObject workObjectUCUniOutside;
    protected SDODataObject homeObjectUCUniOutside;
    protected SDODataObject addressObjectUCUniOutside;
    protected SDOProperty homeAddressUCUniOutside;
    protected SDOProperty rootHomeUCUniOutside;
    protected SDOProperty rootWorkUCUniOutside;
    protected SDOProperty workAddressUCUniOutside;
    protected SDOProperty addressWorkUCUniOutside;
    protected String workObjectNameUCUniOutside = "workObjectNameUCUniOutside";
    protected String homeObjectNameUCUniOutside = "homeObjectNameUCUniOutside";
    protected String addressObjectNameUCUniOutside = "addressObjectNameUCUniOutside";
    protected String workObjectUriUCUniOutside = "workObjectUriUCUniOutside";
    protected String homeObjectUriUCUniOutside = "homeObjectUriUCUniOutside";
    protected String addressObjectUriUCUniOutside = "addressObjectUriUCUniOutside";

    // property strings
    protected String rootUCUniOutsideTypeName = "rootUCUniOutsideTypeName";
    protected String rootUCUniOutsideTypeUri = "rootUCUniOutsideTypeTypeUri";
    protected String rootHomeNameUCUniOutside = "rootHome";
    protected String rootWorkNameUCUniOutside = "rootWork";
    protected String homeAddressNameUCUniOutside = "homeAddress";
    protected String workAddressNameUCUniOutside = "workAddress";
    protected String addressWorkNameUCUniOutside = "addressWork";
    protected String rootHomeUriUCUniOutside = "rootHomeUri";
    protected String rootWorkUriUCUniOutside = "rootWorkUri";
    protected String homeAddressUriUCUniOutside = "homeAddressUri";
    protected String workAddressUriUCUniOutside = "workAddressUri";
    protected String addressWorkUriUCUniOutside = "addressWorkUri";
    protected List objects = new ArrayList();
    protected List objects1 = new ArrayList();

    public SDOCopyEqualityHelperTestCases(String name) {
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
    *      containedProperty3  --> not DataType            <---> value: containedByContainedDataObject3 (bidirectional inside sub-tree copy tree)
    *      containedProperty_ChangeSummary  --> not DataType  <---> value: chSum(ChangeSummary)
    *  containedDataObject's child:
    *    containedByContainedDataObject
    *        containedByContainedProperty1  --> not DataType; bidirectional   <---> value: containedDataObejct1
    *
    * Covered test cases:
    * Test case 1: copied root's DataType property rootproperty1 has same value
    * Test Case 2: copied root's not DataType property rootproperty2 has a copied DataObject from containedDataObject
    * Test case 3: there exists dataobject's bidirectional not containment property has another DataObject as value
    * Test Case 4: there exists dataobject's unidirectional not containment property has another DataObject as value
    * Test Case 5: there exists dataobject's non containment property has another DataObject that is not in containment tree as value
    * Test Case 6: copied containedProperty's ChangeSummary Type property containedProperty_ChangeSummary has a copied ChangeSummary from source's ChangeSummary chSum
    */
    public void setUp() {
        super.setUp();
        SDOType changeSummaryType = (SDOType) aHelperContext.getTypeHelper().getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);

        // first we set up root data object       
        DataObject rootTypeDO = defineType(rootTypeUri, rootTypeName);
        rootType = (SDOType)typeHelper.define(rootTypeDO);

        rootProperty1 = new SDOProperty(aHelperContext);// root's property1
        rootProperty1.setName("rootproperty1-datatype");
        SDOType rootProperty1_type = SDOConstants.SDO_STRING;// string type
        rootProperty1.setType(rootProperty1_type);
        rootType.addDeclaredProperty(rootProperty1);// add this property to root type's declared list

        rootProperty2 = new SDOProperty(aHelperContext);//root's property2
        rootProperty2.setName("rootproperty2-notdatatype");
        rootProperty2.setContainment(true);// containment property
        SDOType rootProperty2_type = new SDOType("notDataTypeUri", "notDataType");
        QName qname = new QName("notDataTypeUri", "notDataType");
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, rootProperty2_type);

        rootProperty2.setType(rootProperty2_type);
        rootType.addDeclaredProperty((Property)rootProperty2);

        rootProperty3 = new SDOProperty(aHelperContext);// root's property3
        rootProperty3.setName("rootproperty3-notdatatype");
        rootProperty3.setContainment(true);// containment property        
        DataObject rootProperty3_typeDO = defineType("notDataTypeUri1", "notDataType1");
        SDOType rootProperty3_type = (SDOType)typeHelper.define(rootProperty3_typeDO);

        rootProperty3.setType(rootProperty3_type);
        rootType.addDeclaredProperty(rootProperty3);

        rootProperty4 = new SDOProperty(aHelperContext);
        rootProperty4.setName("rootproperty4-list");
        rootProperty4.setContainment(true);
        rootProperty4.setMany(true);

        DataObject rootProperty4_typeDO = defineType("listPropertyUri", "listProperty");
        SDOType rootProperty4_type = (SDOType)typeHelper.define(rootProperty4_typeDO);

        rootProperty4.setType(rootProperty4_type);
        rootType.addDeclaredProperty(rootProperty4);

        SDOProperty rootProperty_NotContainment = new SDOProperty(aHelperContext);
        rootProperty_NotContainment.setContainment(false);

        DataObject rootProperty_NotContainment_typeDO = defineType("rootProperty_NotContainmenturi", "rootProperty_NotContainment");
        SDOType rootProperty_NotContainment_type = (SDOType)typeHelper.define(rootProperty_NotContainment_typeDO);
        rootProperty_NotContainment.setType(rootProperty_NotContainment_type);
        rootProperty_NotContainment.setName("rootProperty_NotContainment");
        rootType.addDeclaredProperty(rootProperty_NotContainment);

        // 20060913: opposite property 3 (to test bidirectional property inside subtree copy tree)
        contained1Property3 = new SDOProperty(aHelperContext);// containedDataObject3's property3
        contained1Property3.setName("contained1Property3-notdataType");
        contained1Property3.setContainment(false);// non-containment property

        DataObject contained1Property3_typeDO = defineType("contained1Property3Uri", "contained1Property3_notdataType");
        SDOType contained1Property3_type = (SDOType)typeHelper.define(contained1Property3_typeDO);
        contained1Property3.setType(contained1Property3_type);

        objects = new ArrayList();

        SDOType obj1Type = new SDOType("listelm1", "listelm1");
        qname = new QName("listelm1", "listelm1");
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, obj1Type);
        SDOProperty obj1Property = new SDOProperty(aHelperContext);
        obj1Property.setName("obj1Property");
        SDOType obj1PropertyType = SDOConstants.SDO_STRING;
        obj1Property.setType(obj1PropertyType);
        obj1Property.setContainment(false);
        obj1Type.addDeclaredProperty(obj1Property);
        SDODataObject obj1 = (SDODataObject)dataFactory.create(obj1Type);
        objects.add(obj1);

        obj1.set(obj1Property, "test");

        // second, we create another dataobject contained by root
        DataObject containedTypeDO = defineType(containedTypeUri, containedTypeName);
        containedType = (SDOType)typeHelper.define(containedTypeDO);

        containedProperty1 = new SDOProperty(aHelperContext);// containedDataObject's property1
        containedProperty1.setName("containedProperty1-dataType");
        SDOType containedProperty1_type = SDOConstants.SDO_STRING;// String Type
        containedProperty1.setType(containedProperty1_type);
        containedType.addDeclaredProperty(containedProperty1);

        containedProperty2 = new SDOProperty(aHelperContext);// containedDataObject's property2
        containedProperty2.setName("containedProperty2-notdataType");
        containedProperty2.setContainment(true);// containment property
        SDOType containedProperty2_type = new SDOType(//
        "containedProperty2Uri", "containedProperty2_notdataType");
        qname = new QName("containedProperty2Uri", "containedProperty2_notdataType");
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, containedProperty2_type);
        containedProperty2_type.setDataType(false);// not datatype
        containedProperty2.setType(containedProperty2_type);
        containedType.addDeclaredProperty(containedProperty2);

        // 20060914 new bidirectional hosting node
        containedProperty3 = new SDOProperty(aHelperContext);// containedDataObject's property2
        containedProperty3.setName("containedProperty3-notdataType");
        containedProperty3.setContainment(true);// containment property
        SDOType containedProperty3_type = new SDOType(//
        "containedProperty3Uri", "containedProperty3_notdataType");
        qname = new QName("containedProperty3Uri", "containedProperty3_notdataType");
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, containedProperty3_type);
        containedProperty3_type.setDataType(false);// not datatype
        containedProperty3.setType(containedProperty3_type);
        containedType.addDeclaredProperty(containedProperty3);

        containedProperty_ChangeSummary = new SDOProperty(aHelperContext);
        containedProperty_ChangeSummary.setContainment(false);
        containedProperty_ChangeSummary.setType(changeSummaryType);
        containedProperty_ChangeSummary.setName("containedProperty_ChangeSummary");
        containedType.addDeclaredProperty(containedProperty_ChangeSummary);

        containedDataObject = (SDODataObject)dataFactory.create(containedType);

        // then, we create one dataobject as root's non containment property' value        
        containedType1 = new SDOType(containedType1Uri, containedType1Name);
        qname = new QName(containedType1Uri, containedType1Name);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, containedType1);

        contained1Property1 = new SDOProperty(aHelperContext);// containedDataObject1's property1
        contained1Property1.setName("contained1Property1-notdataType");
        contained1Property1.setContainment(false);// non containment property
        SDOType contained1Property1_type = new SDOType(//
        "contained1Property1Uri", "contained1Property1_notdataType");
        qname = new QName("contained1Property1Uri", "contained1Property1_notdataType");
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, contained1Property1_type);
        contained1Property1_type.setDataType(false);// not datatype
        contained1Property1.setType(contained1Property1_type);
        // TODO: 20060906 bidirectional
        // bidirectional to containedByContainedDataObject        
        // Note: the property parameter is null so this set will have no effect until the 2nd set later
        contained1Property1.setOpposite(containedByContainedProperty1);

        containedType1.addDeclaredProperty(contained1Property1);

        containedDataObject1 = (SDODataObject)dataFactory.create(containedType1);
        // finally, we create a dataobject contained by containedDataObject        
        containedByContainedType = new SDOType(containedBycontainedType1Uri, containedByContainedTypeName);

        qname = new QName(containedBycontainedType1Uri, containedByContainedTypeName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, containedByContainedType);
        containedByContainedProperty1 = new SDOProperty(aHelperContext);// containedByContainedDataObject's property1
        containedByContainedProperty1.setName("containedByContainedProperty1-notdataType");
        containedByContainedProperty1.setContainment(false);// non containment property
        SDOType containedByContainedProperty1_type = new SDOType(//
        "containedByContainedProperty1Uri", "containedByContainedProperty1_notdataType");
        qname = new QName("containedByContainedProperty1Uri", "containedByContainedProperty1_notdataType");
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, containedByContainedProperty1_type);
        containedByContainedProperty1_type.setDataType(false);// not datatype
        containedByContainedProperty1.setType(containedByContainedProperty1_type);
        // TODO: 20060906 bidirectional
        // bidirectional to containedDataObject1
        containedByContainedProperty1.setOpposite(contained1Property1);
        containedByContainedType.addDeclaredProperty(containedByContainedProperty1);
        // reset opposite set above when property paramenter used to be null
        contained1Property1.setOpposite(containedByContainedProperty1);

        containedByContainedProperty2 = new SDOProperty(aHelperContext);// containedByContainedDataObject's property1
        containedByContainedProperty2.setName("containedByContainedProperty2-dataType");
        containedByContainedProperty2.setContainment(false);// non containment property
        SDOType containedByContainedProperty2_type = new SDOType(//
        "containedByContainedProperty2Uri", "containedByContainedProperty2_notdataType");
        qname = new QName("containedByContainedProperty2Uri", "containedByContainedProperty2_notdataType");
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, containedByContainedProperty2_type);
        containedByContainedProperty2_type.setDataType(false);// not datatype
        containedByContainedProperty2.setType(containedByContainedProperty2_type);
        containedByContainedType.addDeclaredProperty(containedByContainedProperty2);

        // opposite property 3
        containedByContainedProperty3 = new SDOProperty(aHelperContext);// containedByContainedDataObject2's property3
        containedByContainedProperty3.setName("containedByContainedProperty3-notdataType");
        containedByContainedProperty3.setContainment(false);// non-containment property
        SDOType containedByContainedProperty3_type = new SDOType(//
        "containedByContainedProperty3Uri", "containedByContainedProperty3_notdataType");
        qname = new QName("containedByContainedProperty3Uri", "containedByContainedProperty3_notdataType");
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, containedByContainedProperty3_type);
        containedByContainedProperty3_type.setDataType(false);// non-datatype
        containedByContainedProperty3.setType(containedByContainedProperty3_type);
        containedByContainedType.addDeclaredProperty(containedByContainedProperty3);

        containedByContainedDataObject = (SDODataObject)dataFactory.create(//
            containedByContainedType);

        // set up relationship
        root = (SDODataObject)dataFactory.create(rootType);
        root.set(rootProperty1, "test");
        root.set(rootProperty2, containedDataObject);// child: containedDataObject
        root.set(rootProperty3, containedDataObject1);// child: containedDataObject1
        root.set(rootProperty4, objects);
        containedDataObject.set(containedProperty1, "test1");
        // child: containedByContainedDataObject        
        containedDataObject.set(containedProperty2, containedByContainedDataObject);
        // set opposite property
        containedDataObject1.set(contained1Property1, containedByContainedDataObject);
        // set opposite property
        containedByContainedDataObject.set(containedByContainedProperty1, containedDataObject1);

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
        // UC 4: mixed containment=true/containment=false bidirectional opposite set
        // (work at home relationship)        
        // root -> home -> address
        // root -> work -> address
        // home/address -> noncontainment to -> work
        // work -> containment to -> home/address
        rootUC4Type = new SDOType(rootUC4TypeUri, rootUC4TypeName);// root's type        
        qname = new QName(rootUC4TypeUri, rootUC4TypeName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, rootUC4Type);

        homeType = new SDOType(homeObjectUri, homeObjectName);
        qname = new QName(homeObjectUri, homeObjectName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, homeType);

        addressType = new SDOType(addressObjectUri, addressObjectName);
        qname = new QName(addressObjectUri, addressObjectName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, addressType);

        workType = new SDOType(workObjectUri, workObjectName);
        qname = new QName(workObjectUri, workObjectName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, workType);

        rootHome = new SDOProperty(aHelperContext);
        rootHome.setName(rootHomeName);
        rootHome.setContainment(true);// containment property
        SDOType rootHome_type = new SDOType(rootHomeUri, rootHomeName);
        qname = new QName(rootHomeUri, rootHomeName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, rootHome_type);

        rootHome_type.setDataType(false);// not datatype
        rootHome.setType(rootHome_type);
        rootUC4Type.addDeclaredProperty((Property)rootHome);

        rootWork = new SDOProperty(aHelperContext);
        rootWork.setName(rootWorkName);
        // non containment so that a containment opposite can be set to this node
        rootWork.setContainment(false);//true);// containment property
        //rootWork.setContainment(true);// containment property        
        SDOType rootWork_type = new SDOType(rootWorkUri, rootWorkName);
        qname = new QName(rootWorkUri, rootWorkName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, rootWork_type);
        rootWork_type.setDataType(false);// not datatype
        rootWork.setType(rootWork_type);
        rootUC4Type.addDeclaredProperty((Property)rootWork);

        homeAddress = new SDOProperty(aHelperContext);
        homeAddress.setName(homeAddressName);
        homeAddress.setContainment(true);// containment property
        SDOType homeAddress_type = new SDOType(homeAddressUri, homeAddressName);
        qname = new QName(homeAddressUri, homeAddressName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, homeAddress_type);
        homeAddress_type.setDataType(false);// not datatype
        homeAddress.setType(homeAddress_type);
        homeType.addDeclaredProperty((Property)homeAddress);

        workAddress = new SDOProperty(aHelperContext);
        workAddress.setName(workAddressName);
        // one opposite property can be true
        workAddress.setContainment(false);//true);// containment property
        //workAddress.setContainment(true);// containment property        
        SDOType workAddress_type = new SDOType(workAddressUri, workAddressName);
        qname = new QName(workAddressUri, workAddressName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, workAddress_type);
        workAddress_type.setDataType(false);// not datatype
        workAddress.setType(workAddress_type);
        workType.addDeclaredProperty((Property)workAddress);

        addressWork = new SDOProperty(aHelperContext);
        addressWork.setName(addressWorkName);
        // other opposite property must be false
        addressWork.setContainment(true);// containment property
        SDOType addressWork_type = new SDOType(addressWorkUri, addressWorkName);
        qname = new QName(addressWorkUri, addressWorkName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, addressWork_type);
        addressWork_type.setDataType(false);// not datatype
        addressWork.setType(addressWork_type);
        addressType.addDeclaredProperty((Property)addressWork);

        rootUC4 = (SDODataObject)dataFactory.create(rootUC4Type);
        addressObject = (SDODataObject)dataFactory.create(addressType);
        homeObject = (SDODataObject)dataFactory.create(homeType);
        workObject = (SDODataObject)dataFactory.create(workType);
        addressWork.setOpposite(workAddress);
        workAddress.setOpposite(addressWork);

        rootUC4.set(rootHome, homeObject);
        rootUC4.set(rootWork, workObject);
        // child:         
        homeObject.set(homeAddress, addressObject);
        // set opposite property
        addressObject.set(addressWork, workObject);
        // set opposite property
        workObject.set(workAddress, addressObject);

        // many case
        // UC 4: mixed containment=true/containment=false bidirectional opposite set
        // (work at home relationship)        
        // root -> home -> address
        // root -> work -> address
        // home/address -> noncontainment to -> work
        // work -> containment to -> home/address
        rootUC4Typem = new SDOType(rootUC4TypeUrim, rootUC4TypeName);// root's type        
        qname = new QName(rootUC4TypeUrim, rootUC4TypeName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, rootUC4Typem);

        homeTypem = new SDOType(homeObjectUrim, homeObjectName);
        qname = new QName(homeObjectUrim, homeObjectName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, homeTypem);

        addressTypem = new SDOType(addressObjectUrim, addressObjectName);
        qname = new QName(addressObjectUrim, addressObjectName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, addressTypem);

        workTypem = new SDOType(workObjectUrim, workObjectName);
        qname = new QName(workObjectUrim, workObjectName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, workTypem);

        rootHomem = new SDOProperty(aHelperContext);
        rootHomem.setName(rootHomeName);
        rootHomem.setContainment(true);// containment property
        SDOType rootHome_typem = new SDOType(rootHomeUrim, rootHomeName);
        qname = new QName(rootHomeUrim, rootHomeName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, rootHome_typem);
        rootHome_typem.setDataType(false);// not datatype
        rootHomem.setType(rootHome_typem);
        rootUC4Typem.addDeclaredProperty((Property)rootHomem);

        rootWorkm = new SDOProperty(aHelperContext);
        rootWorkm.setName(rootWorkName);
        // non containment so that a containment opposite can be set to this node
        rootWorkm.setContainment(false);//true);// containment property
        rootWorkm.setMany(true);
        SDOType rootWork_typem = new SDOType(rootWorkUrim, rootWorkName);
        qname = new QName(rootWorkUrim, rootWorkName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, rootWork_typem);
        rootWork_typem.setDataType(false);// not datatype
        rootWorkm.setType(rootWork_typem);
        rootUC4Typem.addDeclaredProperty((Property)rootWorkm);

        homeAddressm = new SDOProperty(aHelperContext);
        homeAddressm.setName(homeAddressName);
        homeAddressm.setContainment(true);// containment property
        SDOType homeAddress_typem = new SDOType(homeAddressUrim, homeAddressName);
        qname = new QName(homeAddressUrim, homeAddressName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, homeAddress_typem);
        homeAddress_typem.setDataType(false);// not datatype
        homeAddressm.setType(homeAddress_typem);
        homeTypem.addDeclaredProperty((Property)homeAddressm);

        workAddressm = new SDOProperty(aHelperContext);
        workAddressm.setName(workAddressName);
        // one opposite property can be true
        workAddressm.setContainment(false);//true);// containment property
        //workAddress.setContainment(true);// containment property        
        SDOType workAddress_typem = new SDOType(workAddressUri, workAddressName);
        qname = new QName(workAddressUri, workAddressName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, workAddress_typem);
        workAddress_typem.setDataType(false);// not datatype
        workAddressm.setType(workAddress_typem);
        workTypem.addDeclaredProperty((Property)workAddressm);

        addressWorkm = new SDOProperty(aHelperContext);
        addressWorkm.setName(addressWorkName);
        // other opposite property must be false
        addressWorkm.setContainment(false);// containment property
        addressWorkm.setMany(true);
        SDOType addressWork_typem = new SDOType(addressWorkUri, addressWorkName);
        qname = new QName(addressWorkUri, addressWorkName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, addressWork_typem);
        addressWork_typem.setDataType(false);// not datatype
        addressWorkm.setType(addressWork_typem);
        addressTypem.addDeclaredProperty((Property)addressWorkm);

        rootUC4m = (SDODataObject)dataFactory.create(rootUC4Typem);
        addressObjectm = (SDODataObject)dataFactory.create(addressTypem);
        homeObjectm = (SDODataObject)dataFactory.create(homeTypem);
        workObjectm = (SDODataObject)dataFactory.create(workTypem);
        addressWorkm.setOpposite(workAddressm);
        workAddressm.setOpposite(addressWorkm);

        rootUC4m.set(rootHomem, homeObjectm);
        ArrayList workList = new ArrayList();
        workList.add(workObjectm);
        rootUC4m.set(rootWorkm, workList);
        //rootUC4m.set(rootWorkm, workObjectm);
        // child:         
        homeObjectm.set(homeAddressm, addressObjectm);
        // set opposite property
        addressObjectm.set(addressWorkm, workList);//workObjectm);
        // set opposite property
        workObjectm.set(workAddressm, addressObjectm);

        // UC 1b: unidirectional outside the copytree
        // (work at home relationship)        
        // root -> home -> address
        // root -> work -> address
        // home/address -> noncontainment to -> work
        // 
        rootUCUniOutsideType = new SDOType(rootUCUniOutsideTypeUri, rootUCUniOutsideTypeName);// root's type        
        qname = new QName(rootUCUniOutsideTypeUri, rootUCUniOutsideTypeName);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, rootUCUniOutsideType);

        homeTypeUCUniOutside = new SDOType(homeObjectUriUCUniOutside, homeObjectNameUCUniOutside);
        qname = new QName(homeObjectUriUCUniOutside, homeObjectNameUCUniOutside);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, homeTypeUCUniOutside);

        addressTypeUCUniOutside = new SDOType(addressObjectUriUCUniOutside, addressObjectNameUCUniOutside);
        qname = new QName(addressObjectUriUCUniOutside, addressObjectNameUCUniOutside);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, addressTypeUCUniOutside);

        workTypeUCUniOutside = new SDOType(workObjectUriUCUniOutside, workObjectNameUCUniOutside);
        qname = new QName(workObjectUriUCUniOutside, workObjectNameUCUniOutside);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, workTypeUCUniOutside);

        rootHomeUCUniOutside = new SDOProperty(aHelperContext);
        rootHomeUCUniOutside.setName(rootHomeNameUCUniOutside);
        rootHomeUCUniOutside.setContainment(true);// containment property
        SDOType rootHome_typeUCUniOutside = new SDOType(rootHomeUriUCUniOutside, rootHomeNameUCUniOutside);
        qname = new QName(rootHomeUriUCUniOutside, rootHomeNameUCUniOutside);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, rootHome_typeUCUniOutside);
        rootHome_typeUCUniOutside.setDataType(false);// not datatype
        rootHomeUCUniOutside.setType(rootHome_typeUCUniOutside);
        rootUCUniOutsideType.addDeclaredProperty((Property)rootHomeUCUniOutside);

        rootWorkUCUniOutside = new SDOProperty(aHelperContext);
        rootWorkUCUniOutside.setName(rootWorkNameUCUniOutside);
        // non containment so that a containment opposite can be set to this node
        rootWorkUCUniOutside.setContainment(false);//true);// containment property
        SDOType rootWork_typeUCUniOutside = new SDOType(rootWorkUriUCUniOutside, rootWorkNameUCUniOutside);
        qname = new QName(rootWorkUriUCUniOutside, rootWorkNameUCUniOutside);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, rootWork_typeUCUniOutside);

        rootWork_typeUCUniOutside.setDataType(false);// not datatype
        rootWorkUCUniOutside.setType(rootWork_typeUCUniOutside);
        rootUCUniOutsideType.addDeclaredProperty((Property)rootWorkUCUniOutside);

        homeAddressUCUniOutside = new SDOProperty(aHelperContext);
        homeAddressUCUniOutside.setName(homeAddressNameUCUniOutside);
        homeAddressUCUniOutside.setContainment(true);// containment property
        SDOType homeAddress_typeUCUniOutside = new SDOType(homeAddressUriUCUniOutside, homeAddressNameUCUniOutside);
        qname = new QName(homeAddressUriUCUniOutside, homeAddressNameUCUniOutside);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, homeAddress_typeUCUniOutside);
        homeAddress_typeUCUniOutside.setDataType(false);// not datatype
        homeAddressUCUniOutside.setType(homeAddress_typeUCUniOutside);
        homeTypeUCUniOutside.addDeclaredProperty((Property)homeAddressUCUniOutside);

        /*
                workAddressUCUniOutside = new SDOProperty(aHelperContext);
                workAddressUCUniOutside.setName(workAddressNameUCUniOutside);
                // one opposite property can be true
                workAddressUCUniOutside.setContainment(false);//true);// containment property
                SDOType workAddress_typeUCUniOutside = new SDOType(workAddressUriUCUniOutside, workAddressNameUCUniOutside);
                workAddress_typeUCUniOutside.setDataType(false);// not datatype
                workAddressUCUniOutside.setType(workAddress_typeUCUniOutside);
                workTypeUCUniOutside.addDeclaredProperty((Property)workAddressUCUniOutside);
        */
        addressWorkUCUniOutside = new SDOProperty(aHelperContext);
        addressWorkUCUniOutside.setName(addressWorkNameUCUniOutside);
        // other opposite property must be false
        addressWorkUCUniOutside.setContainment(true);//false);// containment property
        SDOType addressWork_typeUCUniOutside = new SDOType(addressWorkUriUCUniOutside, addressWorkNameUCUniOutside);
        qname = new QName(addressWorkUriUCUniOutside, addressWorkNameUCUniOutside);
        ((SDOTypeHelper)typeHelper).getTypesHashMap().put(qname, addressWork_typeUCUniOutside);

        addressWork_typeUCUniOutside.setDataType(false);// not datatype
        addressWorkUCUniOutside.setType(addressWork_typeUCUniOutside);
        addressTypeUCUniOutside.addDeclaredProperty((Property)addressWorkUCUniOutside);

        rootUCUniOutside = (SDODataObject)dataFactory.create(rootUCUniOutsideType);
        addressObjectUCUniOutside = (SDODataObject)dataFactory.create(addressTypeUCUniOutside);
        homeObjectUCUniOutside = (SDODataObject)dataFactory.create(homeTypeUCUniOutside);
        workObjectUCUniOutside = (SDODataObject)dataFactory.create(workTypeUCUniOutside);
        //addressWorkUCUniOutside.setOpposite(workAddressUCUniOutside);
        //workAddressUCUniOutside.setOpposite(addressWorkUCUniOutside);
        rootUCUniOutside.set(rootHomeUCUniOutside, homeObjectUCUniOutside);
        rootUCUniOutside.set(rootWorkUCUniOutside, workObjectUCUniOutside);
        // child:         
        homeObjectUCUniOutside.set(homeAddressUCUniOutside, addressObjectUCUniOutside);
        // set opposite property
        addressObjectUCUniOutside.set(addressWorkUCUniOutside, workObjectUCUniOutside);

        // set opposite property
        //workObjectUCUniOutside.set(workAddressUCUniOutside, addressObjectUCUniOutside);
        containedDataObject.getChangeSummary().beginLogging();
    }
}
