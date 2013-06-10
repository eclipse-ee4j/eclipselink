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

import java.util.Map;

import javax.ws.rs.core.UriInfo;

public interface FeatureRequestValidator {

    /**
     * Checks if feature is requested.
     *
     * @param uri the uri
     * @param additionalParams the additional params
     * @return true, if is requested
     */
    boolean isRequested(UriInfo uri, Map<String, Object> additionalParams);

    /**
     * Checks if request parameters are valid.
     *
     * @param uri the uri
     * @param additionalParams the additional params
     * @return true, if is request valid
     */
    boolean isRequestValid(UriInfo uri, Map<String, Object> additionalParams);
}