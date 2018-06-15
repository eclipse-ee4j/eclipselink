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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistExtension;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistProposals.ClassType;
import org.eclipse.persistence.jpa.tests.jpql.UniqueSignature;

/**
 * @version 2.5.1
 * @since 2.5
 * @author Pascal Filion
 */
@UniqueSignature
public final class DefaultDefaultContentAssistExtensionTest extends AbstractContentAssistTest {

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
    protected ContentAssistExtension buildContentAssistExtension() {
        return new ContentAssistExtension() {
            public Iterable<String> classNames(String prefix, ClassType type) {
                if (type == ClassType.INSTANTIABLE) {
                    return filter(DefaultDefaultContentAssistExtensionTest.this.classNames(), prefix);
                }
                return filter(DefaultDefaultContentAssistExtensionTest.this.enumTypes(), prefix);
            }
            public Iterable<String> columnNames(String tableName, String prefix) {
                return Collections.emptyList();
            }
            public Iterable<String> tableNames(String prefix) {
                return Collections.emptyList();
            }
        };
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
