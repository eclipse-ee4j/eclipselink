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
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * Define the foreign key defined by the relationship to cascade the delete on the database.
 * This means when the source object is deleted the target object will be automatically deleted by the database.
 * This will affect DDL generation as well as runtime behavior in omitting the delete statements.
 * <p>
 * The constraint cascaded depends on the mapping, only relationship mappings are allowed.
 * The relationship should also use cascade remove, or deleteOrphans.
 * <p>For a OneToOne it can only be defined if the mapping uses a mappedBy, and will delete the target object.
 * <p>It cannot be defined for a ManyToOne.
 * <p>For a OneToMany it will delete the target objects, or ONLY the join table if using a join table.
 * <p>For a ManyToMany it will delete the rows from the join table, not the target objects.
 * <p>For an ElementCollection it will delete the target rows.
 * <p>For an Entity it will delete the secondary or JOINED inheritance tables.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)
public @interface CascadeOnDelete {
}
