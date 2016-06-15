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

package org.eclipse.persistence.json.bind.customization;

import org.eclipse.persistence.json.bind.customization.model.InterfacedPojoB;
import org.eclipse.persistence.json.bind.customization.model.InterfacedPojoImpl;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import static org.junit.Assert.assertEquals;

/**
 * @author Roman Grigoriadi
 */
public class InterfaceAnnotationsTest {

    @Test
    public void testJsonbPropertyIfcInheritance() {
        InterfacedPojoB pojo = new InterfacedPojoImpl();
        pojo.setPropertyA("AA");
        pojo.setPropertyB("BB");

        final Jsonb jsonb = JsonbBuilder.create();
        final String json = "{\"propA\":\"AA\",\"propB\":\"BB\"}";
        assertEquals(json, jsonb.toJson(pojo));
    }


}
