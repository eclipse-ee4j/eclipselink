/*
 * Copyright (c) 2024, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkAnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IdExpression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.VersionExpression;
import org.eclipse.persistence.mappings.DatabaseMapping;

import java.util.List;

/**
 * JPQL exclusive ID(), VERSION() functions/expressions are transformed there to StateFieldPathExpression.
 * It should be used in the future for another JPQL functions/expressions which are not available at the DB level.
 * E.g. For Entity e with idAttr as a primary key: <code>SELECT ID(e) FROM Entity e -> SELECT e.idAttr FROM Entity e</code>
 * For Entity e with versionAttr as a version attribute: <code>SELECT VERSION(e) FROM Entity e -> SELECT e.versionAttr FROM Entity e</code>
 *
 * @author Radek Felcman
 * @since 5.0
 */
public abstract class JPQLFunctionsAbstractBuilder extends EclipseLinkAnonymousExpressionVisitor {

    /**
     * The {@link JPQLQueryContext} is used to query information about the application metadata and
     * cached information.
     */
    final JPQLQueryContext queryContext;

    protected JPQLFunctionsAbstractBuilder(JPQLQueryContext queryContext) {
        this.queryContext = queryContext;
    }

    /**
     * For Entity e with idAttr as a primary key: <code>SELECT ID(e) FROM Entity e -> SELECT e.idAttr FROM Entity e</code>
     *
     * @param expression The {@link IdExpression} to visit
     */
    @Override
    public void visit(IdExpression expression) {
        //Fetch identification variable info
        IdentificationVariable identificationVariable = (IdentificationVariable) expression.getExpression();
        String variableText = identificationVariable.getText();
        String variableName = identificationVariable.getVariableName();

        //Get id attribute name
        ClassDescriptor descriptor = this.queryContext.getDeclaration(variableName).getDescriptor();
        List<DatabaseField> primaryKeyFields = descriptor.getPrimaryKeyFields();
        for (DatabaseField primaryKeyField : primaryKeyFields) {
            String idAttributeName = getIdAttributeNameByField(descriptor.getMappings(), primaryKeyField);
            StateFieldPathExpression stateFieldPathExpression = new StateFieldPathExpression(expression.getParent(), variableText + "." + idAttributeName);
            expression.addStateFieldPathExpression(stateFieldPathExpression);
        }
        //Continue with created StateFieldPathExpression
        //It's handled by ObjectBuilder booth @Id/primary key types (simple/composite)
        for (Expression fieldPathExpression: expression.getStateFieldPathExpressions()) {
            fieldPathExpression.accept(this);
        }
    }

    /**
     * For Entity e with versionAttr as a version attribute: <code>SELECT VERSION(e) FROM Entity e -> SELECT e.versionAttr FROM Entity e</code>
     *
     * @param expression The {@link VersionExpression} to visit
     */
    @Override
    public void visit(VersionExpression expression) {
        //Fetch identification variable info
        IdentificationVariable identificationVariable = (IdentificationVariable) expression.getExpression();
        String variableText = identificationVariable.getText();
        String variableName = identificationVariable.getVariableName();

        //Get version attribute name
        ClassDescriptor descriptor = this.queryContext.getDeclaration(variableName).getDescriptor();
        String versionAttributeName = ((VersionLockingPolicy) descriptor.getOptimisticLockingPolicy()).getVersionMapping().getAttributeName();
        StateFieldPathExpression stateFieldPathExpression = new StateFieldPathExpression(expression.getParent(), variableText + "." + versionAttributeName);
        expression.setStateFieldPathExpression(stateFieldPathExpression);

        //Continue with created StateFieldPathExpression
        expression.getStateFieldPathExpression().accept(this);
    }

    private String getIdAttributeNameByField(List<DatabaseMapping> databaseMappings, DatabaseField field) {
        for (DatabaseMapping mapping : databaseMappings) {
            if (mapping.getFields().size() > 1 && (field.equals(mapping.getField()) || mapping.isPrimaryKeyMapping())) {
                //handle @EmbeddedId (composite primary key usually by AggregateObjectMapping)
                return mapping.getAttributeName();
            } else if ((field.equals(mapping.getField()) && mapping.isPrimaryKeyMapping())) {
                //handle single @Id (simple primary key)
                return mapping.getAttributeName();
            } else if (field.equals(mapping.getField())) {
                //handle multiple @Id(s) with mapped @IdClass
                return mapping.getAttributeName();
            }
        }
        return null;
    }
}
