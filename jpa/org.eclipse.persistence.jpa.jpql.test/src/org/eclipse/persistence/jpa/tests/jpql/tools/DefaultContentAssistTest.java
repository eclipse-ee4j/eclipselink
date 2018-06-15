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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.jpa.tests.jpql.UniqueSignature;

/**
 * This unit-test tests the JPQL content assist at various position within the JPQL query and with
 * complete and incomplete queries using the default (generic) JPA support.
 *
 * @version 2.5.1
 * @since 2.4
 * @author Pascal Filion
 */
@UniqueSignature
public final class DefaultContentAssistTest extends AbstractContentAssistTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> acceptableType(String identifier) {
        return defaultAcceptableType(identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> classNames() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> columnNames(String tableName) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> enumConstants() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> enumTypes() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isJoinFetchIdentifiable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> tableNames() {
        return Collections.emptyList();
    }
}
