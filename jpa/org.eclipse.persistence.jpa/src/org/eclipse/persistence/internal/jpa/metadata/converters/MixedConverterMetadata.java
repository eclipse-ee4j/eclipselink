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
//     10/25/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
package org.eclipse.persistence.internal.jpa.metadata.converters;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ConverterAccessor;

/**
 * INTERNAL:
 * Object to hold onto XML converter metadata. This metadata is either for
 * a ConverterMetadata object or a ConverterAccessor.
 *
 * Key notes:
 * - methods should be preserved in alphabetical order.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5
 */
public class MixedConverterMetadata {
    private String m_name;
    protected String className;
    protected Boolean autoApply;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public MixedConverterMetadata() {}

    /**
     * INTERNAL:
     * Build a converter accessor from this metadata.
     */
    public ConverterAccessor buildConverterAccessor() {
        ConverterAccessor converterAccessor = new ConverterAccessor();
        converterAccessor.setAutoApply(getAutoApply());
        converterAccessor.setClassName(getClassName());
        return converterAccessor;
    }

    /**
     * INTERNAL:
     * Build a converter metadata from this metadata.
     */
    public ConverterMetadata buildConverterMetadata() {
        ConverterMetadata converterMetadata = new ConverterMetadata();
        converterMetadata.setName(getName());
        converterMetadata.setClassName(getClassName());
        return converterMetadata;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getAutoApply() {
        return autoApply;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getClassName() {
        return className;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }

    /**
     * INTERNAL:
     * Return true if this metadata has a name specified.
     */
    protected boolean hasName() {
        return m_name != null && ! m_name.equals("");
    }

    /**
     * INTERNAL:
     * If no name is specified, assume JPA converter class.
     */
    public boolean isConverterAccessor() {
        return ! hasName();
    }

    /**
     * INTERNAL:
     * If name is specified, assume EclipseLink converter.
     */
    public boolean isConverterMetadata() {
        return hasName();
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAutoApply(Boolean autoApply) {
        this.autoApply = autoApply;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
}
