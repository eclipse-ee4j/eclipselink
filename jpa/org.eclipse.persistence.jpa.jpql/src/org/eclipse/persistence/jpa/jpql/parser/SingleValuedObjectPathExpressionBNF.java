/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for a simple valued object path expression.
 *
 * <div><b>BNF:</b> <code>single_valued_object_path_expression ::= general_identification_variable.{single_valued_object_field.}* single_valued_object_field</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class SingleValuedObjectPathExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "single_valued_object_path_expression";

    /**
     * Creates a new <code>SingleValuedObjectPathExpressionBNF</code>.
     */
    public SingleValuedObjectPathExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        registerChild(GeneralIdentificationVariableBNF.ID);
        registerChild(StateFieldPathExpressionBNF.ID);
    }
}
