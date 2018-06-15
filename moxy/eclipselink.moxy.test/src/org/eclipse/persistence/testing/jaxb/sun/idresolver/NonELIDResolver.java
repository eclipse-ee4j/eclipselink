/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 27 February - 2.3.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.sun.idresolver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.bind.ValidationEventHandler;

import org.xml.sax.SAXException;

public class NonELIDResolver extends com.sun.xml.bind.IDResolver{
    Map<String, Melon> melons = new HashMap<String, Melon>();

    public boolean hitStartDocument = false;
    public boolean hitEndDocument = false;
    public boolean hitBind = false;
    public boolean hitResolve = false;
    public boolean eventHandlerNotNull = false;

    public void startDocument(ValidationEventHandler eventHandler) throws SAXException {
        melons.clear();
        hitStartDocument = true;
        eventHandlerNotNull = (eventHandler != null);
    }

    public void endDocument() throws SAXException {
        hitEndDocument = true;
    }

    public void bind(String id, Object obj) throws SAXException {
        hitBind = true;
        if (obj instanceof Melon) {
            ((Melon) obj).processed = true;
            melons.put(id, (Melon) obj);
        }
    }

    public Callable<?> resolve(final String id, final Class type) throws SAXException {
        hitResolve = true;
        return new Callable<Object>() {
            public Object call() {
                if (type == Melon.class) {
                    return melons.get(id);
                } else {
                    return null;
                }
            }
        };
    }

}
