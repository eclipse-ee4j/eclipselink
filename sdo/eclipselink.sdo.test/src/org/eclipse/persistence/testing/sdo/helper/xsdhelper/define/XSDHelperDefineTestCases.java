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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.io.InputStream;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;

public abstract class XSDHelperDefineTestCases extends XSDHelperTestCases {
    public XSDHelperDefineTestCases(String name) {
        super(name);
    }

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

        compare(getControlTypes(), types);
    }

    protected void compare(List controlTypes, List types) {
        assertEquals(controlTypes.size(), types.size());
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
            if (control.getBaseTypes() == null) {
                assertNull(generated.getBaseTypes());
            }
            assertEquals(control.getBaseTypes().size(), generated.getBaseTypes().size());
            String x = control.getInstanceClassName();
            String y = generated.getInstanceClassName();
            assertEquals(control.getInstanceClassName(), generated.getInstanceClassName());
            assertEquals(control.getDeclaredProperties().size(), generated.getDeclaredProperties().size());
            assertEquals(control.getAliasNames().size(), generated.getAliasNames().size());
            assertEquals(xsdHelper.getLocalName(control), xsdHelper.getLocalName(generated));
            assertEquals(xsdHelper.isXSD(control), xsdHelper.isXSD(generated));
            
            //            List generatedProps = generated.getDeclaredProperties();
            List controlProps = control.getDeclaredProperties();
            for (int j = 0; j < controlProps.size(); j++) {
                SDOProperty controlProp = (SDOProperty)controlProps.get(j);
                SDOProperty generatedProp = (SDOProperty)generated.getProperty(controlProp.getName());

                assertEquals(controlProp.isMany(), generatedProp.isMany());
                //assertEquals(controlProp.isAttribute(), generatedProp.isAttribute());
                Object controlXMLElementValue = controlProp.get(SDOConstants.XMLELEMENT_PROPERTY);
                Object generatedXMLElementValue = controlProp.get(SDOConstants.XMLELEMENT_PROPERTY);
                if (controlXMLElementValue == null) {
                    assertNull(generatedXMLElementValue);
                } else {
                    assertTrue(controlXMLElementValue instanceof Boolean);
                    assertTrue(generatedXMLElementValue instanceof Boolean);
                    assertEquals(((Boolean)controlXMLElementValue).booleanValue(), ((Boolean)generatedXMLElementValue).booleanValue());
                }

                assertEquals(xsdHelper.isAttribute(controlProp), xsdHelper.isAttribute(generatedProp));
                assertEquals(xsdHelper.isElement(controlProp), xsdHelper.isElement(generatedProp));
                assertEquals(xsdHelper.getLocalName(controlProp), xsdHelper.getLocalName(generatedProp));
                assertEquals(xsdHelper.getNamespaceURI(controlProp), xsdHelper.getNamespaceURI(generatedProp));
                assertEquals(controlProp.isXsd(), generatedProp.isXsd());
                assertEquals(controlProp.getXsdLocalName(), generatedProp.getXsdLocalName());

                if (controlProp.getContainingType() == null) {
                    assertNull(generatedProp.getContainingType());
                } else {
                    assertNotNull(generatedProp.getContainingType());
                    assertEquals(controlProp.getContainingType().getName(), generatedProp.getContainingType().getName());
                }

                // TODO: 20060906: Bidirectional
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

    public abstract String getSchemaToDefine();

    public abstract List getControlTypes();

    protected String getSchemaLocation() {
        return null;
    }
}
