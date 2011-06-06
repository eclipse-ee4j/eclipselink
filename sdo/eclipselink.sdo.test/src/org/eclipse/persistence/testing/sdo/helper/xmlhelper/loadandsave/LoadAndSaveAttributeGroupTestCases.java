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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;

public class LoadAndSaveAttributeGroupTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveAttributeGroupTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveAttributeGroupTestCases" };
        TestRunner.main(arguments);
    }

    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        Type rootType = typeHelper.getType(getControlRootURI(), "root");
        Type childType = typeHelper.getType(getControlRootURI(), "child");
        assertTrue(rootType.isOpen());
        assertTrue(childType.isOpen());
        List rootDecalredProps = rootType.getDeclaredProperties();
        assertEquals(11, rootDecalredProps.size());
        assertEquals("value", ((Property)rootDecalredProps.get(0)).getName());
        assertEquals("arrayType", ((Property)rootDecalredProps.get(1)).getName());
        assertEquals("offset", ((Property)rootDecalredProps.get(2)).getName());
        assertEquals("position", ((Property)rootDecalredProps.get(3)).getName());
        assertEquals("extra", ((Property)rootDecalredProps.get(4)).getName());
        assertEquals("imported1", ((Property)rootDecalredProps.get(5)).getName());
        assertEquals("imported2", ((Property)rootDecalredProps.get(6)).getName());
        assertEquals("imported3", ((Property)rootDecalredProps.get(7)).getName());
        assertEquals("id", ((Property)rootDecalredProps.get(8)).getName());
        assertEquals("href", ((Property)rootDecalredProps.get(9)).getName());
        assertEquals("importedsecond", ((Property)rootDecalredProps.get(10)).getName());

        List childDeclaredProps = childType.getDeclaredProperties();
        assertEquals(9, childDeclaredProps.size());
        assertEquals("arrayType", ((Property)childDeclaredProps.get(0)).getName());
        assertEquals("offset", ((Property)childDeclaredProps.get(1)).getName());
        assertEquals("position", ((Property)childDeclaredProps.get(2)).getName());
        assertEquals("childextra", ((Property)childDeclaredProps.get(3)).getName());
        assertEquals("included1", ((Property)childDeclaredProps.get(4)).getName());
        assertEquals("included2", ((Property)childDeclaredProps.get(5)).getName());
        assertEquals("included3", ((Property)childDeclaredProps.get(6)).getName());
        assertEquals("id", ((Property)childDeclaredProps.get(7)).getName());
        assertEquals("href", ((Property)childDeclaredProps.get(8)).getName());
    }

    protected List defineTypes() {
        try {
            URL url = new URL(getSchemaLocation() + getSchemaName());
            InputStream is = url.openStream();

            return xsdHelper.define(is, getSchemaLocation());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void generateClasses(String tmpDirName) throws Exception {
        URL url = new URL(getSchemaLocation() + getSchemaName());
        InputStream is = url.openStream();

        SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);

        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
        schemaResolver.setBaseSchemaLocation(getSchemaLocation());
        StreamSource ss = new StreamSource(is);
        classGenerator.generate(ss, tmpDirName, schemaResolver);
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xmlhelper/attributegroup/";
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/attributegroup/AttributeGroup.xml";
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/attributegroup/AttributeGroupNoSchema.xml";
    }

    protected String getSchemaName() {
        return "AttributeGroup.xsd";
    }

    protected String getControlRootURI() {
        return "http://schemas.xmlsoap.org/soap/encoding/";
    }

    protected String getControlRootName() {
        return "wrapper";
    }

    protected String getRootInterfaceName() {
        return "Wrapper";
    }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();       
        packages.add("org/xmlsoap/schemas/soap/encoding");
        return packages;
    }
    
    public void registerTypes() {
        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject arrayCoordinateDO = dataFactory.create(typeType);
        arrayCoordinateDO.set("name", "arrayCoordinate");
        arrayCoordinateDO.set("uri", "http://schemas.xmlsoap.org/soap/encoding/");
        arrayCoordinateDO.set("dataType", true);
        List baseTypes = new ArrayList();
        baseTypes.add(stringType);
        arrayCoordinateDO.set("baseType", baseTypes);
        Type arrayCoordinate = typeHelper.define(arrayCoordinateDO);

        DataObject rootDO = dataFactory.create(typeType);
        rootDO.set("name", "root");
        rootDO.set("uri", "http://schemas.xmlsoap.org/soap/encoding/");
        rootDO.set("open", true);
        baseTypes = new ArrayList();
        baseTypes.add(stringType);
        rootDO.set("baseType", baseTypes);
        addProperty(rootDO, "value", SDOConstants.SDO_STRING, false, false, false);
        addProperty(rootDO, "arrayType", SDOConstants.SDO_STRING, false, false, false);
        addProperty(rootDO, "offset", arrayCoordinate, false, false, false);
        addProperty(rootDO, "position", SDOConstants.SDO_STRING, false, false, false);
        addProperty(rootDO, "extra", SDOConstants.SDO_STRING, false, false, false);
        addProperty(rootDO, "imported1", SDOConstants.SDO_STRING, false, false, false);
        addProperty(rootDO, "imported2", SDOConstants.SDO_STRING, false, false, false);
        addProperty(rootDO, "imported3", SDOConstants.SDO_STRING, false, false, false);
        addProperty(rootDO, "id", SDOConstants.SDO_STRING, false, false, false);
        addProperty(rootDO, "href", SDOConstants.SDO_STRING, false, false, false);
        addProperty(rootDO, "importedsecond", SDOConstants.SDO_STRING, false, false, false);
        Type rootType = typeHelper.define(rootDO);

        DataObject childDO = dataFactory.create(typeType);
        childDO.set("name", "child");
        childDO.set("uri", "http://schemas.xmlsoap.org/soap/encoding/");
        childDO.set("open", true);
        baseTypes = new ArrayList();
        baseTypes.add(SDOConstants.SDO_STRING);
        childDO.set("baseType", baseTypes);
        addProperty(childDO, "arrayType", SDOConstants.SDO_STRING, false, false, false);
        addProperty(childDO, "offset", arrayCoordinate, false, false, false);
        addProperty(childDO, "position", SDOConstants.SDO_STRING, false, false, false);
        addProperty(childDO, "childextra", SDOConstants.SDO_STRING, false, false, false);
        addProperty(childDO, "included1", SDOConstants.SDO_STRING, false, false, false);
        addProperty(childDO, "included2", SDOConstants.SDO_STRING, false, false, false);
        addProperty(childDO, "included3", SDOConstants.SDO_STRING, false, false, false);
        addProperty(childDO, "id", SDOConstants.SDO_STRING, false, false, false);
        addProperty(childDO, "href", SDOConstants.SDO_STRING, false, false, false);
        Type childType = typeHelper.define(childDO);

        DataObject rootPropDO = dataFactory.create(propertyType);
        rootPropDO.set("name", "root");
        rootPropDO.set("type", rootType);
        typeHelper.defineOpenContentProperty("http://schemas.xmlsoap.org/soap/encoding/", rootPropDO);

        DataObject wrapperDO = dataFactory.create(typeType);
        wrapperDO.set("name", "wrapper");
        wrapperDO.set("uri", "http://schemas.xmlsoap.org/soap/encoding/");
        addProperty(wrapperDO, "root", rootType, true, false, true);
        addProperty(wrapperDO, "child", childType, true, false, true);
        Type wrapperType = typeHelper.define(wrapperDO);

        DataObject wrapperPropDO = dataFactory.create(propertyType);
        wrapperPropDO.set("name", "wrapper");
        wrapperPropDO.set("type", wrapperType);
        typeHelper.defineOpenContentProperty("http://schemas.xmlsoap.org/soap/encoding/", wrapperPropDO);
        typeHelper.define(rootDO);
    }
}
