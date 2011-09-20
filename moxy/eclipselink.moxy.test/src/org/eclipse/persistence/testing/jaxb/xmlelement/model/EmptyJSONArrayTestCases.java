/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelement.model;

public class EmptyJSONArrayTestCases extends EmptyCollectionTestCases {

    protected final static String EMPTY_JSON_ARRAY_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/model/empty-json-array.json";

    public EmptyJSONArrayTestCases(String name) throws Exception {
        super(name);
        setControlJSON(EMPTY_JSON_ARRAY_RESOURCE);
        setWriteControlJSON(JSON_RESOURCE);
    }

}