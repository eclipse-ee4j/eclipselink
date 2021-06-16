/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//      rbarkhouse - 2013 July 05 - 2.5.1 - Initial implementation
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
