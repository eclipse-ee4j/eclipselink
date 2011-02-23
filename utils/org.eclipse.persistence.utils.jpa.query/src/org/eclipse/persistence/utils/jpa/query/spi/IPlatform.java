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
package org.eclipse.persistence.utils.jpa.query.spi;

/**
 * This enumeration contains two constants that helps to discriminate JPA artifacts being defined
 * in a pure Java environment or when EclipseLink is the persistence provider.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public enum IPlatform {

	/**
	 * This constant is used when the JPA platform is EclipseLink.
	 */
	ECLIPSE_LINK,

	/**
	 * This constant is used when the JPA platform is pure Java.
	 */
	JAVA;
}