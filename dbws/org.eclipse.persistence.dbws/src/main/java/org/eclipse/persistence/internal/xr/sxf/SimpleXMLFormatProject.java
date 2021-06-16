/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.xr.sxf;

// Javase imports

// Java extension imports

// EclipseLink imports
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_TAG;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.mappings.XMLFragmentCollectionMapping;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.sessions.Project;

@SuppressWarnings("serial")
public class SimpleXMLFormatProject extends Project {

    private transient NamespaceResolver ns;

    public SimpleXMLFormatProject() {
        ns = new NamespaceResolver();
        setName("SimpleXMLFormatProject");
        applyLogin();
        addDescriptor(buildXRRowSetModelDescriptor());
    }

    @Override
    public void applyLogin() {
        XMLLogin login = new XMLLogin();
        login.setPlatform(new DOMPlatform());
        setDatasourceLogin(login);
    }

    public ClassDescriptor buildXRRowSetModelDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.descriptorIsAggregate();
        descriptor.setJavaClass(SimpleXMLFormatModel.class);
        descriptor.setAlias(DEFAULT_SIMPLE_XML_FORMAT_TAG);
        descriptor.setNamespaceResolver(ns);
        descriptor.setDefaultRootElement(DEFAULT_SIMPLE_XML_FORMAT_TAG);
        XMLSchemaURLReference schemaReference = new XMLSchemaURLReference("");
        schemaReference.setSchemaContext("/" + DEFAULT_SIMPLE_XML_FORMAT_TAG);
        schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
        descriptor.setSchemaReference(schemaReference);

        XMLFragmentCollectionMapping fragMapping = new XMLFragmentCollectionMapping();
        fragMapping.setAttributeName("simpleXML");
        fragMapping.setXPath(DEFAULT_SIMPLE_XML_TAG);
        descriptor.addMapping(fragMapping);
        return descriptor;
    }

}
