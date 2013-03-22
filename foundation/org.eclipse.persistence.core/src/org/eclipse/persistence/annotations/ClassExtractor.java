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
 *     12/18/2009-2.1 Guy Pelletier 
 *       - 211323: Add class extractor support to the EclipseLink-ORM.XML Schema
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A ClassExtractor allows for a user defined class indicator in place of 
 * providing a discriminator column. The class has the following restrictions:
 * <ul>
 * <li> It must extend the org.eclipse.persistence.descriptors.ClassExtractor 
 *    class and implement the extractClassFromRow(Record, Session) method. 
 * <li> That method must take a database row (a Record/Map) as an argument and 
 *    must return the class to use for that row. 
 * </ul>
 * This method will be used to decide which class to instantiate when reading 
 * from the database. It is the application's responsibility to populate any 
 * typing information in the database required to determine the class from the 
 * row.
 * <p>
 * The ClassExtractor must only be set on the root of an entity class or
 * sub-hierarchy in which a different inheritance strategy is applied. The 
 * ClassExtractor can only be used with the SINGLE_TABLE and JOINED inheritance 
 * strategies.
 * <p>
 * If a ClassExtractor is used then a DiscriminatorColumn cannot be used. A 
 * DiscriminatorColumn also cannot be used on either the root or its subclasses.
 * <p>
 * In addition, for more complex configurations using a ClassExtractor and a 
 * SINGLE_TABLE strategy, the descriptor's withAllSubclasses and onlyInstances 
 * expressions should be set through the ClassExtractor's initialize method.
 *
 * @see org.eclipse.persistence.descriptors.ClassExtractor
 * @see org.eclipse.persistence.descriptors.InheritancePolicy.setWithAllSubclassesExpression(Expression)
 * @see org.eclipse.persistence.descriptors.InheritancePolicy.setOnlyInstancesExpression(Expression)
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.1 
 */ 
@Target({TYPE})
@Retention(RUNTIME)
public @interface ClassExtractor {
    /**
     * (Required) Defines the name of the class extractor that should be 
     * applied to this entity's descriptor.
     */
    Class<? extends org.eclipse.persistence.descriptors.ClassExtractor> value(); 
}

