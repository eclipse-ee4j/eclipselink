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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.expressions;

import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.mappings.*;

public class NestedTable extends DatabaseTable {
    //the handle of the queryKey
    private QueryKeyExpression queryKeyExpression;

    public NestedTable() {
        super();
    }

    public NestedTable(QueryKeyExpression queryKeyExpression) {
        super();
        this.queryKeyExpression = queryKeyExpression;
        setName((queryKeyExpression.getMapping().getDescriptor().getTables().firstElement()).getName());
        tableQualifier = (queryKeyExpression.getMapping().getDescriptor().getTables().firstElement()).getQualifiedName();
    }

    /**
     * INTERNAL:
     */
    public String getQualifiedName(DatasourcePlatform platform) {
        return getQualifiedName(platform, false);
    }

    /**
     * INTERNAL:
     */
    public String getQualifiedNameDelimited(DatasourcePlatform platform) {
        return getQualifiedName(platform, true);
    }

    private String getQualifiedName(DatasourcePlatform platform, boolean allowDelimiters){
        if (qualifiedName == null) {
            // Print nested table using the TABLE function.
            DatabaseMapping mapping = queryKeyExpression.getMapping();
            DatabaseTable nestedTable = mapping.getDescriptor().getTables().firstElement();
            DatabaseTable tableAlias = queryKeyExpression.getBaseExpression().aliasForTable(nestedTable);

            StringBuilder name = new StringBuilder();
            name.append("TABLE(");
            if (allowDelimiters && useDelimiters){
                name.append(platform.getStartDelimiter());
            }
            name.append(tableAlias.getName());
            if (allowDelimiters && useDelimiters){
                name.append(platform.getEndDelimiter());
            }
            name.append(".");
            if (allowDelimiters && useDelimiters){
                name.append(platform.getStartDelimiter());
            }
            name.append(mapping.getField().getNameDelimited(platform));
            if (allowDelimiters && useDelimiters){
                name.append(platform.getEndDelimiter());
            }
            name.append(")");

            qualifiedName = name.toString();
        }

        return qualifiedName;
    }

    public QueryKeyExpression getQuerykeyExpression() {
        return queryKeyExpression;
    }

    public void setQuerykeyExpression(QueryKeyExpression queryKeyExpression) {
        this.queryKeyExpression = queryKeyExpression;
    }
}
