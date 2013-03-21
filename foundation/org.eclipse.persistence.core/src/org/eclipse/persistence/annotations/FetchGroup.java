/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/19/2010-2.1 Guy Pelletier 
 *       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A fetch group is a performance enhancement that allows a group of attributes 
 * of an object to be loaded on demand, which means that the data for an 
 * attribute might not loaded from the underlying data source until an explicit 
 * access call for the attribute first occurs. It avoids the wasteful practice 
 * of loading up all data of the object's attributes, in which the user is 
 * interested in only partial of them.
 * 
 * A great deal of caution and careful system use case analysis should be use 
 * when using the fetch group feature, as the extra round-trip would well offset 
 * the gain from the deferred loading in many cases.
 * 
 * EclipseLink fetch group support is twofold: the pre-defined fetch groups at 
 * the Entity or MappedSuperclass level; and dynamic (use case) fetch groups at 
 * the query level.
 * 
 * Fetch groups can only be used when weaving is enabled or when individual 
 * classes that define them explicitly implement the 
 * org.eclipse.persistence.queries.FetchGroupTracker interface.  
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.1 
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface FetchGroup {
    /**
     * (Required) The fetch group name.
     */
    String name(); 

    /**
     * (Optional) Indicates whether all relationship attributes
     * specified in the fetch group should be loaded.
     */
    boolean load() default false; 

    /**
     * (Required) The list of attributes to fetch.
     */
    FetchAttribute[] attributes();
}
