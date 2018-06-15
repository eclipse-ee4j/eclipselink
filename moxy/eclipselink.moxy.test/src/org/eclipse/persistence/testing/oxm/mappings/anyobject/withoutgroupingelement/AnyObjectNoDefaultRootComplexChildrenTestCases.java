/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;


/**
 *  @version $Header: AnyObjectNoDefaultRootComplexChildrenTestCases.java 28-apr-2006.15:35:47 mfobrien Exp $
 *  @author  mfobrien
 *  @since   10.1.3.1.0
 */
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.Child;
import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.Root;

public class AnyObjectNoDefaultRootComplexChildrenTestCases extends XMLWithJSONMappingTestCases {
    public static final  String XML_RESOURCE_PATH = "org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/no_default_root_element_children.xml";
     public static final String JSON_RESOURCE_PATH = "org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/no_default_root_element_children.json";

    public AnyObjectNoDefaultRootComplexChildrenTestCases(String name) throws Exception {
        super(name);
        Project p = new AnyObjectNoDefaultRootWithoutGroupingElementProject();
        setProject(p);
        setControlDocument(XML_RESOURCE_PATH);
        setControlJSON(JSON_RESOURCE_PATH);
        expectsMarshalException = true;
    }

    public boolean isUnmarshalTest() {
        return false;
    }

    public Object getControlObject() {
        Root root = new Root();
        Child child = new Child();
        child.setContent("child's text");
        root.setAny(child);
        return root;
    }

    @Override
    public void assertMarshalException(Exception exception) throws Exception {
        if(exception.getClass() == XMLMarshalException.class) {
            XMLMarshalException xmlMarshalException = (XMLMarshalException) exception;
            if(XMLMarshalException.DEFAULT_ROOT_ELEMENT_NOT_SPECIFIED == xmlMarshalException.getErrorCode()) {
                return;
            }
        }
        throw exception;
    }


}
