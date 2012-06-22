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
package org.eclipse.persistence.oxm;

import javax.xml.namespace.QName;

public class XMLRoot {
    protected Object rootObject;
    protected String localName;
    protected String namespaceUri;
    protected String prefix;
    protected String encoding;
    protected String xmlVersion;
    protected String schemaLocation;
    protected String noNamespaceSchemaLocation;
    protected QName schemaType;
    protected Class declaredType;
    protected boolean nil;

    public Object getObject() {
        return rootObject;
    }

    public String getLocalName() {
    	return localName;
    }

    public String getNamespaceURI() {
    	return namespaceUri;
    }

    public void setObject(Object rootObject) {
        this.rootObject = rootObject;
    }

    /**
     * Set the element name.  This method will parse the qualified
     * name in an attempt to set the prefix and localName fields.  If
     * there is no prefix, the prefix field is set to null.
     *
     * @param qualifiedName a fully qualified element name
     */
    public void setLocalName(String name) {
    	int colonIdx = name.indexOf(XMLConstants.COLON);
    	if(colonIdx > -1){
    		this.localName = name.substring(colonIdx +1);
    	}else{
    	    this.localName = name;
    	}
    	
    }

    public void setNamespaceURI(String rootElementUri) {
    	if(rootElementUri != null && rootElementUri.length() ==0){
    		this.namespaceUri = null;
    	}else{
       	    this.namespaceUri = rootElementUri;
    	}
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getXMLVersion() {
        return xmlVersion;
    }

    public void setVersion(String version) {
        this.xmlVersion = version;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getNoNamespaceSchemaLocation() {
        return noNamespaceSchemaLocation;
    }

    public void setNoNamespaceSchemaLocation(String noNamespaceSchemaLocation) {
        this.noNamespaceSchemaLocation = noNamespaceSchemaLocation;
    }

    public void setSchemaType(QName schemaType) {
        this.schemaType = schemaType;
    }

    public QName getSchemaType() {
        return schemaType;
    }
    
    public void setDeclaredType(Class type) {
        this.declaredType = type;
    }
    
    public Class getDeclaredType() {
        return this.declaredType;
    }

    public boolean isNil() {
        return nil || rootObject == null;
    }

    public void setNil(boolean nil) {
        this.nil = nil;
    }

}
