/*******************************************************************************
 * Copyright (c)  2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel.proxy;

import java.util.List;

import javax.persistence.metamodel.ListAttribute;

/**
 * A proxy class that allows EclipseLink to trigger the deployment of a persistence unit
 * as an ListAttribute is accessed in the metamodel.
 * @author tware
 *
 * @param <X>
 * @param <T>
 */
public class ListAttributeProxyImpl<X, V> extends PluralAttributeProxyImpl<X, List<V>, V> implements ListAttribute<X, V>  {

}
