/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.mapping;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * Identification holds only it's primary key
 * This class is designed to have an attribute with the same name as it's owner: SecureSystem
 *
 */
public class Identification {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_IDENTIFICATION");

        definition.addField("ID", java.math.BigDecimal.class, 15);
        return definition;
    }
}
