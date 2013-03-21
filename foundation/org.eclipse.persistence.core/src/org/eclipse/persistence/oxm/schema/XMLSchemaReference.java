/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.oxm.schema;

import java.net.URL;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;

import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import javax.xml.namespace.QName;

/**
 * Provides a way for a descriptor's reference to its schema to be specified.
 * The schema can be reference through a classpath resource, a file or URL.
 */
public abstract class XMLSchemaReference implements org.eclipse.persistence.platform.xml.XMLSchemaReference {

    /**
     * The string used to access the XMLSchema, be it classpath resource, URL,
     * or file name
     */
    protected String resource;

    /** The path to a simple/complex type definition or element in the schema */
    protected String schemaContext;

    /**
     * Indicates if a simple/complex type definition, element, or group is being
     * referenced
     */
    protected int type;

    protected QName schemaContextAsQName;

    /**
     * The default constructor.
     */
    protected XMLSchemaReference() {
        super();
        type = COMPLEX_TYPE;
    }

    /**
     * This constructor takes a string that references an XMLSchema.
     * 
     * @param resource -
     *            used to access the XMLSchema (classpath, URL, or file name)
     */
    protected XMLSchemaReference(String resource) {
        this();
        this.resource = resource;
    }

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public abstract URL getURL();

    /**
     * Indicates if the schema reference references a simple type definition,
     * complex type definition, element, or group.
     * 
     * @return COMPLEX_TYPE=1, SIMPLE_TYPE=2, ELEMENT=3, GROUP=5
     */
    public int getType() {
        return type;
    }

    /**
     * Set to indicate if the schema reference references a simple type
     * definition, complex type definition, element or group.
     * 
     * @param type -
     *            COMPLEX_TYPE=1, SIMPLE_TYPE=2, ELEMENT=3, GROUP=5
     */
    public void setType(int type) {
        if ((type < 1) || (type > 3 && type != 5)) {
            throw XMLPlatformException.xmlPlatformInvalidTypeException(type);
        }

        this.type = type;
    }

    /**
     * Get the path to the simple/complex type definition, element or group to
     * be referenced in the schema
     * 
     * @return the schema context
     */
    public String getSchemaContext() {
        return this.schemaContext;
    }

    /**
     * Set the path to the simple/complex type definition, element, or group to
     * be referenced in the schema
     * 
     * @param schemaContext -
     *            the schema context
     */
    public void setSchemaContext(String schemaContext) {
        this.schemaContext = schemaContext;
    }
    
    public void setSchemaContextAsQName(QName qname) {
        schemaContextAsQName = qname;
    }

    public QName getSchemaContextAsQName() {
        return schemaContextAsQName;
    }

    /**
     * INTERNAL:
     * @param nsResolver
     * @return
     */
    public QName getSchemaContextAsQName(NamespaceResolver nsResolver) {
        if (schemaContext == null) {
            return null;
        }

        if (schemaContextAsQName == null) {
            int idx = schemaContext.lastIndexOf("/");
            String type = schemaContext.substring(idx + 1);
            idx = type.indexOf(Constants.COLON);
            if (idx != -1) {
                String prefix = type.substring(0, idx);
                String localPart = type.substring(idx + 1);
                String uri = nsResolver.resolveNamespacePrefix(prefix);
                schemaContextAsQName = new QName(uri, localPart);
            } else {
                if (nsResolver != null && nsResolver.getDefaultNamespaceURI() != null) {
                    schemaContextAsQName = new QName(nsResolver.getDefaultNamespaceURI(), type);
                } else {
                    schemaContextAsQName = new QName(type);
                }
            }
        }
        return schemaContextAsQName;
    }

    public boolean isValid(Document document, ErrorHandler errorHandler) {
        try {
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
            return xmlPlatform.validateDocument(document, getURL(), errorHandler);
        } catch (XMLPlatformException e) {
            if (e.getErrorCode() == XMLPlatformException.XML_PLATFORM_PARSER_ERROR_RESOLVING_XML_SCHEMA) {
                throw XMLMarshalException.errorResolvingXMLSchema(e);
            } else {
                return false;
            }
        }
    }

    /**
     * Indicates a global definition
     */
    public boolean isGlobalDefinition() {
        // more than one occurance of "/" indicates a local definition
        return schemaContext.lastIndexOf("/") <= 0;
    }

    /**
     * Allow subclasses to perform initialization if required.  This method will
     * typically be called during the owning descriptor's initialization phase.
     * 
     * @param session
     */
    public void initialize(AbstractSession session) {
    }
}
