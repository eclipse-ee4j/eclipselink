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
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * A PrivateOwned annotation is used to specify a relationship is privately 
 * owned. A privately owned relationship means the target object is a dependent 
 * part of the source object and is not referenced by any other object and 
 * cannot exist on its own. Private ownership causes many operations to be 
 * cascaded across the relationship, including, deletion, insertion, refreshing, 
 * locking (when cascaded). It also ensures that private objects removed from 
 * collections are deleted and object added are inserted.
 * 
 * A PrivateOwned annotation can be used in conjunction with a OneToOne, 
 * OneToMany and VariableOneToOne annotation. Private ownership is implied 
 * with the BasicCollection and BasicMap annotation.
 * 
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0 
 */ 
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface PrivateOwned {}
