/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 29 January 2013 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.idresolver.collection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.bind.ValidationEventHandler;

import org.eclipse.persistence.jaxb.IDResolver;
import org.xml.sax.SAXException;

public class MyIDResolver extends IDResolver {

    public boolean hitStartDocument = false;
    public boolean hitEndDocument = false;
    public boolean hitBind = false;
    public boolean hitResolve = false;
    public boolean hitBindSingle = false;
    public boolean hitResolveSingle = false;
    public boolean eventHandlerNotNull = false;

    Map<Map<String, Object>, Object> objects = new LinkedHashMap<Map<String, Object>, Object>();

    @Override
    public void startDocument(ValidationEventHandler eventHandler) throws SAXException {
        hitStartDocument = true;
        eventHandlerNotNull = (eventHandler != null);
    }

    @Override
    public void endDocument() throws SAXException {
        hitEndDocument = true;
    }

    @Override
    public void bind(Map<String, Object> idWrapper, Object obj) throws SAXException {
        hitBind = true;
        objects.put(idWrapper, obj);
    }

    @Override
    public Callable<Object> resolve(final Map<String, Object> idWrapper, final Class type) throws SAXException {
        hitResolve = true;
        return new Callable<Object>() {
            public Object call() {
                Object obj = objects.get(idWrapper);
                ((TestObject) obj).processed = true;
                return obj;
            }
        };
    }

    @Override
    public void bind(Object id, Object obj) throws SAXException {
        hitBindSingle = true;
        hitBindSingle = true;
        Map<String, Object> idMap = new LinkedHashMap<String, Object>(1);
        idMap.put("stringId", id);
        bind(idMap, obj);
    }

    @Override
    public Callable<?> resolve(Object id, Class type) throws SAXException {
        hitResolveSingle = true;
        Map<String, Object> idMap = new LinkedHashMap<String, Object>(1);
        idMap.put("stringId", id);
        return resolve(idMap, type);
    }

}
