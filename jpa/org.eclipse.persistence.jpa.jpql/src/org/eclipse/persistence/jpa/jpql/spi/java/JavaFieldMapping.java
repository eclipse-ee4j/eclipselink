/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.spi.java;

import java.lang.reflect.Field;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;

/**
 * The concrete implementation of {@link org.eclipse.persistence.jpa.jpql.spi.IMapping IMapping}
 * that is wrapping the runtime representation of a persistent attribute.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class JavaFieldMapping extends AbstractFieldMapping {

	/**
	 * Creates a new <code>JavaFieldMapping</code>.
	 *
	 * @param parent The parent of this mapping
	 * @param field The Java field wrapped by this mapping
	 */
	public JavaFieldMapping(IManagedType parent, Field field) {
		super(parent, field);
	}
}