/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
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
     * Resolve the object of Class <tt>type</tt>, uniquely identified by <tt>id</tt>.
     * </p>
     *
     * @param id The <tt>Object</tt> that uniquely identifies the object to be found.
     * @param type The <tt>Class</tt> of the object to be found.
     *
     * @return a <tt>Callable</tt> that will return the resolved object.
     *
     * @throws SAXException
     */
    public abstract Callable<?> resolve(Object id, Class type) throws SAXException;

    /**
     * <p>
     * Resolve the object of Class <tt>type</tt>, uniquely identified by the composite key information specified in the <tt>id</tt> Map.
     * </p>
     *
     * @param id A <tt>Map</tt> of id values, keyed on the attribute name.
     * @param type The <tt>Class</tt> of the object to be found.
     *
     * @return a <tt>Callable</tt> that will return the resolved object.
     *
     * @throws SAXException
     */
    public abstract Callable<?> resolve(Map<String, Object> id, Class type) throws SAXException;

    /**
     * <p>
     * Bind the object <tt>obj</tt> to the identifier <tt>id</tt>.
     * </p>
     *
     * @param id The id <tt>Object</tt> that uniquely identifies the object to be bound.
     * @param obj The object that will be bound to this id.
     *
     * @throws SAXException
     */
    public abstract void bind(Object id, Object obj) throws SAXException;

    /**
     * <p>
     * Bind the object <tt>obj</tt> to the composite key information specified in the <tt>id</tt> Map.
     * </p>
     *
     * @param id A <tt>Map</tt> of id values, keyed on attribute name.
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