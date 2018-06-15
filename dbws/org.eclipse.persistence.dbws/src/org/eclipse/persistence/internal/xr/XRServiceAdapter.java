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

package org.eclipse.persistence.internal.xr;

// Javase imports
import java.util.HashMap;
import java.util.Map;

// Java extension imports
import javax.xml.namespace.QName;

// EclipseLink imports
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>INTERNAL</b>: runtime implementation of TopLink XR Service
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class XRServiceAdapter extends XRServiceModel {

    protected String schemaNamespace;
    protected Session orSession;
    protected Session oxSession;
    protected XMLContext xmlContext;
    protected Schema schema;
    protected Map<QName, XMLDescriptor> descriptorsByQName = new HashMap<QName, XMLDescriptor>();

    public Session getORSession() {
        return orSession;
    }
    public void setORSession(Session orSession) {
        this.orSession = orSession;
    }

    public Session getOXSession() {
        return oxSession;
    }
    public void setOXSession(Session oxSession) {
        this.oxSession = oxSession;
    }

    public XMLContext getXMLContext() {
        return xmlContext;
    }
    public void setXMLContext(XMLContext xmlContext) {
        this.xmlContext = xmlContext;
    }

    public String getSchemaNamespace() {
        return schemaNamespace;
    }
    public void setSchemaNamespace(String schemaNamespace) {
        this.schemaNamespace = schemaNamespace;
    }

    public Schema getSchema() {
        return schema;
    }
    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public Map<QName, XMLDescriptor> getDescriptorsByQName() {
        return descriptorsByQName;
    }
    public void setDescriptorsByQName(Map<QName, XMLDescriptor> descriptorsByQName) {
        this.descriptorsByQName = descriptorsByQName;
    }

    public Class<?> getTypeClass(QName type) {
        XMLDescriptor xdesc = descriptorsByQName.get(type);
        if (xdesc != null) {
            return xdesc.getJavaClass();
        }
        else {
            return null;
        }
    }
}
