/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools;

import org.eclipse.persistence.jpa.jpql.AbstractEclipseLinkSemanticValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkSemanticValidatorExtension;
import org.eclipse.persistence.jpa.jpql.SemanticValidatorHelper;

/**
 * This validator is responsible to gather the problems found in a JPQL query by validating the
 * content to make sure it is semantically valid for EclipseLink. The grammar is not validated by
 * this visitor.
 * <p>
 * For instance, the function <b>AVG</b> accepts a state field path. The property it represents has
 * to be of numeric type. <b>AVG(e.name)</b> is parsable but is not semantically valid because the
 * type of name is a string (the property signature is: "<code>private String name</code>").
 * <p>
 * <b>Note:</b> EclipseLink does not validate types, but leaves it to the database. This is because
 * some databases such as Oracle allow different types to different functions and perform implicit
 * type conversion. i.e. <code>CONCAT('test', 2)</code> returns <code>'test2'</code>. Also the
 * <b>FUNC</b> function has an unknown type, so should be allowed with any function.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see org.eclipse.persistence.jpa.jpql.EclipseLinkGrammarValidator EclipseLinkGrammarValidator
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkSemanticValidator extends AbstractEclipseLinkSemanticValidator {

    /**
     * Creates a new <code>EclipseLinkSemanticValidator</code>.
     *
     * @param queryContext The context used to query information about the JPQL query
     * @exception NullPointerException The given {@link JPQLQueryContext} cannot be <code>null</code>
     * @deprecated Use {@link EclipseLinkSemanticValidator#EclipseLinkSemanticValidator(JPQLQueryContext, EclipseLinkSemanticValidatorExtension)}
     */
    @Deprecated
    public EclipseLinkSemanticValidator(JPQLQueryContext queryContext) {
        this(new GenericSemanticValidatorHelper(queryContext), EclipseLinkSemanticValidatorExtension.NULL_EXTENSION);
    }

    /**
     * Creates a new <code>EclipseLinkSemanticValidator</code>.
     *
     * @param queryContext The context used to query information about the JPQL query
     * @param extension The following extension can be used to give access to non-JPA metadata
     * artifacts, such as database tables and columns
     * @exception NullPointerException The given {@link JPQLQueryContext} cannot be <code>null</code>
     */
    public EclipseLinkSemanticValidator(JPQLQueryContext queryContext,
                                        EclipseLinkSemanticValidatorExtension extension) {

        this(new GenericSemanticValidatorHelper(queryContext), extension);
    }

    /**
     * Creates a new <code>EclipseLinkSemanticValidator</code>.
     *
     * @param helper The given helper allows this validator to access the JPA artifacts without using
     * Hermes SPI
     * @param extension The following extension can be used to give access to non-JPA metadata
     * artifacts, such as database tables and columns
     * @exception NullPointerException The given {@link SemanticValidatorHelper} cannot be <code>null</code>
     */
    public EclipseLinkSemanticValidator(SemanticValidatorHelper helper,
                                        EclipseLinkSemanticValidatorExtension extension) {

        super(helper, extension);
    }
}
