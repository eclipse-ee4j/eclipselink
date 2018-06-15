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
//      gonural - Initial implementation
//      2014-09-01-2.6.0 Dmitry Kornilov
//        - added getMetadataSources, getDynamicMetadataSource and getSessionEventListener methods
package org.eclipse.persistence.jpa.rs.features;

import org.eclipse.persistence.internal.jaxb.SessionEventListener;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;

import java.util.List;

/**
 * This interface represents a set of JPARS features. Each service version has it's
 * own implementation of this interface.
 */
public interface FeatureSet {
    public enum Feature {
        /* Not pageable resources */
        NO_PAGING,

        /* Pageable resources */
        PAGING,

        /* Fields filtering (fields, excludeFields query parameters) */
        FIELDS_FILTERING
    }

    /**
     * Returns true if given feature is supported.
     *
     * @param feature Feature to check.
     * @return true if feature is supported, false if not supported.
     */
    boolean isSupported(Feature feature);

    /**
     * Returns an instance of {@link FeatureResponseBuilder} for given feature.
     *
     * @param feature feature to get response builder for.
     * @return {@link FeatureResponseBuilder}
     */
    FeatureResponseBuilder getResponseBuilder(Feature feature);

    /**
     * Gets a list of {@link MetadataSource} related to this version. Called on JAXB context
     * initialization.
     *
     * @return a list of {@link MetadataSource}
     */
    List<MetadataSource> getMetadataSources();

    /**
     * Builds a dynamic {@link MetadataSource} for given package. Called on JAXB context
     * initialization.
     *
     * @param session the session
     * @param packageName package name to build meta data for.
     *
     * @return {@link MetadataSource}
     */
    MetadataSource getDynamicMetadataSource(AbstractSession session, String packageName);

    /**
     * Returns {@link SessionEventListener} related to this version.
     *
     * @param session the session
     * @return {@link SessionEventListener}
     */
    SessionEventListener getSessionEventListener(AbstractSession session);
}
