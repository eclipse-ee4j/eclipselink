/*******************************************************************************
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.tools.model;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IEclipseLinkSelectExpressionStateObjectBuilder extends ISelectExpressionStateObjectBuilder {

    /**
     * {@inheritDoc}
     */
    @Override
    IEclipseLinkSelectExpressionStateObjectBuilder append();

    /**
     * {@inheritDoc}
     */
    @Override
    IEclipseLinkSelectExpressionStateObjectBuilder new_(String className,
                                                        ISelectExpressionStateObjectBuilder... parameters);

    /**
     * {@inheritDoc}
     */
    @Override
    IEclipseLinkSelectExpressionStateObjectBuilder object(String identificationVariable);

    /**
     * {@inheritDoc}
     */
    @Override
    IEclipseLinkSelectExpressionStateObjectBuilder resultVariable(String resultVariable);

    /**
     * {@inheritDoc}
     */
    @Override
    IEclipseLinkSelectExpressionStateObjectBuilder resultVariableAs(String resultVariable);

    /**
     * {@inheritDoc}
     */
    @Override
    IEclipseLinkSelectExpressionStateObjectBuilder variable(String variable);
}
