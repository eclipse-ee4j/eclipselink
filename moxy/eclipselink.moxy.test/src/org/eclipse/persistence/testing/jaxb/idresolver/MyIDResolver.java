/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 26 October 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.idresolver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.bind.ValidationEventHandler;

import org.eclipse.persistence.jaxb.IDResolver;

import org.xml.sax.SAXException;

public class MyIDResolver extends IDResolver {
    Map<Map<String, Object>, Apple> apples = new LinkedHashMap();
    Map<Map<String, Object>, Orange> oranges = new LinkedHashMap();

    public boolean hitStartDocument = false;
    public boolean hitEndDocument = false;
    public boolean hitBind = false;
    public boolean hitResolve = false;
    public boolean hitBindSingle = false;
    public boolean hitResolveSingle = false;
    public boolean eventHandlerNotNull = false;

    @Override
    public void startDocument(ValidationEventHandler eventHandler) throws SAXException {
        apples.clear();
        oranges.clear();
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
        if (obj instanceof Apple) {
            ((Apple) obj).processed = true;
            apples.put(idWrapper, (Apple) obj);
        } else {
            ((Orange) obj).processed = true;
            oranges.put(idWrapper, (Orange) obj);
        }
    }

    @Override
    public Callable<Object> resolve(final Map<String, Object> idWrapper, final Class type) throws SAXException {
        hitResolve = true;
        return new Callable<Object>() {
            public Object call() {
                if (type == Apple.class) {
                    return apples.get(idWrapper);
                } else {
                    return oranges.get(idWrapper);
                }
            }
        };
    }

    @Override
    public void bind(Object id, Object obj) throws SAXException {
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