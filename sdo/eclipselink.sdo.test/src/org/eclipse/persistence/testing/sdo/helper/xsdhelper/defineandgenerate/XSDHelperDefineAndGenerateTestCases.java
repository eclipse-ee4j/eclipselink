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

import commonj.sdo.Type;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public abstract class XSDHelperDefineAndGenerateTestCases extends XSDHelperTestCases {
    public XSDHelperDefineAndGenerateTestCases(String name) {
        super(name);
    }

    public void testDefineAndGenerate() throws Exception {
        InputStream is = getSchemaInputStream(getSchemaToDefine());
        List types = xsdHelper.define(is, getSchemaLocation());

        compareGeneratedTypes(getControlTypes(), types);

        DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(getTypesToGenerateFrom(), resolver);

        String controlSchema = getSchema(getControlGeneratedFileName());
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);

        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);
        reader.close();

        assertSchemaIdentical(getDocument(getControlGeneratedFileName()), generatedSchemaDoc);
    }

    protected void compareGeneratedTypes(List controlTypes, List types) {
        assertEquals(controlTypes.size(), types.size());

        SDOProperty xmlDataTypeProperty = (SDOProperty) typeHelper.getOpenContentProperty(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_DATATYPE);
        SDOProperty xmlSchemaTypeProperty = (SDOProperty) typeHelper.getOpenContentProperty(SDOConstants.ORACLE_SDO_URL, SDOConstants. XML_SCHEMA_TYPE_NAME);

        for (int i = 0; i < types.size(); i++) {
            SDOType control = (SDOType)controlTypes.get(i);
            SDOType generated = null;
            for(int j = 0; j < types.size(); j++) {
                //look for the same type in the generated collection
                //may be in a different order
                SDOType next = (SDOType)types.get(j);
                if(next.getName().equals(control.getName())) {
                    if((next.getURI() == null && control.getURI() == null) || ((next.getURI() != null && control.getURI() != null && next.getURI().equals(control.getURI())))) {
                        generated = next;
                        break;
                    }
                }
            }
            assertNotNull(generated);
            assertEquals(control.getURI(), generated.getURI());
            assertEquals(control.getName(), generated.getName());
            if (control.getBaseTypes() == null) {
                assertNull(generated.getBaseTypes());
            }
            assertEquals(control.getBaseTypes().size(), generated.getBaseTypes().size());
            assertEquals(control.isOpen(), generated.isOpen());
            assertEquals(control.isSequenced(), generated.isSequenced());
            assertEquals(control.getInstanceClassName(), generated.getInstanceClassName());
            assertEquals(control.getDeclaredProperties().size(), generated.getDeclaredProperties().size());
            assertEquals(control.getAliasNames().size(), generated.getAliasNames().size());
            //            List generatedProps = generated.getDeclaredProperties();
            List controlProps = control.getDeclaredProperties();
            for (int j = 0; j < controlProps.size(); j++) {
                SDOProperty controlProp = (SDOProperty)controlProps.get(j);
                SDOProperty generatedProp = (SDOProperty)generated.getProperty(controlProp.getName());

                assertEquals(controlProp.isMany(), generatedProp.isMany());
                //assertEquals(controlProp.isAttribute(), generatedProp.isAttribute());
                //assertEquals(controlProp.isElement(), generatedProp.isElement());
                /*Object controlXMLElementValue = controlProp.get(SDOConstants.XMLELEMENT_PROPERTY);
                Object generatedXMLElementValue = generatedProp.get(SDOConstants.XMLELEMENT_PROPERTY);
                if (controlXMLElementValue == null) {
                    assertNull(generatedXMLElementValue);
                } else {
                    assertTrue(controlXMLElementValue instanceof Boolean);
                    assertTrue(generatedXMLElementValue instanceof Boolean);
                    assertEquals(((Boolean)controlXMLElementValue).booleanValue(), ((Boolean)generatedXMLElementValue).booleanValue());
                }*/
                Object controlXMLElementValue = controlProp.get(SDOConstants.XMLELEMENT_PROPERTY);                
                if(controlXMLElementValue != null) {
                    Object generatedXMLElementValue = generatedProp.get(SDOConstants.XMLELEMENT_PROPERTY);
                    assertTrue(controlXMLElementValue instanceof Boolean);
                    assertTrue(generatedXMLElementValue instanceof Boolean);
                    assertEquals(((Boolean)controlXMLElementValue).booleanValue(), ((Boolean)generatedXMLElementValue).booleanValue());
                }
                
                
                Object controlDataTypeValue = controlProp.get(xmlDataTypeProperty);
                Object generatedDataTypeValue = generatedProp.get(xmlDataTypeProperty);
                if (controlDataTypeValue == null) {
                    assertNull(generatedDataTypeValue);
                } else {
                    assertTrue(controlDataTypeValue instanceof Type);
                    assertTrue(generatedDataTypeValue instanceof Type);
                    assertEquals(controlDataTypeValue, generatedDataTypeValue);
                }
                
                Object generatedSchemaTypeValue = generatedProp.get(xmlSchemaTypeProperty);

                assertEquals(xsdHelper.isAttribute(controlProp), xsdHelper.isAttribute(generatedProp));
                assertEquals(xsdHelper.isElement(controlProp), xsdHelper.isElement(generatedProp));

                assertEquals(controlProp.isXsd(), generatedProp.isXsd());
                assertEquals(controlProp.getXsdLocalName(), generatedProp.getXsdLocalName());
                assertEquals(controlProp.isNullable(), generatedProp.isNullable());
                if (controlProp.getContainingType() == null) {
                    assertNull(generatedProp.getContainingType());
                } else {
                    assertNotNull(generatedProp.getContainingType());
                    assertEquals(controlProp.getContainingType().getName(), generatedProp.getContainingType().getName());
                }

                if (controlProp.getOpposite() == null) {
                    assertNull(generatedProp.getOpposite());
                } else {
                    assertNotNull(generatedProp.getOpposite());
                    assertEquals(controlProp.getOpposite().getName(), generatedProp.getOpposite().getName());
                }

                assertEquals(controlProp.getType().getURI(), generatedProp.getType().getURI());
                assertEquals(controlProp.getType().getName(), generatedProp.getType().getName());

                assertEquals(controlProp.getAliasNames().size(), generatedProp.getAliasNames().size());
                assertEquals(controlProp.isReadOnly(), generatedProp.isReadOnly());
            }
        }
    }

    /*
        ///define
        public void testDefine() {
            //String xsdSchema = getSchema(getSchemaToDefine());
            InputStream is = getSchemaInputStream(getSchemaToDefine());
            List types = xsdHelper.define(is, getSchemaLocation());

            //List types = xsdHelper.define(xsdSchema, getSchemaLocation());
            log("\nExpected:\n");
            List controlTypes = getControlTypes();
            log(controlTypes);

            log("\nActual:\n");
            log(types);
            //assertEquals(types, getControlTypes());
            //compare(types, getControlTypes());
            assertEquals(controlTypes.size(), types.size());
            for (int i = 0; i < types.size(); i++) {
                SDOType generated = (SDOType)types.get(i);
                SDOType control = (SDOType)controlTypes.get(i);
                assertEquals(control.getURI(), generated.getURI());
                assertEquals(control.getName(), generated.getName());
                if (control.getBaseTypes() == null) {
                    assertNull(generated.getBaseTypes());
                }
                assertEquals(control.getBaseTypes().size(), generated.getBaseTypes().size());

                assertEquals(control.getInstanceClassName(), generated.getInstanceClassName());
                assertEquals(control.getDeclaredProperties().size(), generated.getDeclaredProperties().size());
                assertEquals(control.getAliasNames().size(), generated.getAliasNames().size());
                //            List generatedProps = generated.getDeclaredProperties();
                List controlProps = control.getDeclaredProperties();
                for (int j = 0; j < controlProps.size(); j++) {
                    SDOProperty controlProp = (SDOProperty)controlProps.get(j);
                    SDOProperty generatedProp = (SDOProperty)generated.getProperty(controlProp.getName());

                    assertEquals(controlProp.isMany(), generatedProp.isMany());
                    assertEquals(controlProp.isAttribute(), generatedProp.isAttribute());
                    assertEquals(controlProp.isElement(), generatedProp.isElement());
                    assertEquals(controlProp.isXsd(), generatedProp.isXsd());
                    assertEquals(controlProp.getXsdLocalName(), generatedProp.getXsdLocalName());

                    if (controlProp.getContainingType() == null) {
                        assertNull(generatedProp.getContainingType());
                    } else {
                        assertNotNull(generatedProp.getContainingType());
                        assertEquals(controlProp.getContainingType().getName(), generatedProp.getContainingType().getName());
                    }

                    if (controlProp.getOpposite() == null) {
                        assertNull(generatedProp.getOpposite());
                    } else {
                        assertEquals(controlProp.getOpposite().getName(), generatedProp.getOpposite().getName());
                    }

                    assertEquals(controlProp.getType().getURI(), generatedProp.getType().getURI());
                    assertEquals(controlProp.getType().getName(), generatedProp.getType().getName());

                    assertEquals(controlProp.getAliasNames().size(), generatedProp.getAliasNames().size());
                    assertEquals(controlProp.isReadOnly(), generatedProp.isReadOnly());
                }
            }
        }
    */
    public abstract String getSchemaToDefine();

    public abstract List getControlTypes();

    protected String getSchemaLocation() {
        return null;
    }

    //generate

    /*
    public void testGenerateSchema() {
        //String generatedSchema = xsdHelper.generate(getTypesToGenerateFrom(), getSchemaNamespacesMap());
        DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(getTypesToGenerateFrom(), resolver);

        String controlSchema = getSchema(getControlGeneratedFileName());
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);

        assertXMLIdentical(controlSchema, generatedSchema);
    }
    */
    public java.util.Map getMap() {
        return new HashMap();
    }

    protected List getTypesToGenerateFrom() {
        return getControlTypes();
    }

    protected String getControlGeneratedFileName() {
        return getSchemaToDefine();
    }
}
