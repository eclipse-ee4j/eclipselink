/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.serializers.model;

import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.math.BigDecimal;

/**
 * @author Roman Grigoriadi
 */
public class CrateSerializerWithConversion extends CrateSerializer {

    @Override
    public void serialize(Crate obj, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();
        generator.write("crateStr", "REPLACED crate str");
        ctx.serialize("crateInner", obj.crateInner, generator);
        ctx.serialize("crateInnerList", obj.crateInnerList, generator);
        generator.write("crateBigDec", new BigDecimal("54321"));
        ctx.serialize("date-converted", obj.date, generator);
        generator.writeEnd();
    }
}
