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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/


package org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_2;

import java.util.EventListener;

public class EmployeeListener implements EventListener {
    public static int PRE_PERSIST_COUNT = 0;
    public static int POST_PERSIST_COUNT = 0;
    public static int PRE_REMOVE_COUNT = 0;
    public static int POST_REMOVE_COUNT = 0;
    public static int PRE_UPDATE_COUNT = 0;
    public static int POST_UPDATE_COUNT = 0;
    public static int POST_LOAD_COUNT = 0;

	public void prePersist(Object emp) {
        PRE_PERSIST_COUNT++;
	}

	public void postPersist(Object emp) {
        POST_PERSIST_COUNT++;
	}

	public void preRemove(Object emp) {
        PRE_REMOVE_COUNT++;
	}

	public void postRemove(Object emp) {
        POST_REMOVE_COUNT++;
	}

	public void preUpdate(Object emp) {
        PRE_UPDATE_COUNT++;
	}

	public void postUpdate(Object emp) {
        POST_UPDATE_COUNT++;
	}

	public void postLoad(Employee emp) {
        POST_LOAD_COUNT++;
	}
}
