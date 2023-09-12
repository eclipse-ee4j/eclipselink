/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.models.jpa.persistence32;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Persistence32TableCreator extends TogglingFastTableCreator {

    public Persistence32TableCreator() {
        setName("Persistence32Project");
        addTableDefinition(buildTypeTable());
        addTableDefinition(buildPokemonTable());
        addTableDefinition(buildPokemonTypeTable());
        addTableDefinition(buildSyntaxEntityTable());
    }

    public static TableDefinition buildTypeTable() {
        TableDefinition table = new TableDefinition();
        table.setName("PERSISTENCE32_TYPE");
        table.addField(createNumericPk("ID"));
        table.addField(createStringColumn("NAME", 64, false));
        return table;
    }

    public static TableDefinition buildPokemonTable() {
        TableDefinition table = new TableDefinition();
        table.setName("PERSISTENCE32_POKEMON");
        table.addField(createNumericPk("ID"));
        table.addField(createStringColumn("NAME", 64, false));
        return table;
    }

    public static TableDefinition buildPokemonTypeTable() {
        TableDefinition table = new TableDefinition();
        table.setName("PERSISTENCE32_POKEMON_TYPE");
        table.addField(createNumericFk("POKEMON_ID", "PERSISTENCE32_POKEMON.ID"));
        table.addField(createNumericFk("TYPE_ID", "PERSISTENCE32_TYPE.ID"));
        return table;
    }

    public static TableDefinition buildSyntaxEntityTable() {
        TableDefinition table = new TableDefinition();
        table.setName("PERSISTENCE32_SYNTAX_ENTITY");
        table.addField(createNumericPk("ID"));
        table.addField(createStringColumn("STR_VAL_1", 128, true));
        table.addField(createStringColumn("STR_VAL_2", 128, true));
        table.addField(createNumericColumn("INT_VAL", 15, true));
        table.addField(createTimeColumn("TIME_VAL", 3, true));
        table.addField(createDateColumn("DATE_VAL", 3, true));
        return table;
    }

}
