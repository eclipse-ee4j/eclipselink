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

import javax.json.stream.JsonGenerator;

import org.eclipse.persistence.internal.oxm.record.ExtendedResult;
import org.eclipse.persistence.oxm.record.JsonGeneratorRecord;

public class JsonGeneratorResult extends ExtendedResult{

    private JsonGenerator generator;
    private String rootKeyName;
    
    public JsonGeneratorResult(JsonGenerator generator){
        this.generator = generator;
        rootKeyName = null;
    }
    
    public JsonGeneratorResult(JsonGenerator generator, String rootKeyName){
        this.generator = generator;
        this.rootKeyName = rootKeyName;
    }

    @Override
    public org.eclipse.persistence.oxm.record.MarshalRecord createRecord() {
        JsonGeneratorRecord record = new JsonGeneratorRecord(generator, rootKeyName);
        return record;
    }    

}
