/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.model;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IEclipseLinkSelectExpressionStateObjectBuilder extends ISelectExpressionStateObjectBuilder {

    @Override
    IEclipseLinkSelectExpressionStateObjectBuilder append();

    @Override
    IEclipseLinkSelectExpressionStateObjectBuilder new_(String className,
                                                        ISelectExpressionStateObjectBuilder... parameters);

    @Override
    IEclipseLinkSelectExpressionStateObjectBuilder object(String identificationVariable);

    @Override
    IEclipseLinkSelectExpressionStateObjectBuilder resultVariable(String resultVariable);

    @Override
    IEclipseLinkSelectExpressionStateObjectBuilder resultVariableAs(String resultVariable);

    @Override
    IEclipseLinkSelectExpressionStateObjectBuilder variable(String variable);
}
