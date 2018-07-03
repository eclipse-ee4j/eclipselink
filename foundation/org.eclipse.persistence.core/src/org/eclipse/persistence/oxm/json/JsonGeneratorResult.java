/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.6 - initial implementation
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
