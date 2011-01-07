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
package org.eclipse.persistence.tools.workbench.scplugin;

/**
 * This interface defines a constant that can be used instead of the number
 * defined for each problem bundle.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public interface SCProblemsConstants
{
	public static final String DATABASE_LOGIN_CONNECTION_URL      = "0801";
	public static final String DATABASE_LOGIN_DRIVER_CLASS        = "0802";
	public static final String DATABASE_LOGIN_DATA_SOURCE_NAME    = "0803";
	public static final String SESSION_BROKER_SESSION_COUNT       = "0804";
	public static final String SESSION_DATABASE_MAPPING_PROJECT   = "0805";
	public static final String USER_DEFINED_TRANSPORT_CLASS       = "0806";
	public static final String DEFAULT_LOGGING_FILE_NAME          = "0807";
	public static final String NON_JTS_VALUES                     = "0808";
	public static final String DATABASE_LOGIN_PLATFORM_CLASS_NAME = "0810";
	public static final String CUSTOM_SERVER_PLATFORM_JTA         = "0811";
	public static final String CUSTOM_SERVER_PLATFORM_SERVER_CLASS_NAME = "0812";
	public static final String EXTERNAL_TRANSACTION_CONTROLLER_904_DEPRECATED = "0813";
	public static final String CACHE_SYNCHRONIZATION_DEPRECATED          = "0814";
	public static final String OBSOLETE_SERVER_PLATFORM_PROBLEM = "0815";

}
