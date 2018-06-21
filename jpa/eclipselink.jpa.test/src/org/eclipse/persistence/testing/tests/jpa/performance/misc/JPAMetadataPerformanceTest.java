/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     James Sutherland (Oracle) - initial API and implementation
 package org.eclipse.persistence.testing.tests.jpa.performance.misc;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of metadata processing.
 */
public class JPAMetadataPerformanceTest extends PerformanceRegressionTestCase {

    public JPAMetadataPerformanceTest() {
        setDescription("This tests the JPA metadata processing.");
    }

    /**
     * Rebuild EntityManagerFactory.
     */
    public void test() throws Exception {
        MetadataAsmFactory factory = new MetadataAsmFactory(new MetadataLogger(getAbstractSession()), getClass().getClassLoader());
        for (Class javaClass : ((EntityManagerFactoryImpl)getExecutor().getEntityManagerFactory()).getServerSession().getDescriptors().keySet()) {
            factory.getMetadataClass(javaClass.getName());
        }
    }
}
