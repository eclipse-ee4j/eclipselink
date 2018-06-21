/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - October 26/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.bind.annotation.W3CDomHandler;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Element;

/**
 * This handler simply wraps a W3CDomHandler.    It will be used to test setting,
 * initialization and use of a DomHandlerConverter on an XMLAnyCollectionMapping.
 *
 */
public class MyDomHandler implements DomHandler {
    W3CDomHandler theHandler;

    public MyDomHandler() {
        theHandler = new W3CDomHandler();
    }

    public Result createUnmarshaller(ValidationEventHandler errorHandler) {
        return theHandler.createUnmarshaller(errorHandler);
    }

    public Object getElement(Result rt) {
        if (rt instanceof DOMResult) {
            return theHandler.getElement((DOMResult) rt);
        }
        return null;
    }

    public Source marshal(Object n, ValidationEventHandler errorHandler) {
        if (n instanceof Element) {
            return theHandler.marshal((Element) n, errorHandler);
        }
        return null;
    }
}
