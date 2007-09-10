/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.workbench.expressions;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;

/**
 * INTERNAL:
 * An MWQueryableArgument is always used as the left hand side of an MWBasicExpression.
 * It can also be used as the right hand side of an MWBinaryExpression.
 *
 * more here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public final class QueryableArgumentRepresentation extends ExpressionArgumentRepresentation {
    private String queryKey;
    private boolean isOuterJoin;
    private boolean isToMany;
    private QueryableArgumentRepresentation baseQueryKey;

    /**
     * Default constructor - for TopLink use only.
     */
    private QueryableArgumentRepresentation() {
        super();
    }

    public String displayString() {
        StringBuffer result = new StringBuffer();
        if (baseQueryKey != null) {
            result.append(baseQueryKey.displayString());
            result.append(".");
        }
        if (isToMany) {
            if (isOuterJoin) {
                result.append("anyOfAllowingNone(");
            } else {
                result.append("anyOf(");
            }
        } else {
            if (isOuterJoin) {
                result.append("getAllowingNull(");
            } else {
                result.append("get(");
            }
        }
        result.append("\"");
        result.append(queryKey);
        result.append("\"");
        result.append(")");

        return result.toString();
    }

    public boolean isQueryableArgument() {
        return true;
    }

    public String getQueryKeyName() {
        return this.queryKey;
    }

    /**
    * Is this is an outerjoin query key
    */
    public boolean isOuterJoin() {
        return this.isOuterJoin;
    }

    /**
     * Is this query key for a toMany relationship
     */
    public boolean isToMany() {
        return this.isToMany;
    }

    /**
     * Used for setting up a queryable argument which does not use joining
     */
    public void setQueryKeyName(String queryKeyName) {
        this.queryKey = queryKeyName;
    }

    /**
    * Used to set if this is an outerjoin query key
    */
    public void setIsOuterJoin(boolean outerJoin) {
        this.isOuterJoin = outerJoin;
    }

    /**
     * Used to set if this query key is for a toMany relationship
     */
    public void setIsToMany(boolean toMany) {
        this.isToMany = toMany;
    }

    //Conversion to Runtime
    public Expression convertToRuntime(Expression builder) {
        if (baseQueryKey != null) {
            builder = this.baseQueryKey.convertToRuntime(builder);
        }
        if (isToMany) {
            if (isOuterJoin) {
                builder = builder.anyOfAllowingNone(this.queryKey);
            } else {
                builder = builder.anyOf(this.queryKey);
            }
        } else {
            if (isOuterJoin) {
                builder = builder.getAllowingNull(queryKey);
            } else {
                builder = builder.get(queryKey);
            }
        }
        return builder;
    }

    public static QueryableArgumentRepresentation convertFromRuntime(QueryKeyExpression runtimeExpression) {
        QueryableArgumentRepresentation newArgument = new QueryableArgumentRepresentation();

        newArgument.setQueryKeyName(runtimeExpression.getName());
        newArgument.setIsToMany(runtimeExpression.shouldQueryToManyRelationship());
        newArgument.setIsOuterJoin(runtimeExpression.shouldUseOuterJoin());

        if (runtimeExpression.getBaseExpression().isQueryKeyExpression()) {
            newArgument.baseQueryKey = QueryableArgumentRepresentation.convertFromRuntime((QueryKeyExpression)runtimeExpression.getBaseExpression());
        }
        return newArgument;
    }

    public String convertToRuntimeString(String builderString) {
        return builderString + "." + displayString();
    }
}