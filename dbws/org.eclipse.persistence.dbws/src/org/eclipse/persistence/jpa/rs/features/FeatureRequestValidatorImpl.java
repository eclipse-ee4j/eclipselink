package org.eclipse.persistence.jpa.rs.features;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

public class FeatureRequestValidatorImpl implements FeatureRequestValidator {

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureRequestValidator#isRequested(javax.ws.rs.core.UriInfo, java.util.Map)
     */
    @Override
    public boolean isRequested(UriInfo uri, Map<String, Object> additionalParams) {
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureRequestValidator#isRequestValid(javax.ws.rs.core.UriInfo, java.util.Map)
     */
    @Override
    public boolean isRequestValid(UriInfo uri, Map<String, Object> additionalParams) {
        return true;
    }

    protected Map<String, Object> getQueryParameters(UriInfo info) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        for (String key : info.getQueryParameters().keySet()) {
            queryParameters.put(key, info.getQueryParameters().getFirst(key));
        }
        return queryParameters;
    }
}
