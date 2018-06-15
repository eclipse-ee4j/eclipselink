/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Gordon Yorke - Initial development
//
package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serializable;
import java.util.List;

import javax.persistence.criteria.Selection;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Selection interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: The Selection is the expression describing what should be returned by the query.
 * <p>
 *
 * @see javax.persistence.criteria Join
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */
public abstract class SelectionImpl<X> implements Selection<X>, InternalSelection, Serializable{

    protected Class<X> javaType;
    protected Expression currentNode;

    /**
     * Returns the current EclipseLink expression at this node in the criteria expression tree
     * @return the currentNode
     */
    @Override
    public Expression getCurrentNode() {
        return currentNode;
    }

    protected String alias;

    public <T> SelectionImpl(Class<X> javaType, Expression expressionNode){
        this.javaType = javaType;
        this.currentNode = expressionNode;
    }

    //SELECTION
    /**
     * Assign an alias to the selection.
     *
     * @param name
     *            alias
     */
    @Override
    public Selection<X> alias(String name) {
        this.alias = name;
        return this;
    }


    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public Class<? extends X> getJavaType() {
        return this.javaType;
    }
    /**
     * Return selection items composing a compound selection
     * @return list of selection items
     * @throws IllegalStateException if selection is not a compound
     *           selection
     */
    @Override
    public List<Selection<?>> getCompoundSelectionItems(){
        throw new IllegalStateException(ExceptionLocalization.buildMessage("CRITERIA_NOT_A_COMPOUND_SELECTION"));
    }

    /**
     * Whether the selection item is a compound selection
     * @return boolean
     */
    @Override
    public boolean isCompoundSelection(){
        return false;
    }

    @Override
    public boolean isFrom(){
        return false;
    }
    @Override
    public boolean isRoot(){
        return false;
    }
    @Override
    public boolean isConstructor(){
        return false;
    }


}
