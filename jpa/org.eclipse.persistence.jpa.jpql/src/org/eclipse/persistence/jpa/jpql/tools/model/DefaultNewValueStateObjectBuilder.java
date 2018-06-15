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
package org.eclipse.persistence.jpa.jpql.tools.model;

import org.eclipse.persistence.jpa.jpql.tools.model.query.UpdateItemStateObject;

/**
 * The default implementation of {@link INewValueStateObjectBuilder}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DefaultNewValueStateObjectBuilder extends AbstractNewValueStateObjectBuilder {

    /**
     * Creates a new <code>DefaultNewValueStateObjectBuilder</code>.
     *
     * @param parent The parent of the expression to build, which is only required when a JPQL
     * fragment needs to be parsed
     */
    public DefaultNewValueStateObjectBuilder(UpdateItemStateObject parent) {
        super(parent);
    }
}
