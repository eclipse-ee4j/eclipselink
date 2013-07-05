/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      rbarkhouse - 2013 July 05 - 2.5.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.substitution;

import java.util.List;

import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class SubstitutionInheritanceTestCases extends SDOTestCase {

    private final String XSD = "org/eclipse/persistence/testing/sdo/substitution/hierarchySubstitution.xsd";
    private final String XML = "org/eclipse/persistence/testing/sdo/substitution/hierarchySubstitution.xml";    
    
    public SubstitutionInheritanceTestCases(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        super.setUp();
        xsdHelper.define(getSchema(XSD));
    }
    
    /**
     * Ensure that the elements in the collections are of the proper Java type.
     */
    public void testInstanceClasses() throws Exception {
        DataObject root = loadXML(XML, false);

        SDOType genericType = (SDOType) typeHelper.getType("hierarchySubstitution", "tGeneric");
        Class genericElementClass = genericType.getImplClass();

        SDOType substituteType = (SDOType) typeHelper.getType("hierarchySubstitution", "tSubstitute");
        Class substituteElementClass = substituteType.getImplClass();
        
        List<DataObject> genericElements = root.getList("genericElement");
        for (DataObject genericElement : genericElements) {
            assertEquals("Incorrect instance class for [" + genericElement.get("name") + "]",
                    genericElementClass, genericElement.getClass());
        }
        
        List<DataObject> substituteElements = root.getList("substitutableElement");
        for (DataObject substituteElement : substituteElements) {
            assertEquals("Incorrect instance class for [" + substituteElement.get("name") + "]",
                    substituteElementClass, substituteElement.getClass());
        }
    }

}