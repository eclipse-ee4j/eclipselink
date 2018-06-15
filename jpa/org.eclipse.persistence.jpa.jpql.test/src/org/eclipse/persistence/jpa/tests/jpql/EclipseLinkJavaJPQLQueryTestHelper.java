/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql;

import java.lang.reflect.Member;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMappingBuilder;
import org.eclipse.persistence.jpa.tests.jpql.tools.spi.java.EclipseLinkMappingBuilder;

/**
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class EclipseLinkJavaJPQLQueryTestHelper extends JavaJPQLQueryTestHelper {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IMappingBuilder<Member> buildMappingBuilder() {
        return new EclipseLinkMappingBuilder();
    }
}
