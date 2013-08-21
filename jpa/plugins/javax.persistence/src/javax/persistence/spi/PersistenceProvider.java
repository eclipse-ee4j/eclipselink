/*******************************************************************************
 * Copyright (c) 2008, 2009 Sun Microsystems. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.0 - Version 2.0 (October 1, 2009)
 *     Specification available from http://jcp.org/en/jsr/detail?id=317
 *
 ******************************************************************************/
package javax.persistence.spi;

import javax.persistence.EntityManagerFactory;
import java.util.Map;

/**
 * Interface implemented by the persistence provider.
 *
 * <p> It is invoked by the container in Java EE environments and
 * by the {@link javax.persistence.Persistence} class in Java SE environments to
 * create an {@link javax.persistence.EntityManagerFactory}.
 *
 * @since Java Persistence 1.0
 */
public interface PersistenceProvider {

    /**
     * Called by <code>Persistence</code> class when an
     * <code>EntityManagerFactory</code> is to be created.
     *
     * @param emName  the name of the persistence unit
     * @param map  a Map of properties for use by the 
     * persistence provider. These properties may be used to
     * override the values of the corresponding elements in 
     * the <code>persistence.xml</code> file or specify values for 
     * properties not specified in the <code>persistence.xml</code>
     * (and may be null if no properties are specified).
     * @return EntityManagerFactory for the persistence unit, 
     * or null if the provider is not the right provider 
     */
    public EntityManagerFactory createEntityManagerFactory(String emName, Map map);

    /**
     * Called by the container when an <code>EntityManagerFactory</code>
     * is to be created. 
     *
     * @param info  metadata for use by the persistence provider
     * @param map  a Map of integration-level properties for use 
     * by the persistence provider (may be null if no properties
     * are specified).
     * If a Bean Validation provider is present in the classpath,
     * the container must pass the <code>ValidatorFactory</code> instance in
     * the map with the key <code>"javax.persistence.validation.factory"</code>.
     * @return EntityManagerFactory for the persistence unit 
     * specified by the metadata
     */
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map);

    /**
     * Return the utility interface implemented by the persistence
     * provider.
     * @return ProviderUtil interface
     *
     * @since Java Persistence 2.0
     */
    public ProviderUtil getProviderUtil();
}

