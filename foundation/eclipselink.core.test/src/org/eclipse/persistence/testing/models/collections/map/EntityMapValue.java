/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial implementation
package org.eclipse.persistence.testing.models.collections.map;

public class EntityMapValue {

    private int id;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("ENT_MAP_VALUE");
        definition.addField("ID", java.math.BigDecimal.class, 15);
        definition.addField("HOLDER_ID", Integer.class, 15);
        definition.addField("MAP_KEY", Integer.class, 15);
        definition.addField("MAP_KEY_1", Integer.class, 15);
        definition.addField("KEY_ID", Integer.class, 15);
        definition.addForeignKeyConstraint("ENT_MAP_VALUE_U1M_AGG_ENT_HOLDER_FK", "HOLDER_ID", "ID", "AGG_ENT_U1M_MAP_HOLDER");
        definition.addForeignKeyConstraint("ENT_MAP_VALUE_U1M_DIR_ENT_HOLDER_FK", "HOLDER_ID", "ID", "DIR_ENT_U1M_MAP_HOLDER");
        definition.addForeignKeyConstraint("ENT_MAP_VALUE_U1M_ENT_ENT_HOLDER_FK", "HOLDER_ID", "ID", "ENT_ENT_U1M_MAP_HOLDER");
        definition.addForeignKeyConstraint("ENT_MAP_VALUE_U1M_ENT_ENT_HOLDER_KEY_FK", "KEY_ID", "ID", "ENT_MAP_KEY");

        return definition;
    }
}
