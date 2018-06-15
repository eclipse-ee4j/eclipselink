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
//      gonural - initial implementation
//      2014-09-01-2.6.0 Dmitry Kornilov
//        - JPARS 2.0 related changes
package org.eclipse.persistence.jpa.rs.features;

import org.eclipse.persistence.internal.jaxb.SessionEventListener;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jpa.rs.util.PreLoginMappingAdapter;
import org.eclipse.persistence.jpa.rs.util.metadatasources.DynamicXMLMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.ErrorResponseMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.ItemLinksMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.JavaLangMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.JavaMathMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.JavaUtilMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.LinkMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.LinkV2MetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.ReadAllQueryResultCollectionMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.ReportQueryResultCollectionMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.ReportQueryResultListItemMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.ReportQueryResultListMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.SimpleHomogeneousListMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.SingleResultQueryListMetadataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * The legacy initial feature set. Used if version number is not present or equal to 1.
 *
 * @author gonural, Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
public class FeatureSetPreV2 implements FeatureSet {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupported(Feature feature) {
        switch (feature) {
            case NO_PAGING:
                return true;
            case PAGING:
            default:
                return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureResponseBuilder getResponseBuilder(Feature feature) {
        switch (feature) {
            case NO_PAGING:
            case PAGING:
            default:
                return new FeatureResponseBuilderImpl();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MetadataSource> getMetadataSources() {
        final List<MetadataSource> metadataSources = new ArrayList<MetadataSource>();

        metadataSources.add(new LinkMetadataSource());
        metadataSources.add(new ReportQueryResultListMetadataSource());
        metadataSources.add(new ReportQueryResultListItemMetadataSource());
        metadataSources.add(new SingleResultQueryListMetadataSource());
        metadataSources.add(new SimpleHomogeneousListMetadataSource());
        metadataSources.add(new ReportQueryResultCollectionMetadataSource());
        metadataSources.add(new ReadAllQueryResultCollectionMetadataSource());

        metadataSources.add(new JavaLangMetadataSource());
        metadataSources.add(new JavaMathMetadataSource());
        metadataSources.add(new JavaUtilMetadataSource());
        metadataSources.add(new LinkV2MetadataSource());
        metadataSources.add(new ItemLinksMetadataSource());
        metadataSources.add(new ErrorResponseMetadataSource());

        return metadataSources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetadataSource getDynamicMetadataSource(AbstractSession session, String packageName) {
        return new DynamicXMLMetadataSource(session, packageName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SessionEventListener getSessionEventListener(AbstractSession session) {
        return new PreLoginMappingAdapter(session);
    }
}
