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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

/**
 * This <code>JNDINamingService</code> is implemented by any
 * {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TransportManagerAdapter
 * TransportManagerAdapter} that supports the functionality defined here.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public interface JNDINamingService extends Property
{
	/**
	 * Identifies a change in the <code>JNDINamingService</code>'s initial
	 * context factory name.
	 */
	public static final String INITIAL_CONTEXT_FACTORY_NAME_PROPERTY = "initialContextFactoryName";

	/**
	 * Identifies a change in the <code>JNDINamingService</code>'s password.
	 */
	public static final String JNDI_PASSWORD_PROPERTY = "password";

	/**
	 * Identifies a change in the <code>JNDINamingService</code>'s URL.
	 */
	public static final String JNDI_URL_PROPERTY = "url";

	/**
	 * Identifies a change in the <code>JNDINamingService</code>'s username.
	 */
	public static final String JNDI_USER_NAME_PROPERTY = "username";

	/**
	 * Returns the name of the initialize context factory.
	 *
	 * @return The name of the initialize context factory
	 */
	public String getInitialContextFactoryName();

	/**
	 * Returns the (unencryped) password for this JNDI Naming Service.
	 *
	 * @return The (unencryped) password or <code>null</code> if it was not
	 * specified
	 */
	public String getPassword();

	/**
	 * Returns the URL for this JNDI Naming Service.
	 *
	 * @return The URL or <code>null</code> if it was not specified
	 */
	public String getURL();

	/**
	 * Returns the username for this JNDI Naming Service.
	 *
	 * @return The username or <code>null</code> if it was not specified
	 */
	public String getUserName();

	/**
	 * Sets the name of the initialize context factory.
	 *
	 * @param name The name of the initialize context factory
	 */
	public void setInitialContextFactoryName(String name);

	/**
	 * Sets the (unencryped) password to use by this JNDI Naming Service.
	 *
	 * @param password The (unencryped) password value or <code>null</code>
	 */
	public void setPassword(String password);

	/**
	 * Sets the URL to use by this JNDI Naming Service.
	 *
	 * @param url The URL value or <code>null</code>
	 */
	public void setURL(String url);

	/**
	 * Returns the username for this JNDI Naming Service.
	 *
	 * @param name The username value <code>null</code>
	 */
	public void setUserName(String name);
}
