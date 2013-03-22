/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke, mnorman - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *
 ******************************************************************************/
package org.eclipse.persistence.dynamic;

//EclipseLink imports
import org.eclipse.persistence.exceptions.DynamicException;

/**
 * <code>DynamicEntity</code> is the public interface for dealing with dynamic persistent objects.
 * <p>
 * The purpose of dynamic persistent objects is to enable (simple) data access when only mapping
 * information is available <br />
 * and no concrete Java model is present (specifically, no <tt>.class</tt> files .)
 * <p>
 * Applications using <code>DynamicEntity</code>'s can access the persistent state using property names
 * which correspond <br />
 * to the mapped attributes in the underlying EclipseLink descriptors.
 * For properties mapped to containers ({@link java.util.Collection Collection},<br />
 * {@link java.util.Map Map}, etc.), the property is retrieved then the resulting container can
 * be manipulated.
 * <pre>
 *     ...
 *     DynamicEntity de = ...; // retrieve from database
 *     Collection&lt;String&gt; myListOfGroups = de.&lt;Collection&lt;String&gt;&gt;get("myListOfGroups");
 *     if (!myListOfGroups.isEmpty()) {
 *        myListOfGroups.add("FabFour");
 *     }
 * </pre>
 * To discover meta-data about a DynamicEntity's properties, see the {@link DynamicHelper} class
 *
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public interface DynamicEntity {

    /**
     * Return the persistence value for the given property as the specified type.
     * In the case of relationships, this call will populate lazy-loaded relationships
     *
     * @param <T>
     *      generic type of the property (if not provided, assume Object).
     *      If the property cannot be cast to the specific type, a {@link DynamicException}will be thrown.
     * @param
     *      propertyName the name of a mapped property
     *      If the property cannot be found, a {@link DynamicException} will be thrown.
     * @throws
     *      DynamicException
     * @return
     *      persistent value or relationship container of the specified type
     */
    public <T> T get(String propertyName) throws DynamicException;

    /**
     * Set the persistence value for the given property to the specified value
     *
     * @param
     *      propertyName the name of a mapped property
     *      If the property cannot be found, a {@link DynamicException} will be thrown.
     * @param
     *      value the specified object
     * @throws
     *      DynamicException
     * @return
     *      the same DynamicEntity instance
     */
    public DynamicEntity set(String propertyName, Object value) throws DynamicException;

    /**
     * Discover if a property has a persistent value
     *
     * @param
     *      propertyName the name of a mapped property
     *      If the property cannot be found, a {@link DynamicException} will be thrown.
     * @return
     *      true if the property has been set
     * @throws
     *      DynamicException
     */
    public boolean isSet(String propertyName) throws DynamicException;

}
