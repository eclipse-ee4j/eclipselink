/*******************************************************************************
 * Copyright (c) 2013, 2014 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.features;

import org.eclipse.persistence.jpa.rs.features.core.selflinks.SelfLinksResponseBuilder;
import org.eclipse.persistence.jpa.rs.features.paging.PagingResponseBuilder;

/**
 * Feature set for service version 2.0.
 */
public class FeatureSetV2 implements FeatureSet {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupported(Feature feature) {
        switch (feature) {
            case NO_PAGING:
            case PAGING:
                return true;
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
            case PAGING:
                return new PagingResponseBuilder();
            case NO_PAGING:
            default:
                return new SelfLinksResponseBuilder();
        }
    }
}
