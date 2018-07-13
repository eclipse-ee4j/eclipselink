/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//    Denise Smith - 2.5
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;

/**
 * <p>Root is used to hold an Object along with the corresponding QName and some other related information.
 * Typically this is used when the object is marshalled/unmarshalled to a QName other than
 * the defaultRootElement set on the Descriptor.</p>
 *
 * <p>Root objects can be returned from Unmarshaller unmarshal operations and
 * can be given to Marshaller.marshal operations.  They may also be in values
 * returned by AnyCollectionMappings and AnyObjectMappings.</p>
 */
public class Root {
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

    /**
     * Gets the object.  This may be null.
     *
     * @return the object
     */
    public Object getObject() {
        return rootObject;
    }

    /**
     * Gets the local name. This should not be null.
     *
     * @return the local name
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Gets the namespace uri.  This may be null.
     *
     * @return the namespace uri
     */
    public String getNamespaceURI() {
        return namespaceUri;
    }

    /**
     * Sets the object associated with this XMLRoot.  This may be null.
     *
     * @param rootObject The object to associate with this XMLRoot.
     */
    public void setObject(Object rootObject) {
        this.rootObject = rootObject;
    }

    /**
     * Set the element name.  This method will parse the qualified
     * name in an attempt to set the localName fields.  ie: this could be
     * set to "someLocalName" or "somePrefix:someLocalName"
     *
     * @param name the new local name
     */
    public void setLocalName(String name) {
        if(null == name) {
            this.localName = Constants.EMPTY_STRING;
            return;
        }
        int colonIdx = name.indexOf(Constants.COLON);
        if(colonIdx > -1){
            this.localName = name.substring(colonIdx +1);
        }else{
            this.localName = name;
        }
    }

    /**
     * Sets the namespace uri associated with the QName of this XMLRoot.
     *
     * @param rootElementUri the new namespace uri
     */
    public void setNamespaceURI(String rootElementUri) {
        if(rootElementUri != null && rootElementUri.length() ==0){
            this.namespaceUri = null;
        }else{
               this.namespaceUri = rootElementUri;
        }
    }
    /**
     * Gets the encoding which will be set on the XMLRoot during unmarshal.
     *
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding.
     *
     * @param encoding the new encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Gets the XML version which will be set on the XMLRoot during unmarshal.
     *
     * @return the XML version
     */
    public String getXMLVersion() {
        return xmlVersion;
    }

    /**
     * Sets the version.
     *
     * @param version the new version
     */
    public void setVersion(String version) {
        this.xmlVersion = version;
    }

    /**
     * Gets the schema location which will be set on the XMLRoot during unmarshal.
     *
     * @return the schema location
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * Sets the schema location.
     *
     * @param schemaLocation the new schema location
     */
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    /**
     * Gets the no namespace schema location which will be set on the XMLRoot during unmarshal.
     *
     * @return the no namespace schema location
     */
    public String getNoNamespaceSchemaLocation() {
        return noNamespaceSchemaLocation;
    }

    /**
     * Sets the no namespace schema location.
     *
     * @param noNamespaceSchemaLocation the new no namespace schema location
     */
    public void setNoNamespaceSchemaLocation(String noNamespaceSchemaLocation) {
        this.noNamespaceSchemaLocation = noNamespaceSchemaLocation;
    }

    /**
     * Sets the schema type that should be associated with this XMLRoot object.
     *
     * @param schemaType the new schema type
     */
    public void setSchemaType(QName schemaType) {
        this.schemaType = schemaType;
    }

    /**
     * Gets the schema type.  This schema type will be considering when marshalling XMLRoot objects.
     *
     * @return the schema type associated with this XMLRoot object or null.
     */
    public QName getSchemaType() {
        return schemaType;
    }

    /**
     * Sets the declared type.  This may be different than the getObject().getClass(), for example,
     * in the case where inheritance is used the declared type may be the super class and the actual
     * object could be a subclass.
     *
     * @param type The declared type of this XMLRoot object.
     */
    public void setDeclaredType(Class type) {
        this.declaredType = type;
    }

    /**
     * Gets the declared type.  This may be different than the getObject().getClass(), for example,
     * in the case where inheritance is used the declared type may be the super class and the actual
     * object could be a subclass.
     *
     * @return the declared type
     */
    public Class getDeclaredType() {
        return this.declaredType;
    }

    /**
     * Checks if is nil. Returns true if this XMLRoot has been explicitly set to nil
     * or if xmlRoot.getObject() is null.
     *
     * @return true, if is nil
     */
    public boolean isNil() {
        return nil || rootObject == null;
    }

    /**
     * Sets that this XMLRoot object is nil.  If setNil(true) is explicitly called then isNil() will
     * return true even if rootObject is not null.
     *
     * @param nil
     */
    public void setNil(boolean nil) {
        this.nil = nil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (nil != root.nil) return false;
        if (declaredType != null ? !declaredType.equals(root.declaredType) : root.declaredType != null) return false;
        if (encoding != null ? !encoding.equals(root.encoding) : root.encoding != null) return false;
        if (localName != null ? !localName.equals(root.localName) : root.localName != null) return false;
        if (namespaceUri != null ? !namespaceUri.equals(root.namespaceUri) : root.namespaceUri != null) return false;
        if (noNamespaceSchemaLocation != null ? !noNamespaceSchemaLocation.equals(root.noNamespaceSchemaLocation) : root.noNamespaceSchemaLocation != null)
            return false;
        if (prefix != null ? !prefix.equals(root.prefix) : root.prefix != null) return false;
        if (rootObject != null ? !rootObject.equals(root.rootObject) : root.rootObject != null) return false;
        if (schemaLocation != null ? !schemaLocation.equals(root.schemaLocation) : root.schemaLocation != null)
            return false;
        if (schemaType != null ? !schemaType.equals(root.schemaType) : root.schemaType != null) return false;
        if (xmlVersion != null ? !xmlVersion.equals(root.xmlVersion) : root.xmlVersion != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rootObject != null ? rootObject.hashCode() : 0;
        result = 31 * result + (localName != null ? localName.hashCode() : 0);
        result = 31 * result + (namespaceUri != null ? namespaceUri.hashCode() : 0);
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        result = 31 * result + (encoding != null ? encoding.hashCode() : 0);
        result = 31 * result + (xmlVersion != null ? xmlVersion.hashCode() : 0);
        result = 31 * result + (schemaLocation != null ? schemaLocation.hashCode() : 0);
        result = 31 * result + (noNamespaceSchemaLocation != null ? noNamespaceSchemaLocation.hashCode() : 0);
        result = 31 * result + (schemaType != null ? schemaType.hashCode() : 0);
        result = 31 * result + (declaredType != null ? declaredType.hashCode() : 0);
        result = 31 * result + (nil ? 1 : 0);
        return result;
    }
}
