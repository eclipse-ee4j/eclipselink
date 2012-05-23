/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.xml.inheritance.listeners;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;

public class DefaultListener {
    // Not going to test all the callbacks from the default listener
    // since the listener gets added to 90+ classes and when testing that
    // is a lot of notifications which slows things down. Just
    // going to test a couple of the callbacks which should be
    // good enough.
    
    public static int PRE_PERSIST_COUNT = 0;
    public static int POST_PERSIST_COUNT = 0;
    public static int POST_LOAD_COUNT = 0;

    public DefaultListener() {
        super();
    }
    
    @PostLoad
    // Defined in XML, annotations processor should ignore this one.
    // If @PostLoad was defined on another method than it should throw
    // and exception cause we have multiple methods for the same call
    // back defined. Exception throwing tested manually.
    public void postLoad(Object obj) {
        POST_LOAD_COUNT++;
    }
    
	// @PrePersist
    // Defined in XML, test that we pick it up.
	public void prePersist(Object obj) {
        PRE_PERSIST_COUNT++;
	}
    
	@PostPersist
	public void postPersist(Object obj) {
        POST_PERSIST_COUNT++;
	}
}
