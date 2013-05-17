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
package org.eclipse.persistence.jpa.rs.features.generic;

import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.eclipse.persistence.jpa.rs.features.FeatureRequestValidator;
import org.eclipse.persistence.jpa.rs.features.FeatureRequestValidatorUtil;

public class GenericRequestValidator extends FeatureRequestValidatorUtil implements FeatureRequestValidator {

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureRequestValidator#isRequestValid(javax.ws.rs.core.UriInfo, java.util.Map)
     */
    @Override
    public boolean isRequestValid(UriInfo uri, Map<String, Object> additionalParams) {
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureRequestValidator#isRequested(javax.ws.rs.core.UriInfo, java.util.Map)
     */
    @Override
    public boolean isRequested(UriInfo uri, Map<String, Object> additionalParams) {
        return true;
    }
}
