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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement.model;

public class EmptyJSONArrayTestCases extends EmptyCollectionTestCases {

    protected final static String EMPTY_JSON_ARRAY_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/model/empty-json-array.json";

    public EmptyJSONArrayTestCases(String name) throws Exception {
        super(name);
        setControlJSON(EMPTY_JSON_ARRAY_RESOURCE);
        setWriteControlJSON(JSON_RESOURCE);
    }

}
