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
 *     03/26/2008-1.0M6 Guy Pelletier 
 *       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema 
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * A DiscriminatorClass is used within a VariableOneToOne annotation.
 * 
 * @author Guy Pelletier
 * @since Eclipselink 1.0 
 */ 
@Target({}) 
@Retention(RUNTIME)
public @interface DiscriminatorClass {
    /**
     * (Required) The discriminator to be stored on the database. 
     */
    String discriminator();

    /**
     * (Required) The class to the instantiated with the given discriminator.
     */
    Class value();
}
