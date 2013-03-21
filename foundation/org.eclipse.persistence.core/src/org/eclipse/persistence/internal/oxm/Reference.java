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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import java.util.HashMap;

import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.oxm.sequenced.Setting;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Holds mapping reference info.  The info held in this class
 * will be used after unmarshal to resolve 1-1 and 1-M mapping references.  This 
 * is necessary to ensure that all related objects have been created before 
 * attempting to set instance values in related objects.</p>
 */

public class Reference {
	protected Mapping mapping;		// mapping associated with this reference
	protected Object sourceObject;		// the source object instance
	protected Class targetClass;		// the reference class
	protected Object primaryKey;		// primary key values for cache lookup - used in single case
	protected HashMap primaryKeyMap;	// map of primary key values for cache lookup - used in collection case
	private Setting setting;
	private Object container;

	/**
	 * Get the container this reference should be added to if it was from a collection mapping
	 * @return
	 */
	public Object getContainer() {
		return container;
	}
	
	public Setting getSetting() {
		return setting;
	}

	public void setSetting(Setting setting) {
		this.setting = setting;
	}

	/**
	 * Constructor typically used in the collection case.
	 */
	public Reference(Mapping mapping, Object source, Class target, HashMap primaryKeyMap, Object container) {
		this.mapping = mapping;
		sourceObject = source;
		targetClass = target;
		this.primaryKeyMap = primaryKeyMap;
		this.container = container;
	}
	
	/**
	 * Constructor typically used in the single case.
	 */
	public Reference(Mapping mapping, Object source, Class target, Object primaryKey) {
		this.mapping = mapping;
		sourceObject = source;
		targetClass = target;
		this.primaryKey = primaryKey;
	}

	/**
	 * Return the XMLMapping associated with this reference.
	 * 
	 * @return
	 */
	public Mapping getMapping() {
		return mapping;
	}

	/**
	 * Return the map of primary key/values required to lookup
	 * the reference class in the cache.
	 * 
	 * @return
	 */
	public HashMap getPrimaryKeyMap() {
		return primaryKeyMap;
	}

	/**
	 * Return the list of primary key values required to lookup
	 * the reference class in the cache.
	 * 
	 * @return
	 */
	public Object getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * Return the source object for this reference.
	 * 
	 * @return
	 */
	public Object getSourceObject() {
		return sourceObject;
	}

	/**
	 * Return the target (reference) class for this reference.
	 * 
	 * @return
	 */
	public Class getTargetClass() {
		return targetClass;
	}
	
	/**
	 * Set the primary key value required to lookup
	 * the reference class in the cache.
	 */
	public void setPrimaryKey(Object primaryKey) {
		this.primaryKey = primaryKey;
	}
}
