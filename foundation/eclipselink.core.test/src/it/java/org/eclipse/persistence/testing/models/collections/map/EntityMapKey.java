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

public class EntityMapKey {

    private int id;
    private String data;

    public String getData(){
        return data;
    }

    public int getId(){
        return id;
    }

    public void setData(String data){
        this.data = data;
    }

    public void setId(int id){
        this.id = id;
    }

    public boolean equals(Object object){
        if (!(object instanceof EntityMapKey)){
            return false;
        } else {
            return ((EntityMapKey)object).getId() == this.id;
        }
    }

    public int hashCode(){
        return id;
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("ENT_MAP_KEY");
        definition.addField("ID", java.lang.Integer.class, 15);
        definition.addField("DATA", String.class, 15);

        return definition;
    }

}
