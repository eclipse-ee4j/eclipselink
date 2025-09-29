/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_SEQUENCE_GENERATOR;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_TABLE_GENERATOR;

/**
 * An package-info accessor.
 */
public class PackageAccessor extends MetadataAccessor {
    private boolean m_isPreProcessed = false;
    private boolean m_isProcessed = false;

    private SequenceGeneratorMetadata m_sequenceGenerator;
    private TableGeneratorMetadata m_tableGenerator;

    /**
     * INTERNAL:
     */
    public PackageAccessor(MetadataAnnotation annotation, MetadataClass cls, MetadataProject project) {
        super(annotation, cls, new MetadataDescriptor(cls), project);
    }

    public String getName() {
        return getAccessibleObjectName();
    }

    /**
     * INTERNAL:
     * Return true if this accessor has been pre-processed.
     */
    public boolean isPreProcessed() {
        return m_isPreProcessed;
    }

    /**
     * INTERNAL:
     * The pre-process method is called during regular deployment and metadata
     * processing and will pre-process the items of interest on an package-info.
     */
    public void preProcess() {

        this.getAccessibleObject().getAnnotations().values().forEach(annotation -> {
            // Set the sequence generator if one is present.
            if (isAnnotationPresent(JPA_SEQUENCE_GENERATOR)) {
                m_sequenceGenerator = new SequenceGeneratorMetadata(getAnnotation(JPA_SEQUENCE_GENERATOR), this);
            }

            // Set the table generator if one is present.
            if (isAnnotationPresent(JPA_TABLE_GENERATOR)) {
                m_tableGenerator = new TableGeneratorMetadata(getAnnotation(JPA_TABLE_GENERATOR), this);
            }
        });
        m_isPreProcessed = true;
    }

    protected MetadataAnnotation getAnnotation(String annotation) {
        return this.getAccessibleObject().getAnnotation(annotation);
    }

    public boolean isAnnotationPresent(String annotation) {
        return this.getAccessibleObject().isAnnotationPresent(annotation);
    }

    @Override
    public boolean isProcessed() {
        return m_isProcessed;
    }

    @Override
    public void process() {
        // Add the table generator to the project if one is set.
        if (m_tableGenerator != null) {
            getProject().addTableGenerator(m_tableGenerator, getDescriptor().getDefaultCatalog(), getDescriptor().getDefaultSchema(), getName());
        }

        // Add the sequence generator to the project if one is set.
        if (m_sequenceGenerator != null) {
            getProject().addSequenceGenerator(m_sequenceGenerator, getDescriptor().getDefaultCatalog(), getDescriptor().getDefaultSchema(), getName());
        }

        m_isProcessed = true;
    }

    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof PackageAccessor accessor) {
            return valuesMatch(getName(), accessor.getName());
        }
        return false;
    }
}
