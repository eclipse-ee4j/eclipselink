/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.collections.map;

import java.util.HashMap;
import java.util.Map;

public class DirectEntityU1MMapHolder  {

    private int id;
    private Map directToEntityMap = null;

    public DirectEntityU1MMapHolder(){
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

        definition.setName("DIR_ENT_U1M_MAP_HOLDER");
        definition.addField("ID", java.math.BigDecimal.class, 15);

        return definition;
    }

}
