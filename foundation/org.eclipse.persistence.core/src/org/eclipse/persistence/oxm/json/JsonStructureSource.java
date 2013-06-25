/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm.json;

import javax.json.JsonStructure;

import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.record.ExtendedSource;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.json.JsonStructureReader;

/**
 *   Object to be used with Unmarshaller to unmarshal javax.json.JsonStructure objects
 *   (ie: javax.json.JsonObject or javax.json.JsonArray)
 *
 *   Usage:
 *      JsonStructureSource source = new JsonStructureSource(jsonObject);
 *      Object unmarshalled = jaxbUnmarshaller.unmarshal(source);
 *    
 */
public class JsonStructureSource extends ExtendedSource {

    private JsonStructure jsonStructure;

    public JsonStructureSource(JsonStructure jsonStructure) {
        this.jsonStructure = jsonStructure;
    }

    public JsonStructure getJsonStructure() {
        return jsonStructure;
    }

    @Override
    public XMLReader createReader(Unmarshaller unmarshaller) {
        JsonStructureReader reader = new JsonStructureReader(unmarshaller);            
        reader.setJsonStructure(getJsonStructure());
        return reader;
    }

    @Override
    public XMLReader createReader(Unmarshaller unmarshaller, Class unmarshalClass) {
        JsonStructureReader reader = new JsonStructureReader(unmarshaller, unmarshalClass);            
        reader.setJsonStructure(getJsonStructure());
        return reader;
    }

}
