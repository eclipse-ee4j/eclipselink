/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class DefineAndGenerateNameCollisionsTestCases extends XSDHelperDefineAndGenerateTestCases {
    public DefineAndGenerateNameCollisionsTestCases(String name) {
        super(name);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/NameCollisions.xsd";
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate.DefineAndGenerateNameCollisionsTestCases" };
        TestRunner.main(arguments);
    }

    public String getControlGeneratedFileName() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/NameCollisionsGenerated.xsd";
    }

    public List<Type> getTypesToGenerateFrom() {
        return getControlTypes();
    }

    public List<Type> getControlTypes() {
        List<Type> types = new ArrayList<Type>();
        ((SDOTypeHelper)typeHelper).reset();        

        DataObject phoneTypeDO = dataFactory.create("commonj.sdo", "Type");
        phoneTypeDO.set("uri", "my.uri");
        phoneTypeDO.set("name", "phoneType");
        phoneTypeDO.set("sequenced", true);
        DataObject areaCodeProp = addProperty(phoneTypeDO, "areaCode", SDOConstants.SDO_STRING);
        DataObject numberProp = addProperty(phoneTypeDO, "number", SDOConstants.SDO_OBJECT);
        numberProp.set("many", true);
        Type phoneType = typeHelper.define(phoneTypeDO);
        ((SDOProperty)phoneType.getProperty("areaCode")).setXsd(true);
        ((SDOProperty)phoneType.getProperty("areaCode")).setContainment(true);
        ((SDOProperty)phoneType.getProperty("areaCode")).setXsdLocalName("areaCode");
        ((SDOProperty)phoneType.getProperty("number")).setXsd(true);
        ((SDOProperty)phoneType.getProperty("number")).setContainment(true);
        ((SDOProperty)phoneType.getProperty("number")).setXsdLocalName("number");

        DataObject personTypeDO = dataFactory.create("commonj.sdo", "Type");
        personTypeDO.set("uri", "my.uri");
        personTypeDO.set("name", "personType");
        personTypeDO.set("sequenced", true);
        addProperty(personTypeDO, "age", SDOConstants.SDO_STRING);
        DataObject nameProp = addProperty(personTypeDO, "name", SDOConstants.SDO_OBJECT);
        nameProp.set("many", true);
        Type personType = typeHelper.define(personTypeDO);
        ((SDOProperty)personType.getProperty("age")).setXsd(true);
        ((SDOProperty)personType.getProperty("age")).setContainment(true);
        ((SDOProperty)personType.getProperty("age")).setXsdLocalName("age");
        ((SDOProperty)personType.getProperty("name")).setXsd(true);
        ((SDOProperty)personType.getProperty("name")).setContainment(true);
        ((SDOProperty)personType.getProperty("name")).setXsdLocalName("name");

        DataObject jobTypeDO = dataFactory.create("commonj.sdo", "Type");
        jobTypeDO.set("uri", "my.uri");
        jobTypeDO.set("name", "jobType");
        jobTypeDO.set("sequenced", true);
        DataObject titleProp = addProperty(jobTypeDO, "title", SDOConstants.SDO_OBJECT);
        titleProp.set("many", true);
        Type jobType = typeHelper.define(jobTypeDO);
        ((SDOProperty)jobType.getProperty("title")).setXsd(true);
        ((SDOProperty)jobType.getProperty("title")).setContainment(true);
        ((SDOProperty)jobType.getProperty("title")).setXsdLocalName("title");

        DataObject contactTypeDO = dataFactory.create("commonj.sdo", "Type");
        contactTypeDO.set("uri", "my.uri");
        contactTypeDO.set("name", "contactType");
        contactTypeDO.set("sequenced", true);
        DataObject streetProp = addProperty(contactTypeDO, "street", SDOConstants.SDO_OBJECT);
        streetProp.set("many", true);
        Type contactType = typeHelper.define(contactTypeDO);
        ((SDOProperty)contactType.getProperty("street")).setXsd(true);
        ((SDOProperty)contactType.getProperty("street")).setContainment(true);
        ((SDOProperty)contactType.getProperty("street")).setXsdLocalName("street");

        DataObject addressTypeDO = dataFactory.create("commonj.sdo", "Type");
        addressTypeDO.set("uri", "my.uri");
        addressTypeDO.set("name", "addressType");
        addressTypeDO.set("sequenced", true);
        List baseTypes = new ArrayList();
        baseTypes.add(contactType);
        addressTypeDO.set("baseType", baseTypes);
        DataObject cityProp = addProperty(addressTypeDO, "city", SDOConstants.SDO_STRING);
        Type addressType = typeHelper.define(addressTypeDO);
        ((SDOProperty)addressType.getProperty("city")).setXsd(true);
        ((SDOProperty)addressType.getProperty("city")).setContainment(true);
        ((SDOProperty)addressType.getProperty("city")).setXsdLocalName("city");

        DataObject cdnAddressTypeDO = dataFactory.create("commonj.sdo", "Type");
        cdnAddressTypeDO.set("uri", "my.uri");
        cdnAddressTypeDO.set("name", "cdnAddressType");
        cdnAddressTypeDO.set("sequenced", true);
        baseTypes = new ArrayList();
        baseTypes.add(addressType);
        cdnAddressTypeDO.set("baseType", baseTypes);
        DataObject postalCodeProp = addProperty(cdnAddressTypeDO, "postalCode", SDOConstants.SDO_STRING);
        Type cdnAddressType = typeHelper.define(cdnAddressTypeDO);
        ((SDOProperty)cdnAddressType.getProperty("postalCode")).setXsd(true);
        ((SDOProperty)cdnAddressType.getProperty("postalCode")).setContainment(true);
        ((SDOProperty)cdnAddressType.getProperty("postalCode")).setXsdLocalName("postalCode");

        types.add(personType);
        types.add(jobType);
        types.add(addressType);
        types.add(contactType);
        types.add(cdnAddressType);
        types.add(phoneType);

        return types;
    }
}
