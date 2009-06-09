/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     		     Specification available from http://jcp.org/en/jsr/detail?id=317
 *
 * Java(TM) Persistence API, Version 2.0 - EARLY ACCESS
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP).  The code is untested and presumed not to be a  
 * compatible implementation of JSR 317: Java(TM) Persistence API, Version 2.0.   
 * We encourage you to migrate to an implementation of the Java(TM) Persistence 
 * API, Version 2.0 Specification that has been tested and verified to be compatible 
 * as soon as such an implementation is available, and we encourage you to retain 
 * this notice in any implementation of Java(TM) Persistence API, Version 2.0 
 * Specification that you distribute.
 ******************************************************************************/
package javax.persistence.spi;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

/**
 * Interface implemented by the persistence provider.
 * 
 * It is invoked by the container in Java EE environments and by the Persistence
 * class in Java SE environments to create an EntityManagerFactory.
 * 
 * It is also invoked by the PersistenceUtil implementation to determine the
 * load status of an entity or entity attribute.
 */
public interface PersistenceProvider {
    /**
     * Called by Persistence class when an EntityManagerFactory is to be
     * created.
     * 
     * @param emName
     *            The name of the persistence unit
     * @param map
     *            A Map of properties for use by the persistence provider. These
     *            properties may be used to override the values of the
     *            corresponding elements in the persistence.xml file or specify
     *            values for properties not specified in the persistence.xml
     *            (and may be null if no properties are specified).
     * @return EntityManagerFactory for the persistence unit, or null if the
     *         provider is not the right provider
     */
    public EntityManagerFactory createEntityManagerFactory(String emName, Map map);

    /**
     * Called by the container when an EntityManagerFactory is to be created.
     * 
     * @param info
     *            Metadata for use by the persistence provider
     * @return EntityManagerFactory for the persistence unit specified by the
     *         metadata
     * @param map
     *            A Map of integration-level properties for use by the
     *            persistence provider (may be null if no properties are
     *            specified). If a Bean Validation provider is present in the
     *            classpath, the container must pass the ValidatorFactory
     *            instance in the map with the key
     *            "javax.persistence.validation.factory".
     */
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map);

    /**
     * If the provider determines that the entity has been provided by itself
     * and that the state of the specified attribute has been loaded, this
     * method returns LoadState.LOADED. If the provider determines that the
     * entity has been provided by itself and that either entity attributes with
     * FetchType EAGER have not been loaded or that the state of the specified
     * attribute has not been loaded, this methods returns LoadState.NOT_LOADED.
     * If a provider cannot determine the load state, this method returns
     * LoadState.UNKNOWN. The provider's implementation of this method must not
     * obtain a reference to an attribute value, as this could trigger the
     * loading of entity state if the entity has been provided by a different
     * provider.
     * 
     * @param entity
     * @param attributeName
     *            name of attribute whose load status is to be determined
     * @return load status of the attribute
     */
    public LoadState isLoadedWithoutReference(Object entity, String attributeName);

    /**
     * If the provider determines that the entity has been provided by itself
     * and that the state of the specified attribute has been loaded, this
     * method returns LoadState.LOADED. If a provider determines that the entity
     * has been provided by itself and that either the entity attributes with
     * FetchType EAGER have not been loaded or that the state of the specified
     * attribute has not been loaded, this method returns return
     * LoadState.NOT_LOADED. If the provider cannot determine the load state,
     * this method returns LoadState.UNKNOWN. The provider's implementation of
     * this method is permitted to obtain a reference to the attribute value.
     * (This access is safe because providers which might trigger the loading of
     * the attribute state will have already been determined by
     * isLoadedWithoutReference. )
     * 
     * @param entity
     * @param attributeName
     *            name of attribute whose load status is to be determined
     * @return load status of the attribute
     */
    public LoadState isLoadedWithReference(Object entity, String attributeName);

    /**
     * If the provider determines that the entity has been provided by itself
     * and that the state of all attributes for which FetchType EAGER has been
     * specified have been loaded, this method returns LoadState.LOADED. If the
     * provider determines that the entity has been provided by itself and that
     * not all attributes with FetchType EAGER have been loaded, this method
     * returns LoadState.NOT_LOADED. If the provider cannot determine if the
     * entity has been provided by itself, this method returns
     * LoadState.UNKNOWN. The provider's implementation of this method must not
     * obtain a reference to any attribute value, as this could trigger the
     * loading of entity state if the entity has been provided by a different
     * provider.
     * 
     * @param entity
     *            whose loaded status is to be determined
     * @return load status of the entity
     */
    public LoadState isLoaded(Object entity);
}