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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model.query;

/**
 * This visitor allows a subclass to simply override {@link #visit(StateObject)} and perform the
 * same task for all visited {@link StateObject ItateObjects}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AnonynousEclipseLinkStateObjectVisitor extends AnonymousStateObjectVisitor
                                                             implements EclipseLinkStateObjectVisitor {

}
