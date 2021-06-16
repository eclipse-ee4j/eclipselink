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
//     Denise Smith - 2.6 - initial implementation
package org.eclipse.persistence.oxm.json;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import org.eclipse.persistence.internal.oxm.record.ExtendedResult;
import org.eclipse.persistence.oxm.record.JsonBuilderRecord;


public class JsonObjectBuilderResult extends ExtendedResult{

    private JsonObjectBuilder jsonObjectBuilder;

    public JsonObjectBuilderResult(){
        this.jsonObjectBuilder = Json.createObjectBuilder();
    }

    public JsonObjectBuilderResult(JsonObjectBuilder jsonObjectBuilder){
        this.jsonObjectBuilder = jsonObjectBuilder;
    }

    @Override
    public org.eclipse.persistence.oxm.record.MarshalRecord createRecord() {
         return new JsonBuilderRecord(jsonObjectBuilder);
    }

    public JsonObjectBuilder getJsonObjectBuilder() {
        return jsonObjectBuilder;
    }

}
