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

    @Override
    protected Class<?> acceptableType(String identifier) {
        return defaultAcceptableType(identifier);
    }

    @Override
    protected List<String> classNames() {
        return Collections.emptyList();
    }

    @Override
    protected List<String> columnNames(String tableName) {
        return Collections.emptyList();
    }

    @Override
    protected List<String> enumConstants() {
        return Collections.emptyList();
    }

    @Override
    protected List<String> enumTypes() {
        return Collections.emptyList();
    }

    @Override
    protected boolean isJoinFetchIdentifiable() {
        return false;
    }

    @Override
    protected List<String> tableNames() {
        return Collections.emptyList();
    }
}
