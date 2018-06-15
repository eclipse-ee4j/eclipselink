/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//              ljungmann - initial implementation
package org.eclipse.persistence.testing.perf.jpa.tests.basic;

import java.util.Set;

import javax.persistence.Persistence;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.perf.jpa.model.metaannotations.MetaEmployee;
import org.eclipse.persistence.testing.perf.jpa.model.metaannotations.RegularEmployee;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * Benchmarks for JPA meta-data processing.
 *
 * @author lukas
 */
@State(Scope.Benchmark)
//@BenchmarkMode(Mode.AverageTime)
public class JPAMetadataProcessingTests {

    private ServerSession session;
    private Set<Class> entities;

    @Setup
    public void setup() {
        EntityManagerFactoryImpl emf = (EntityManagerFactoryImpl) Persistence.createEntityManagerFactory("jpa-performance");
        session = emf.getServerSession();
        entities = session.getDescriptors().keySet();
    }

    /**
     * Create JPA meta-data based on an existing sample model. For each class check
     * if it is an &#64;Entity and whether it contains &#64;EntityListeners annotation.
     */
    @Benchmark
    public void testMetadataProcessing() {
        MetadataAsmFactory factory = new MetadataAsmFactory(new MetadataLogger(session), JPAMetadataProcessingTests.class.getClassLoader());
        for (Class<?> javaClass : entities) {
            MetadataClass metadataClass = factory.getMetadataClass(javaClass.getName());
            PersistenceUnitProcessor.isEntity(metadataClass);
            metadataClass.getAnnotation("javax.persistence.EntityListeners");
        }
    }

    /**
     * Parse simple class, check if it is an &#64;Entity and whether it contains &#64;EntityListeners annotation.
     * Required annotations are defined directly on a parsed class.
     */
    @Benchmark
    public void testSimpleEntity() {
        MetadataAsmFactory factory = new MetadataAsmFactory(new MetadataLogger(null), JPAMetadataProcessingTests.class.getClassLoader());
        MetadataClass metadataClass = factory.getMetadataClass(RegularEmployee.class.getName());
        PersistenceUnitProcessor.isEntity(metadataClass);
        metadataClass.getAnnotation("javax.persistence.EntityListeners");
    }

    /**
     * Parse simple class, check if it is an &#64;Entity and whether it contains &#64;EntityListeners annotation.
     * Required annotations are defined on a meta-annotation used on a parsed class.
     */
    @Benchmark
    public void testMetaEntity() {
        MetadataAsmFactory factory = new MetadataAsmFactory(new MetadataLogger(null), JPAMetadataProcessingTests.class.getClassLoader());
        MetadataClass metadataClass = factory.getMetadataClass(MetaEmployee.class.getName());
        PersistenceUnitProcessor.isEntity(metadataClass);
        //found through meta-annotation
        metadataClass.getAnnotation("javax.persistence.EntityListeners");
    }

}
