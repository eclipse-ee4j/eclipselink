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
 *     rbarkhouse - 2009-05-05 14:32:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm.mappings.converters;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>Purpose:</b> Provides an implementation of Converter that can be used to convert a 
 * collection of Objects into a space separated list of Strings and back. Used with 
 * XMLCompositeDirectCollectionMapping to implement the behaviour of the XmlList annotation  
 *
 * @see XMLCompositeDirectCollectionMapping
 * @see XMLConverter
 * @see Converter
 */
public class XMLListConverter implements Converter {

    private XMLConversionManager conversionManager;
    private XMLCompositeDirectCollectionMapping mapping;
    private Class objectClass = null;
    private String objectClassName = null;

    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        return this.conversionManager.convertStringToList(dataValue, getObjectClass(), mapping.getContainerPolicy());
    }

    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return this.conversionManager.convertListToString(objectValue);
    }

    public void initialize(DatabaseMapping mapping, Session session) {
        this.conversionManager = (XMLConversionManager) session.getDatasourcePlatform().getConversionManager();
        this.mapping = (XMLCompositeDirectCollectionMapping) mapping;

        try {
            if (getObjectClassName() != null) {
                ClassLoader loader = session.getDatasourcePlatform().getConversionManager().getLoader();
                Class aClass = (Class) AccessController.doPrivileged(new PrivilegedClassForName(getObjectClassName(), true, loader));
                setObjectClass(aClass);
            }
        } catch (PrivilegedActionException pae) {
            throw ValidationException.classNotFoundWhileConvertingClassNames(getObjectClassName(), pae.getException());
        }
    }

    public boolean isMutable() {
        return false;
    }

    /**
     * Get the Class name of the elements of this collection's "sub-collection".
     * Only applicable for DirectCollections of Lists (for example, for an 
     * ArrayList&lt;ArrayList&lt;Double&gt;&gt;, FieldSubElementClassName would be "java.lang.Double").
     * @return String the name of the Class of the elements of this collection's "sub-collection" 
     */
    public String getObjectClassName() {
        return objectClassName;
    }

    /**
     * Set the Class name of the elements of this collection's "sub-collection".
     * Only applicable for DirectCollections of Lists (for example, for an 
     * ArrayList&lt;ArrayList&lt;Double&gt;&gt;, FieldSubElementClassName would be "java.lang.Double").
     * @param aClassName the name of the Class of the elements of this collection's "sub-collection"
     */
    public void setObjectClassName(String aClassName) {
        objectClassName = aClassName;
    }

    /**
     * Get the Class of the elements of this collection's "sub-collection".
     * Only applicable for DirectCollections of Lists (for example, for an 
     * ArrayList&lt;ArrayList&lt;Double&gt;&gt;, FieldSubElementClass would be java.lang.Double.class).
     * @return Class the Class of the elements of this collection's "sub-collection"
     */
    public Class getObjectClass() {
        return objectClass;
    }

    /**
     * Set the Class of the elements of this collection's "sub-collection".
     * Only applicable for DirectCollections of Lists (for example, for an 
     * ArrayList&lt;ArrayList&lt;Double&gt;&gt;, FieldSubElementClass would be java.lang.Double.class).
     * @param aClass the Class of the elements of this collection's "sub-collection"
     */
    public void setObjectClass(Class aClass) {
        this.objectClass = aClass;
        if (this.objectClassName == null) {
            this.objectClassName = aClass.getName();
        }
    }

}
