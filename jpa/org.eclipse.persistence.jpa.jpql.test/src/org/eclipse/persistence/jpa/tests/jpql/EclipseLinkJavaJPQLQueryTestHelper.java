/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
