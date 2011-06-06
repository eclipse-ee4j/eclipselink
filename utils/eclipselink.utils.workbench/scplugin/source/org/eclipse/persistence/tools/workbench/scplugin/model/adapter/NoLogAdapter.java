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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

// Mapping Workbench
import org.eclipse.persistence.internal.sessions.factories.model.log.DefaultSessionLogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.log.LogConfig;

/**
 * A <code>null</code> instance of the adapter for {@link LogConfig}.
 *
 * @see LogConfig
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public final class NoLogAdapter extends LogAdapter {

	public static final String OFF_LOG_LEVEL = "off";

	protected NoLogAdapter(SCAdapter parent) {
		super(parent);
	}

	protected Object buildModel() {
		DefaultSessionLogConfig config = new DefaultSessionLogConfig();
		config.setLogLevel(OFF_LOG_LEVEL);
		return config;
	}

	/**
	 * Returns true if options are used.
	 */
	public boolean optionsIsEnable() {
		return false;
	}
	
	public void enableOptions() {
		// do nothing
	}
	
	public void disableOptions() {
		// do nothing
	}	
}
