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

import java.util.HashMap;
import java.util.Map;

public class DirectEntity1MMapHolder {

    private int id;
    private Map directToEntityMap = null;

    public DirectEntity1MMapHolder(){
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

    public void addDirectToEntityMapItem(Integer key, DEOTMMapValue value){
        directToEntityMap.put(key, value);
    }

    public void removeDirectToEntityMapItem(Integer key){
        directToEntityMap.remove(key);
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("DIR_ENT_1M_MAP_HOLDER");
        definition.addField("ID", java.math.BigDecimal.class, 15);

        return definition;
    }

}
