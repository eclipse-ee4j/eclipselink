/*******************************************************************************
 * Copyright (c) 2013 Oracle. All rights reserved.
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

public class FeatureSetPreV2 implements FeatureSet {

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureSet#isSupported(org.eclipse.persistence.jpa.rs.features.FeatureSet.Feature)
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

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureSet#getRequestValidator(org.eclipse.persistence.jpa.rs.features.FeatureSet.Feature)
     */
    @Override
    public FeatureRequestValidator getRequestValidator(Feature feature) {
        switch (feature) {
            case NO_PAGING:
            case PAGING:
            default:
                return new FeatureRequestValidatorImpl();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureSet#getResponseBuilder(org.eclipse.persistence.jpa.rs.features.FeatureSet.Feature)
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
}