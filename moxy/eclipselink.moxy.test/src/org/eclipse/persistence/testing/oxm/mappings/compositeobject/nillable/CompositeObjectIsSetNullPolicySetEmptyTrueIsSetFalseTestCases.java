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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable;

import org.w3c.dom.Document;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

/**
 * UC 9-3 to 9-4 and 11.2
 * This use case is a special case that really only tests marshal.
 * Marshal input isset state != Unmarshal output isset state.
 * The unmarshal (9-3 to 9-4) case will not round trip because isset for manager will always be true
 * for both the isset=false and isset=true versions of this test.
 */
public class CompositeObjectIsSetNullPolicySetEmptyTrueIsSetFalseTestCases extends XMLWithJSONMappingTestCases {
    private final static String XML_RESOURCE = //
    	"org/eclipse/persistence/testing/oxm/mappings/compositeobject/nillable/CompositeObjectIsSetNullPolicySetEmptyTrueIsSetFalse.xml";
    private final static String JSON_RESOURCE = //
    	"org/eclipse/persistence/testing/oxm/mappings/compositeobject/nillable/CompositeObjectIsSetNullPolicySetEmptyTrueIsSetFalse.json";
    private final static String XML_WRITE_RESOURCE = //
    	"org/eclipse/persistence/testing/oxm/mappings/compositeobject/nillable/CompositeObjectIsSetNullPolicySetEmptyTrueIsSetFalse.xml";    																		   

    public CompositeObjectIsSetNullPolicySetEmptyTrueIsSetFalseTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);       
		setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);

        AbstractNullPolicy aNullPolicy = new IsSetNullPolicy();
    	// Alter unmarshal policy state
    	aNullPolicy.setNullRepresentedByEmptyNode(true);
    	aNullPolicy.setNullRepresentedByXsiNil(false);
    	// Alter marshal policy state
    	aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);

    	((IsSetNullPolicy)aNullPolicy).setIsSetMethodName("isSetManager");
        Project aProject = new CompositeObjectNodeNullPolicyProject(true);
        XMLDescriptor teamDescriptor = (XMLDescriptor) aProject.getDescriptor(Team.class);
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);
        teamDescriptor.setNamespaceResolver(namespaceResolver);
        XMLCompositeObjectMapping aMapping = (XMLCompositeObjectMapping) teamDescriptor.getMappingForAttributeName("manager");
        aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    // Marshal
    protected Document getWriteControlDocument() throws Exception {
        return writeControlDocument;
    }

    // Override unmarshal
    public Object getReadControlObject() {
    	Team aTeam = new Team();
    	aTeam.setId(123);
    	aTeam.setName("Eng");
    	aTeam.setManager(null); // output isset=true for UC 9-3 to 9-4       
        return aTeam;
    }

    // Marshal
    protected Object getControlObject() {
    	Team aTeam = new Team();
    	aTeam.setId(123);
    	aTeam.setName("Eng");
    	aTeam.setManager(null); // input sset=false for UC 11.2       
        return aTeam;
    }
}
