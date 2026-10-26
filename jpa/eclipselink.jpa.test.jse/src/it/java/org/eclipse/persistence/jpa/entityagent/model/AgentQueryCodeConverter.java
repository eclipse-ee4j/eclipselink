/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.entityagent.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AgentQueryCodeConverter implements AttributeConverter<AgentQueryCode, String> {
    public static int conversions;

    @Override
    public String convertToDatabaseColumn(AgentQueryCode value) {
        conversions++;
        return value == null ? null : value.value();
    }

    @Override
    public AgentQueryCode convertToEntityAttribute(String value) {
        return value == null ? null : new AgentQueryCode(value);
    }
}
