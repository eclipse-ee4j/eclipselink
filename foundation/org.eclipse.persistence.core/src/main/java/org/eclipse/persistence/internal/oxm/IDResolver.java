/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.oxm;

import java.util.Map;
import java.util.concurrent.Callable;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * <p>
 * IDResolver can be subclassed to allow customization of the ID/IDREF processing of
 * Unmarshaller.  A custom IDResolver can be specified on the Unmarshaller as follows:
 * </p>
 *
 * <p>
 * <code>
 * IDResolver customResolver = new MyIDResolver();
 * unmarshaller.setIDResolver(customResolver);
 * </code>
 * </p>
 * @since EclipseLink 2.5.0
 */
public abstract class IDResolver {

    /**
     * <p>
     * Resolve the object of Class <code>type</code>, uniquely identified by <code>id</code>.
     * </p>
     *
     * @param id The <code>Object</code> that uniquely identifies the object to be found.
     * @param type The <code>Class</code> of the object to be found.
     *
     * @return a <code>Callable</code> that will return the resolved object.
     *
     * @throws SAXException
     */
    public abstract Callable<?> resolve(Object id, Class type) throws SAXException;

    /**
     * <p>
     * Resolve the object of Class <code>type</code>, uniquely identified by the composite key information specified in the <code>id</code> Map.
     * </p>
     *
     * @param id A <code>Map</code> of id values, keyed on the attribute name.
     * @param type The <code>Class</code> of the object to be found.
     *
     * @return a <code>Callable</code> that will return the resolved object.
     *
     * @throws SAXException
     */
    public abstract Callable<?> resolve(Map<String, Object> id, Class type) throws SAXException;

    /**
     * <p>
     * Bind the object <code>obj</code> to the identifier <code>id</code>.
     * </p>
     *
     * @param id The id <code>Object</code> that uniquely identifies the object to be bound.
     * @param obj The object that will be bound to this id.
     *
     * @throws SAXException
     */
    public abstract void bind(Object id, Object obj) throws SAXException;

    /**
     * <p>
     * Bind the object <code>obj</code> to the composite key information specified in the <code>id</code> Map.
     * </p>
     *
     * @param id A <code>Map</code> of id values, keyed on attribute name.
     * @param obj The object that will be bound to this id.
     *
     * @throws SAXException
     */
    public abstract void bind(Map<String, Object> id, Object obj) throws SAXException;

    /**
     * <p>
     * Called when unmarshalling begins.
     * </p>
     *
     * @param errorHandler Any errors encountered during the unmarshal process should be reported to this handler.
     *
     * @throws SAXException
     */
    public void startDocument(ErrorHandler errorHandler) throws SAXException {}

    /**
     * <p>
     * Called when unmarshalling ends.
     * </p>
     *
     * @throws SAXException
     */
    public void endDocument() throws SAXException {}

}
