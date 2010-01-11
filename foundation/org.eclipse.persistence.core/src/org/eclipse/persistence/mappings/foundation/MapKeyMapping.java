/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial API check-in for MappedKeyMapContainerPolicy
 ******************************************************************************/  
package org.eclipse.persistence.mappings.foundation;

/**
 * MapKeyMapping is implemented by DatabaseMappings that can be used to map the key in a map
 * that uses a MappedKeyMapContainerPolicy.  This interface provides the facilities to retreive data
 * for the key from the database, to get data from the object to put in the database, and to appropriately
 * initialize the mappings.
 * 
 * @see MappedKeyMapContainerPolicy
 * @see AbstractDirectMapping
 * @see AggregateObjectMapping
 * @see OneToOneMapping
 * @author tware
 *
 */
public interface MapKeyMapping extends MapComponentMapping {

}
