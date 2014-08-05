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

/**
 * This interface represents one feature of the JPARS. Features can be supported or not
 * supported by different versions of the service.
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
}