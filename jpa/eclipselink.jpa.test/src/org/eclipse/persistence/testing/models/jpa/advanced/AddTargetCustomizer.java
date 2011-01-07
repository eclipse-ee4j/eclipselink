/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - test for bug 331692
 ******************************************************************************/  

package org.eclipse.persistence.testing.models.jpa.advanced;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.SQLUpdateStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

public class AddTargetCustomizer implements DescriptorCustomizer{

    public void customize(ClassDescriptor descriptor) {
        OneToManyMapping dealers = (OneToManyMapping)descriptor.getMappingForAttributeName("dealers");
        DataModifyQuery query = new DataModifyQuery();
        query.setName("CustomAddTargetQuery");
        SQLUpdateStatement statement = new SQLUpdateStatement();
        statement.setTable(descriptor.getPrimaryKeyFields().get(0).getTable());
        
        // Build where clause expression.
        Expression whereClause = null;
        Expression builder = new ExpressionBuilder();

        int size = dealers.getSourceKeyFields().size();
        for (int index = 0; index < size; index++) {
            DatabaseField key = dealers.getSourceKeyFields().get(index);
            Expression expression = builder.getField(key).equal(builder.getParameter(dealers.getTargetForeignKeyFields().get(index)));
            whereClause = expression.and(whereClause);
        }
        
        statement.setWhereClause(whereClause);
        AbstractRecord modifyRow = new DatabaseRecord();
        modifyRow.add(descriptor.getMappingForAttributeName("firstName").getField(), null);
        statement.setModifyRow(modifyRow);
        query.setSQLStatement(statement);
        dealers.setCustomAddTargetQuery(query);
    }
}
