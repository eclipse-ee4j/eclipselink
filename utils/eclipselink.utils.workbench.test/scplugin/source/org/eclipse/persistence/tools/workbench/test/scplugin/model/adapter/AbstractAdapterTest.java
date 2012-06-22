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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.adapter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DataSource;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatform;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import junit.framework.TestCase;

/**
 * @version 10.0.3
 * @author Pascal Filion
 */
public abstract class AbstractAdapterTest extends TestCase
{
	public AbstractAdapterTest(String name)
	{
		super(name);
	}

	protected final ServerPlatform noServerPlatform()
	{
		return new ServerPlatform( "NoServerPlatform");
	}

	protected final DataSource buildOracleDataSource()
	{
		return buildDataSource( "Oracle");
	}

	protected final DataSource buildAQDataSource()
	{
		return buildEisDataSource( "Oracle AQ");
	}

	protected final DataSource buildDataSource( String platformName)
	{
		DatabasePlatform platform = DatabasePlatformRepository.getDefault().platformNamed( platformName);
		return new DataSource( platform);
	}
	
	protected final DataSource buildEisDataSource( String platformName)
	{
		return new DataSource( platformName);
	}

	protected final DataSource buildPreferedDataSourceFor( SCAdapter adapter)
	{
		return DataSource.buildPreferedDataSourceFor( adapter);
	}
	
	protected SessionConfigs loadTopLinkSessions( File path) {
		
		SessionConfigs topLinkSessions =
		    SessionManager.getManager().getInternalMWConfigObjects( path.getPath(), buildLoader( path), false);
		
		return topLinkSessions; 
	}
	
	private ClassLoader buildLoader( File path) {
	    final File topLinkSessionsPath = path;
	    
		URLClassLoader loader = new URLClassLoader( new URL[ 0], getClass().getClassLoader()) {

		public URL findResource( String name) {
				try {
					if(  name.equals( topLinkSessionsPath.getPath())) {
						return topLinkSessionsPath.toURL();
					}
				}
				catch( MalformedURLException e) {
					// Ignore so that super.findResource(String) will be called
				}
				URL url = super.findResource( name);

				if( url != null)
					return url;

				// Use this class class loader
				return getClass().getResource( name);
			}
		};
		return loader;
	}
	
	public boolean hasProblem(String messageKey, AbstractNodeModel model)
	{
		model.validateBranch();

		for (Iterator iter = model.problems(); iter.hasNext();)
		{
			Problem problem = (Problem) iter.next();

			if (problem.getMessageKey().equals(messageKey))
				return true;
		}

		return false;
	}

	/**
	 * Determines whether an object contains any problem in the given collection.
	 *
	 * @param errors
	 * @param model
	 */
	public boolean hasAnyProblem(Collection errors, AbstractNodeModel model)
	{
		for (Iterator iter = errors.iterator(); iter.hasNext();)
		{
			String messageKey = (String) iter.next();

			if (hasProblem(messageKey, model))
				return true;
		}

		return false;
	}
}
