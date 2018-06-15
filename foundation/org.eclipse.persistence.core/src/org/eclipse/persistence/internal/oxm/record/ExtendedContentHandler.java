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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.internal.oxm.record;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * This class is used to introduce new events to the standard ContentHandler
 * interface that can be leveraged by MOXy.
 */
public interface ExtendedContentHandler extends ContentHandler {

    /*
     * This method is an alternate to the characters method on ContentHandler.
     * If this method is called other characters methods must not be called.
     */
    void characters(CharSequence characters) throws SAXException;

    /**
     * This method can be used to track that the current element is nil
     * @param isNil
     */
    void setNil(boolean isNil);

}
