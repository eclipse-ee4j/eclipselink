/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries.options;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;

public class ReadObjectQueryDisableCacheHitsTest extends AutoVerifyTestCase {
    ClassDescriptor m_addressDesc;
    ClassDescriptor m_policyHolderDesc;
    boolean m_addressDisableCacheHits;
    boolean m_addressAlwaysrefreshCache;
    boolean m_policyHolderDisableCacheHits;
    boolean m_policyHolderAlwaysrefreshCache;

    public ReadObjectQueryDisableCacheHitsTest() {
        super();
        setDescription("This test verifies that infinite recursion will not occur when reading an object with no cascade all, no indirection and disabled cache hits");
    }

    public void reset() {
        m_addressDesc.setShouldDisableCacheHits(m_addressDisableCacheHits);
        m_addressDesc.setShouldAlwaysRefreshCache(m_addressAlwaysrefreshCache);
        m_policyHolderDesc.setShouldDisableCacheHits(m_policyHolderDisableCacheHits);
        m_policyHolderDesc.setShouldAlwaysRefreshCache(m_policyHolderAlwaysrefreshCache);
    }

    public void setup() {
        m_addressDesc = getSession().getClassDescriptor(org.eclipse.persistence.testing.models.insurance.Address.class);
        m_addressDisableCacheHits = m_addressDesc.shouldDisableCacheHits();
        m_addressAlwaysrefreshCache = m_addressDesc.shouldAlwaysRefreshCache();
        m_addressDesc.disableCacheHits();
        m_addressDesc.alwaysRefreshCache();

        m_policyHolderDesc = getSession().getClassDescriptor(org.eclipse.persistence.testing.models.insurance.PolicyHolder.class);
        m_policyHolderDisableCacheHits = m_policyHolderDesc.shouldDisableCacheHits();
        m_policyHolderAlwaysrefreshCache = m_policyHolderDesc.shouldAlwaysRefreshCache();
        m_policyHolderDesc.disableCacheHits();
        m_policyHolderDesc.alwaysRefreshCache();
    }

    public void test() {
        try {
            ReadObjectQuery query = new ReadObjectQuery(org.eclipse.persistence.testing.models.insurance.PolicyHolder.class);
            getSession().executeQuery(query);
        } catch (StackOverflowError ex) {
            throw new TestErrorException("ReadObjectQuery with disabled cache hits caused infinite recursion");
        }
    }
}
