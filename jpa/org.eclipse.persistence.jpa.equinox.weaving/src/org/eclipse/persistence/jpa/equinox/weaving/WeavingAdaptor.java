/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware, ssmith - Initial Equinox weaving code
 ******************************************************************************/  
 package org.eclipse.persistence.jpa.equinox.weaving;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Properties;

import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.baseadaptor.hooks.AdaptorHook;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class WeavingAdaptor implements AdaptorHook {

	public void frameworkStart(BundleContext context) throws BundleException {
		WeaverRegistry.getInstance().start(context);
	}

	public void frameworkStopping(BundleContext context) {
		WeaverRegistry.getInstance().stop(context);

	}

	public void addProperties(Properties properties) {
	}

	public FrameworkLog createFrameworkLog() {
		return null;
	}

	public void frameworkStop(BundleContext context) throws BundleException {
	}

	public void handleRuntimeError(Throwable error) {
	}

	public void initialize(BaseAdaptor adaptor) {
	}

	public URLConnection mapLocationToURLConnection(String location)
			throws IOException {
		return null;
	}

	public boolean matchDNChain(String pattern, String[] dnChain) {
		return false;
	}
}
