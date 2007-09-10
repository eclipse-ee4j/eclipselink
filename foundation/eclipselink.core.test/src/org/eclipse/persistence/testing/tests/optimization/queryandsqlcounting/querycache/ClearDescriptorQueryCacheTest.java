/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting.querycache;

public class ClearDescriptorQueryCacheTest extends ClearQueryResultsQueryCacheTest {
    public ClearDescriptorQueryCacheTest() {
        super(NamedQueryQueryCacheTest.QUERY_ON_DESCRIPTOR);
        setDescription("Test the clearQueryResults on queries that are stored on the descriptor.");
    }
}