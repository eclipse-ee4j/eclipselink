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
/*
   DESCRIPTION
    Perform copy junit test operations on DataObjects.
    References:
      SDO59-DeepCopy.doc
      SDO_Ref_BiDir_Relationships_DesignSpec.doc
      http://files.oraclecorp.com/content/MySharedFolders/ST%20Functional%20Specs/AS11gR1/TopLink/SDO/SDO_Ref_BiDir_Relationships_DesignSpec.doc

   MODIFIED    (MM/DD/YY)
    mfobrien    02/12/07 - 
    dmahar      11/23/06 -
    mfobrien    09/12/06 - Add bidirectional property copy support
 */
package org.eclipse.persistence.testing.sdo.helper.copyhelper;

import commonj.sdo.Property;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOCopyHelper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDOCopyHelperTestCases extends SDOTestCase {
    protected SDOCopyHelper copyHelper;
    protected String rootTypeName = "rootTypeName";
    protected String rootTypeUri = "rootTypeTypeUri";
    protected String containedTypeName = "containedTypeName";
    protected String containedTypeUri = "containedTypeTypeUri";
    protected String containedType1Name = "containedType1Name";
    protected String containedType1Uri = "containedType1TypeUri";
    protected String containedByContainedTypeName = "containedByContainedTypeName";
    protected String containedBycontainedType1Uri = "containedBycontainedTypeTypeUri";
    protected SDODataObject root;
    protected SDODataObject containedDataObject;
    protected SDODataObject containedDataObject1;
    protected SDODataObject containedByContainedDataObject;
    protected SDOType rootType;
    protected SDOType containedType;
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
    protected List objects;

    public SDOCopyHelperTestCases(String name) {
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
     *        containedByContainedProperty1  --> not DataType; bidirectional   <---> value: containedDataObject1
     *
     * Covered test cases:
     * Test Case 1: copied root's DataType property rootproperty1 has same value
     * Test Case 2: copied root's not DataType property rootproperty2 has a copied DataObject from containedDataObject
     * Test Case 3: there exists dataobject's bidirectional not containment property has another DataObject as value
     * Test Case 4: there exists dataobject's unidirectional not containment property has another DataObject as value
     * Test Case 5: there exists dataobject's not containment property has another DataObject
     *   that is not in containment tree as value
     * Test Case 6: copied containedProperty's ChangeSummary Type property containedProperty_ChangeSummary
     *   has a copied ChangeSummary from source's ChangeSummary chSum
     */
    public void setUp() {
        super.setUp();

        SDOType changeSummaryType = (SDOType) aHelperContext.getTypeHelper().getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);

        // first we set up root data object       
        rootType = new SDOType(rootTypeUri, rootTypeName);// root's type        

        rootProperty1 = new SDOProperty(aHelperContext);// root's property1
        rootProperty1.setName("rootproperty1-datatype");
        SDOType rootProperty1_type = SDOConstants.SDO_STRING;// string type
        rootProperty1_type.setDataType(true);// datatype
        rootProperty1.setType(rootProperty1_type);
        rootType.addDeclaredProperty((Property)rootProperty1);// add this property to root type's declared list

        rootProperty2 = new SDOProperty(aHelperContext);//root's property2
        rootProperty2.setName("rootproperty2-notdatatype");
        rootProperty2.setContainment(true);// containment property
        SDOType rootProperty2_type = new SDOType("notDataTypeUri", "notDataType");
        rootProperty2_type.setDataType(false);// not datatype
        rootProperty2.setType(rootProperty2_type);
        rootType.addDeclaredProperty((Property)rootProperty2);

        rootProperty3 = new SDOProperty(aHelperContext);// root's property3
        rootProperty3.setName("rootproperty3-notdatatype");
        rootProperty3.setContainment(true);// containment property
        SDOType rootProperty3_type = new SDOType("notDataTypeUri1", "notDataType1");
        rootProperty3_type.setDataType(false);// not datatype
        rootProperty3.setType(rootProperty3_type);
        rootType.addDeclaredProperty((Property)rootProperty3);

        rootProperty4 = new SDOProperty(aHelperContext);
        rootProperty4.setName("rootproperty4-list");
        rootProperty4.setContainment(true);
        rootProperty4.setMany(true);
        SDOType rootProperty4_type = new SDOType("listPropertyUri", "listProperty");
        rootProperty4_type.setDataType(false);
        rootProperty4.setType(rootProperty4_type);
        rootType.addDeclaredProperty((Property)rootProperty4);

        SDOProperty rootProperty_NotContainment = new SDOProperty(aHelperContext);
        rootProperty_NotContainment.setContainment(false);
        SDOType rootProperty_NotContainment_type = new SDOType(//
        "rootProperty_NotContainmenturi", "rootProperty_NotContainment");
        rootProperty_NotContainment_type.setDataType(false);
        rootProperty_NotContainment.setType(rootProperty_NotContainment_type);
        rootProperty_NotContainment.setName("rootProperty_NotContainment");
        rootType.addDeclaredProperty(rootProperty_NotContainment);

        // 20060913: opposite property 3
        contained1Property3 = new SDOProperty(aHelperContext);// containedDataObject3's property3
        contained1Property3.setName("contained1Property3-notdataType");
        contained1Property3.setContainment(false);// non-containment property
        SDOType contained1Property3_type = new SDOType(//
        "contained1Property3Uri", "contained1Property3_notdataType");
        contained1Property3_type.setDataType(false);// non-datatype
        contained1Property3.setType(contained1Property3_type);

        objects = new ArrayList();

        SDOType obj1Type = new SDOType("listelm1", "listelm1");

        SDOProperty obj1Property = new SDOProperty(aHelperContext);
        obj1Property.setName("obj1Property");
        SDOType obj1PropertyType = SDOConstants.SDO_STRING;
        obj1PropertyType.setDataType(true);
        obj1Property.setType(obj1PropertyType);
        obj1Property.setContainment(false);
        obj1Type.addDeclaredProperty(obj1Property);
        SDODataObject obj1 = (SDODataObject)dataFactory.create(obj1Type);
        objects.add(obj1);

        obj1.set(obj1Property, "test");

        // second, we create another dataobject contained by root
        containedType = new SDOType(containedTypeUri, containedTypeName);

        containedProperty1 = new SDOProperty(aHelperContext);// containedDataObject's property1
        containedProperty1.setName("containedProperty1-dataType");
        SDOType containedProperty1_type = SDOConstants.SDO_STRING;// String Type
        containedProperty1_type.setDataType(true);// dataType
        containedProperty1.setType(containedProperty1_type);
        containedType.addDeclaredProperty(containedProperty1);

        containedProperty2 = new SDOProperty(aHelperContext);// containedDataObject's property2
        containedProperty2.setName("containedProperty2-notdataType");
        containedProperty2.setContainment(true);// containment property
        SDOType containedProperty2_type = new SDOType(//
        "containedProperty2Uri", "containedProperty2_notdataType");
        containedProperty2_type.setDataType(false);// not datatype
        containedProperty2.setType(containedProperty2_type);
        containedType.addDeclaredProperty(containedProperty2);

        // 20060914 new bidirectional hosting node
        containedProperty3 = new SDOProperty(aHelperContext);// containedDataObject's property2
        containedProperty3.setName("containedProperty3-notdataType");
        containedProperty3.setContainment(true);// containment property
        SDOType containedProperty3_type = new SDOType(//
        "containedProperty3Uri", "containedProperty3_notdataType");
        containedProperty3_type.setDataType(false);// not datatype
        containedProperty3.setType(containedProperty3_type);
        containedType.addDeclaredProperty(containedProperty3);

        containedProperty_ChangeSummary = new SDOProperty(aHelperContext);
        containedProperty_ChangeSummary.setContainment(false);
        containedProperty_ChangeSummary.setType(changeSummaryType);
        containedProperty_ChangeSummary.setName("containedProperty_ChangeSummary");
        containedType.addDeclaredProperty((Property)containedProperty_ChangeSummary);

        containedDataObject = (SDODataObject)dataFactory.create(containedType);
        containedDataObject.getChangeSummary().beginLogging();

        // then, we create one dataobject as root's noncontainment property' value        
        containedType1 = new SDOType(containedType1Uri, containedType1Name);

        contained1Property1 = new SDOProperty(aHelperContext);// containedDataObject1's property1
        contained1Property1.setName("contained1Property1-notdataType");
        contained1Property1.setContainment(false);// not containment property
        SDOType contained1Property1_type = new SDOType(//
        "contained1Property1Uri", "contained1Property1_notdataType");
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

        containedByContainedProperty1 = new SDOProperty(aHelperContext);// containedByContainedDataObject's property1
        containedByContainedProperty1.setName("containedByContainedProperty1-notdataType");
        containedByContainedProperty1.setContainment(false);// not containment property
        SDOType containedByContainedProperty1_type = new SDOType(//
        "containedByContainedProperty1Uri", "containedByContainedProperty1_notdataType");
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
        containedByContainedProperty2.setContainment(false);// not containment property
        SDOType containedByContainedProperty2_type = new SDOType(//
        "containedByContainedProperty2Uri", "containedByContainedProperty2_notdataType");
        containedByContainedProperty2_type.setDataType(false);// not datatype
        containedByContainedProperty2.setType(containedByContainedProperty2_type);
        containedByContainedType.addDeclaredProperty(containedByContainedProperty2);

        // opposite property 3
        /*        containedByContainedProperty3 = new SDOProperty(aHelperContext);// containedByContainedDataObject2's property3
                containedByContainedProperty3.setName("containedByContainedProperty3-notdataType");
                containedByContainedProperty3.setContainment(false);// non-containment property
                SDOType containedByContainedProperty3_type = new SDOType(//
                        "containedByContainedProperty3Uri", "containedByContainedProperty3_notdataType");
                containedByContainedProperty3_type.setDataType(false);// non-datatype
                containedByContainedProperty3.setType(containedByContainedProperty3_type);
        //        containedByContainedType.addDeclaredProperty(containedByContainedProperty3);
        */
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
              containedByContainedDataObject3 = (SDODataObject)aHelperContext.getDataFactory().create(//
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

    /**
     * DataObject heiarchy
     * Eclipse debug output of the [root] object at the end of the setUp() function above
     *
     * Abridged and Full versions
     *
    root    SDODataObject  (id=79)
    instanceProperties    ArrayList<E>  (id=87)
        elementData    Object[10]  (id=105)
            [0]    SDOProperty  (id=80)
                containingType    null
                global    false
                hasMany    false
                isContainment    false
                opposite    null
                propertyName    "rootproperty1-datatype"
                type    SDOType  (id=22)
                    typeName    "String"
            [1]    SDOProperty  (id=81)
                containingType    null
                global    false
                hasMany    false
                isContainment    true
                opposite    null
                propertyName    "rootproperty2-notdatatype"
                type    SDOType  (id=31)
                    typeName    "notDataType"
            [2]    SDOProperty  (id=82)
                containingType    null
                global    false
                hasMany    false
                isContainment    true
                opposite    null
                propertyName    "rootproperty3-notdatatype"
                type    SDOType  (id=32)
                    typeName    "notDataType1"
            [3]    SDOProperty  (id=83)
                containingType    null
                global    false
                hasMany    true
                isContainment    true
                opposite    null
                propertyName    "rootproperty4-list"
                type    SDOType  (id=33)
                    typeName    "listProperty"
            [4]    SDOProperty  (id=34)
                containingType    null
                global    false
                hasMany    false
                isContainment    false
                propertyName    "rootProperty_NotContainment"
                type    SDOType  (id=37)
                    typeName    "rootProperty_NotContainment"
    properties    DefaultValueStore  (id=89)
        typePropertiesIsSetStatus    boolean[5]  (id=99)
            [0]    true
            [1]    true
            [2]    true
            [3]    true
            [4]    false
        typePropertyValues    Object[5]  (id=101)
            [0]    "test"
            [1]    SDODataObject  (id=56)
            [2]    SDODataObject  (id=57)
            [3]    ListWrapper  (id=140)
            [4]    null
    type    SDOType  (id=84)
        aliasNames    null
        allProperties    ArrayList<E>  (id=144)
        allPropertiesArr    Property[5]  (id=145)
        baseTypes    ArrayList<E>  (id=147)
        changeSummaryProperty    null
        declaredProperties    ArrayList<E>  (id=148)
        declaredPropertiesMap    HashMap<K,V>  (id=149)
        IDProp    null
        instancePropertiesMap    null
        isAbstract    false
        isDataType    false
        isSequenced    false
        open    false
        propertyValues    HashMap<K,V>  (id=150)
        typeName    "rootTypeName"
        typeUri    "rootTypeTypeUri"
        xmlDescriptor    null
        xsd    false
        xsdList    false
        xsdLocalName    null
     *
     *
    root    SDODataObject  (id=79)
    changeSummary    null
    container    null
    containmentPropertyName    null
    instanceProperties    ArrayList<E>  (id=87)
        elementData    Object[10]  (id=105)
            [0]    SDOProperty  (id=80)
                aliasNames    ArrayList<E>  (id=106)
                attribute    false
                containingType    null
                defaultValue    null
                global    false
                hasMany    false
                indexInType    0
                instancePropertiesMap    null
                isContainment    false
                namespaceQualified    false
                opposite    null
                propertyName    "rootproperty1-datatype"
                propertyValues    null
                readOnly    false
                type    SDOType  (id=22)
                    aliasNames    null
                    allProperties    null
                    allPropertiesArr    null
                    baseTypes    null
                    changeSummaryProperty    null
                    declaredProperties    null
                    declaredPropertiesMap    null
                    IDProp    null
                    instancePropertiesMap    HashMap<K,V>  (id=110)
                    isAbstract    false
                    isDataType    true
                    isSequenced    false
                    open    false
                    propertyValues    HashMap<K,V>  (id=111)
                    typeName    "String"
                    typeUri    "commonj.sdo"
                    xmlDescriptor    null
                    xsd    false
                    xsdList    false
                    xsdLocalName    null
                xmlMapping    null
                xsd    false
                xsdLocalName    null
            [1]    SDOProperty  (id=81)
                aliasNames    ArrayList<E>  (id=116)
                attribute    false
                containingType    null
                defaultValue    null
                global    false
                hasMany    false
                indexInType    0
                instancePropertiesMap    null
                isContainment    true
                namespaceQualified    false
                opposite    null
                propertyName    "rootproperty2-notdatatype"
                propertyValues    null
                readOnly    false
                type    SDOType  (id=31)
                    aliasNames    null
                    allProperties    null
                    allPropertiesArr    null
                    baseTypes    null
                    changeSummaryProperty    null
                    declaredProperties    null
                    declaredPropertiesMap    null
                    IDProp    null
                    instancePropertiesMap    null
                    isAbstract    false
                    isDataType    false
                    isSequenced    false
                    open    false
                    propertyValues    null
                    typeName    "notDataType"
                    typeUri    "notDataTypeUri"
                    xmlDescriptor    null
                    xsd    false
                    xsdList    false
                    xsdLocalName    null
                xmlMapping    null
                xsd    false
                xsdLocalName    null
            [2]    SDOProperty  (id=82)
                aliasNames    ArrayList<E>  (id=120)
                attribute    false
                containingType    null
                defaultValue    null
                global    false
                hasMany    false
                indexInType    0
                instancePropertiesMap    null
                isContainment    true
                namespaceQualified    false
                opposite    null
                propertyName    "rootproperty3-notdatatype"
                propertyValues    null
                readOnly    false
                type    SDOType  (id=32)
                    aliasNames    null
                    allProperties    null
                    allPropertiesArr    null
                    baseTypes    null
                    changeSummaryProperty    null
                    declaredProperties    null
                    declaredPropertiesMap    null
                    IDProp    null
                    instancePropertiesMap    null
                    isAbstract    false
                    isDataType    false
                    isSequenced    false
                    open    false
                    propertyValues    null
                    typeName    "notDataType1"
                    typeUri    "notDataTypeUri1"
                    xmlDescriptor    null
                    xsd    false
                    xsdList    false
                    xsdLocalName    null
                xmlMapping    null
                xsd    false
                xsdLocalName    null
            [3]    SDOProperty  (id=83)
                aliasNames    ArrayList<E>  (id=127)
                attribute    false
                containingType    null
                defaultValue    null
                global    false
                hasMany    true
                indexInType    0
                instancePropertiesMap    null
                isContainment    true
                namespaceQualified    false
                opposite    null
                propertyName    "rootproperty4-list"
                propertyValues    null
                readOnly    false
                type    SDOType  (id=33)
                    aliasNames    null
                    allProperties    null
                    allPropertiesArr    null
                    baseTypes    null
                    changeSummaryProperty    null
                    declaredProperties    null
                    declaredPropertiesMap    null
                    IDProp    null
                    instancePropertiesMap    null
                    isAbstract    false
                    isDataType    false
                    isSequenced    false
                    open    false
                    propertyValues    null
                    typeName    "listProperty"
                    typeUri    "listPropertyUri"
                    xmlDescriptor    null
                    xsd    false
                    xsdList    false
                    xsdLocalName    null
                xmlMapping    null
                xsd    false
                xsdLocalName    null
            [4]    SDOProperty  (id=34)
                aliasNames    ArrayList<E>  (id=133)
                attribute    false
                containingType    null
                defaultValue    null
                global    false
                hasMany    false
                indexInType    0
                instancePropertiesMap    null
                isContainment    false
                namespaceQualified    false
                opposite    null
                propertyName    "rootProperty_NotContainment"
                propertyValues    null
                readOnly    false
                type    SDOType  (id=37)
                    aliasNames    null
                    allProperties    null
                    allPropertiesArr    null
                    baseTypes    null
                    changeSummaryProperty    null
                    declaredProperties    null
                    declaredPropertiesMap    null
                    IDProp    null
                    instancePropertiesMap    null
                    isAbstract    false
                    isDataType    false
                    isSequenced    false
                    open    false
                    propertyValues    null
                    typeName    "rootProperty_NotContainment"
                    typeUri    "rootProperty_NotContainmenturi"
                    xmlDescriptor    null
                    xsd    false
                    xsdList    false
                    xsdLocalName    null
                xmlMapping    null
                xsd    false
                xsdLocalName    null
            [5]    null
            [6]    null
            [7]    null
            [8]    null
            [9]    null
        modCount    2
        size    5
    openContentProperties    ArrayList<E>  (id=88)
        elementData    Object[10]  (id=136)
        modCount    0
        size    0
    openContentPropertiesMap    null
    properties    DefaultValueStore  (id=89)
        dataObject    SDODataObject  (id=79)
        openContentValues    HashMap<K,V>  (id=95)
        typePropertiesIsSetStatus    boolean[5]  (id=99)
            [0]    true
            [1]    true
            [2]    true
            [3]    true
            [4]    false
        typePropertyValues    Object[5]  (id=101)
            [0]    "test"
            [1]    SDODataObject  (id=56)
            [2]    SDODataObject  (id=57)
            [3]    ListWrapper  (id=140)
            [4]    null
    type    SDOType  (id=84)
        aliasNames    null
        allProperties    ArrayList<E>  (id=144)
        allPropertiesArr    Property[5]  (id=145)
        baseTypes    ArrayList<E>  (id=147)
        changeSummaryProperty    null
        declaredProperties    ArrayList<E>  (id=148)
        declaredPropertiesMap    HashMap<K,V>  (id=149)
        IDProp    null
        instancePropertiesMap    null
        isAbstract    false
        isDataType    false
        isSequenced    false
        open    false
        propertyValues    HashMap<K,V>  (id=150)
        typeName    "rootTypeName"
        typeUri    "rootTypeTypeUri"
        xmlDescriptor    null
        xsd    false
        xsdList    false
        xsdLocalName    null
    xPathEngine    XPathEngine  (id=92)

     */
}
