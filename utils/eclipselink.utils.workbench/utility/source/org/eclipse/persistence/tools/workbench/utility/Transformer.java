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
package org.eclipse.persistence.tools.workbench.utility;

/**
 * Used by various "pluggable" classes to transform objects.
 */
public interface Transformer {

	/**
	 * Return the transformed object.
	 * The semantics of "transform" is determined by the
	 * contract between the client and the server.
	 */
	Object transform(Object o);


	Transformer NULL_INSTANCE =
		new Transformer() {
			// simply return the object, unchanged
			public Object transform(Object o) {
				return o;
			}
			public String toString() {
				return "NullTransformer";
			}
		};

}
