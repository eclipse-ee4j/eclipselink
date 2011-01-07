/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.dynamic.simple.mappings;

//JUnit4 imports
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    org.eclipse.persistence.testing.tests.jpa.dynamic.simple.mappings.SimpleTypes_AggregateObject.class,
    org.eclipse.persistence.testing.tests.jpa.dynamic.simple.mappings.SimpleTypes_ManyToMany.class,
    org.eclipse.persistence.testing.tests.jpa.dynamic.simple.mappings.SimpleTypes_MultiTable.class,
    org.eclipse.persistence.testing.tests.jpa.dynamic.simple.mappings.SimpleTypes_OneToMany.class,
    org.eclipse.persistence.testing.tests.jpa.dynamic.simple.mappings.SimpleTypes_OneToOne.class
    }
)
public class AllTests {}