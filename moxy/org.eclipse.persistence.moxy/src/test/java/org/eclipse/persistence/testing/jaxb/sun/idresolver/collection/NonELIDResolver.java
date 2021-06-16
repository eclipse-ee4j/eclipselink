/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//  - rbarkhouse - 29 January 2013 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.sun.idresolver.collection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import jakarta.xml.bind.ValidationEventHandler;

import org.eclipse.persistence.testing.jaxb.idresolver.collection.TestObject;
import org.xml.sax.SAXException;

public class NonELIDResolver extends org.glassfish.jaxb.runtime.IDResolver {

    public boolean hitStartDocument = false;
    public boolean hitEndDocument = false;
    public boolean hitBind = false;
    public boolean hitResolve = false;
    public boolean eventHandlerNotNull = false;

    Map<String, Object> objects = new LinkedHashMap<String, Object>();

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
    public void bind(String id, Object obj) throws SAXException {
        hitBind = true;
        objects.put(id, obj);
    }

    @Override
    public Callable<?> resolve(final String id, Class type) throws SAXException {
        hitResolve = true;
        return new Callable<Object>() {
            public Object call() {
                Object obj = objects.get(id);
                ((TestObject) obj).processed = true;
                return obj;
            }
        };
    }

}
