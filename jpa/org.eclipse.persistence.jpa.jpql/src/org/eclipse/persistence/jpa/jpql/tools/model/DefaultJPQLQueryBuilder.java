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

/**
 * An implementation of {@link IJPQLQueryBuilder} that provides support based on the latest release
 * of the Java Persistence functional specification. The current version of the functional
 * specification is <a href="http://jcp.org/en/jsr/detail?id=317">JSR-337 Java Persistence 2.0</a>.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class DefaultJPQLQueryBuilder extends JPQLQueryBuilderWrapper {

    /**
     * Creates a new <code>DefaultJPQLQueryBuilder</code>.
     */
    public DefaultJPQLQueryBuilder() {
        super(new JPQLQueryBuilder2_0());
    }
}
