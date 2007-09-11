/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.weaving;

public class SimpleClassLoader extends ClassLoader {

	public SimpleClassLoader() {
		super();
	}

	public Class<?> define_class(String name, byte[] b, int off, int len) {
		return defineClass(name, b, off, len, null);
	}
}
