/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

/**
 * UC 10-5 and 8-3 to 8-4
 * ISNBEN=false
 */
public class CompositeObjectNullPolicySetEmptyTrueIsSetFalseTestCases extends XMLWithJSONMappingTestCases {
    private final static String XML_RESOURCE = //
        "org/eclipse/persistence/testing/oxm/mappings/compositeobject/nillable/CompositeObjectNullPolicySetEmptyTrueIsSetFalse.xml";
    private final static String JSON_RESOURCE = //
        "org/eclipse/persistence/testing/oxm/mappings/compositeobject/nillable/CompositeObjectNullPolicySetEmptyTrueIsSetFalse.json";

    public CompositeObjectNullPolicySetEmptyTrueIsSetFalseTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        AbstractNullPolicy aNullPolicy = new NullPolicy();
        // Alter unmarshal policy state
        aNullPolicy.setNullRepresentedByEmptyNode(true); // false=new Manager, true=null
        aNullPolicy.setNullRepresentedByXsiNil(false); // No effect
        ((NullPolicy)aNullPolicy).setSetPerformedForAbsentNode(false); // non-default

        // Alter marshal policy state
        aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);

        Project aProject = new CompositeObjectNodeNullPolicyProject(true);
        XMLDescriptor teamDescriptor = (XMLDescriptor) aProject.getDescriptor(Team.class);
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        teamDescriptor.setNamespaceResolver(namespaceResolver);
        XMLCompositeObjectMapping aMapping = (XMLCompositeObjectMapping) teamDescriptor.getMappingForAttributeName("manager");
        aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
        Team aTeam = new Team();
        aTeam.setId(123);
        aTeam.setName("Eng");
        Employee anEmployee = null;//new Employee();
        aTeam.setManager(anEmployee);
        return aTeam;
    }


    /*
//  public void testXMLToObjectFromInputStream() throws Exception {    }
  public void testObjectToXMLDocument() throws Exception {    }
  public void testObjectToXMLStringWriter() throws Exception {   }
  public void testObjectToContentHandler() throws Exception {    }
  public void testXMLToObjectFromURL() throws Exception {    }
  public void testUnmarshallerHandler() throws Exception {    }
*/

}
