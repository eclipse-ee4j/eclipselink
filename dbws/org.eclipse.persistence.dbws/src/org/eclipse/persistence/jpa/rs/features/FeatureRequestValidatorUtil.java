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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

public class FeatureRequestValidatorUtil {
    protected Map<String, Object> getQueryParameters(UriInfo info) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        for (String key : info.getQueryParameters().keySet()) {
            queryParameters.put(key, info.getQueryParameters().getFirst(key));
        }
        return queryParameters;
    }
}