/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial implementation
package org.eclipse.persistence.testing.models.collections.map;

import java.util.Map;
import java.util.HashMap;

public class DirectEntityMapHolder {

    private int id;
    private Map directToEntityMap = null;

    public DirectEntityMapHolder(){
        directToEntityMap = new HashMap();
    }

    public Map getDirectToEntityMap(){
        return directToEntityMap;
    }

    public int getId(){
        return id;
    }

    public void setDirectToEntityMap(Map map){
        directToEntityMap = map;
    }

    public void setId(int id){
        this.id = id;
    }

    public void addDirectToEntityMapItem(Integer key, EntityMapValue value){
        directToEntityMap.put(key, value);
    }

    public void removeDirectToEntityMapItem(Integer key){
        directToEntityMap.remove(key);
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("DIR_ENT_MAP_HOLDER");
        definition.addField("ID", java.math.BigDecimal.class, 15);

        return definition;
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition relationTableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("DIR_ENT_MAP_REL");
        definition.addField("HOLDER_ID", java.math.BigDecimal.class, 15);
        definition.addField("VALUE_ID", java.math.BigDecimal.class, 15);
        definition.addField("MAP_KEY", Integer.class, 15);
        definition.addForeignKeyConstraint("DIR_ENT_MAP_REL_FK", "HOLDER_ID", "ID", "DIR_ENT_MAP_HOLDER");
        definition.addForeignKeyConstraint("DIR_ENT_MAP_REL_VALUE_FK", "VALUE_ID", "ID", "ENT_MAP_VALUE");

        return definition;
    }
}
