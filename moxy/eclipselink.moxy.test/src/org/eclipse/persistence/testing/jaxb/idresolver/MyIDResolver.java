/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
    Map<String, Apple> apples = new LinkedHashMap<String, Apple>();
    Map<String, Orange> oranges = new LinkedHashMap<String, Orange>();

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
        String id = idWrapper.values().toArray()[0].toString();
        if (obj instanceof Apple) {
            ((Apple) obj).processed = true;
            apples.put(id, (Apple) obj);
        } else {
            ((Orange) obj).processed = true;
            oranges.put(id, (Orange) obj);
        }
    }

    @Override
    public Callable<Object> resolve(Map<String, Object> idWrapper, final Class type) throws SAXException {
        hitResolve = true;
        final String id = idWrapper.values().toArray()[0].toString();
        return new Callable<Object>() {
            public Object call() {
                if (type == Apple.class) {
                    return apples.get(id);
                } else {
                    return oranges.get(id);
                }
            }
        };
    }

    @Override
    public void bind(Object id, Object obj) throws SAXException {
        hitBindSingle = true;
        HashMap<String, Object> idMap = new HashMap<String, Object>(1);
        idMap.put("stringId", id);
        bind(idMap, obj);
    }

    @Override
    public Callable<?> resolve(Object id, Class type) throws SAXException {
        hitResolveSingle = true;
        HashMap<String, Object> idMap = new HashMap<String, Object>(1);
        idMap.put("stringId", id);
        return resolve(idMap, type);
    }

}