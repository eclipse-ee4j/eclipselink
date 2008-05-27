/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Gyorke
 ******************************************************************************/  
package org.eclipse.persistence.jpa.config;

/**
 * The class defines TopLink properties' names.
 * 
 * This properties are specific to an EnityManger and should be 
 * passed to createEntityManager methods of EntityManagerFactory.
 * 
 * Property values are usually case-insensitive with some common sense exceptions,
 * for instance class names.
 * 
 */
public class EntityManagerProperties {
	
    //for gf3334, this property force persistence context to read through JTA-managed ("write") connection in case there is an active transaction.    
    public static final String JOIN_EXISTING_TRANSACTION = "eclipselink.transaction.join-existing";
    
    //specifies, whether there should be used hard or soft references in the Persistence Context.
    //Default is "HARD".  With soft references entities no longer referenced by the application
    // may be garbage collected freeing resources.  Any changes that have not been flushed in these
    // entities will be lost.
    public static final String PERSISTENCE_CONTEXT_REFERENCE_MODE="eclipselink.persistence-context.reference-mode";
}
