package org.eclipse.persistence.jpa.rs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

/**
 * @author gonural
 *
 */

public abstract class AbstractResource {

    protected PersistenceContextFactory factory;

    public void setPersistenceFactory(PersistenceContextFactory factory) {
        this.factory = factory;
    }

    public PersistenceContextFactory getPersistenceFactory() {
        if (factory == null) {
            factory = buildPersistenceContextFactory();
        }
        return factory;
    }
    
    protected PersistenceContextFactory buildPersistenceContextFactory(){
        ServiceLoader<PersistenceContextFactoryProvider> contextFactoryLoader = ServiceLoader.load(PersistenceContextFactoryProvider.class);

        for (PersistenceContextFactoryProvider provider: contextFactoryLoader){
            PersistenceContextFactory factory = provider.getPersistenceContextFactory(null);
            if (factory != null){
                return factory;
            }
        }
        return null;
    }

    /**
     * This method has been temporarily added to allow processing of either
     * query or matrix parameters When the final protocol is worked out, it
     * should be removed or altered.
     * 
     * Here we check for query parameters and if they don't exist, we get the
     * matrix parameters.
     * 
     * @param info
     * @return
     */
    protected static Map<String, String> getParameterMap(UriInfo info, String segment) {
        Map<String, String> parameters = new HashMap<String, String>();
        for (PathSegment pathSegment : info.getPathSegments()) {
            if (pathSegment.getPath() != null && pathSegment.getPath().equals(segment)) {
                for (Entry<String, List<String>> entry : pathSegment.getMatrixParameters().entrySet()) {
                    parameters.put(entry.getKey(), entry.getValue().get(0));
                }
                return parameters;
            }
        }
        return parameters;
    }

    protected static Map<String, Object> getHintMap(UriInfo info) {
        Map<String, Object> hints = new HashMap<String, Object>();
        for (String key : info.getQueryParameters().keySet()) {
            hints.put(key, info.getQueryParameters().getFirst(key));
        }
        return hints;
    }

}

