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
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

// JDK
import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;

// Mapping Workbench

/**
 * This <code>ConnectionPoolCellRendererAdapter</code> formats
 * {@link LoginAdapter} objects by using its display string along with its type.
 * 
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class ConnectionPoolCellRendererAdapter extends AbstractCellRendererAdapter
{
	/**
	 * The repository used to retrieve the localized string and icon.
	 */
	private final ResourceRepository repository;

	/**
	 * Creates a new <code>ConnectionPoolCellRendererAdapter</code>.
	 *
	 * @param repository The repository used to retrieve localized string and the
	 * icon that decorates objects of type {@link LoginAdapter}
	 */
	public ConnectionPoolCellRendererAdapter(ResourceRepository repository)
	{
		super();
		this.repository = repository;
	}

	/**
	 * Creates the string that will be used to retrieve the localized string from
	 * the resource bundle.
	 *
	 * @param session The pool to be formatted
	 * @param prefixKey The string to be appended at the beginning of the key
	 * @return The fully created key
	 */
	private static String buildKey(ConnectionPoolAdapter pool)
	{
		StringBuffer sb = new StringBuffer("CONNECTION_POOL");

		if (pool.isReadConnectionPool())
		{
			sb.append("_READ");
		}
		else if (pool.isSequenceConnectionPool())
		{
			sb.append("_SEQUENCE");
		}
		else if (pool.isWriteConnectionPool())
		{
			sb.append("_WRITE");
		}

		return sb.toString();
	}

	/**
	 * Retrieves the key mapped to the icon that reflects the given session.
	 *
	 * @param session The {@link SessionAdapter} to be represented by an icon
	 * @return The key of the icon to be used
	 */
	public static String iconKey(ConnectionPoolAdapter pool)
	{
		return buildKey(pool);
	}

	/**
	 * Returns the accessible name to be given to the component used to rendered
	 * the given value.
	 *
	 * @param value The object to be decorated
	 * @return A string that can add more description to the rendered object when
	 * the text is not sufficient, if <code>null</code> is returned, then the
	 * text is used as the accessible text
	 */
	public String buildAccessibleName(Object value)
	{
		ConnectionPoolAdapter pool = (ConnectionPoolAdapter) value;
		String bundleKey = buildKey(pool);
		return repository.getString(bundleKey, pool.displayString());
	}

	/**
	 * Returns an icon that can be used to identify the object in a UI component
	 * that supports icons.
	 *
	 * @param value The object to be represented by an icon, if one is required
	 * @return An icon representing the given object or <code>null</code>
	 */
	public Icon buildIcon(Object value)
	{
		ConnectionPoolAdapter pool = (ConnectionPoolAdapter) value;
		return repository.getIcon(iconKey(pool));
	}

	/**
	 * Returns a string that can be used to identify the object in a textual UI
	 * setting (typically the object's name).
	 *
	 * @param value The object to be represented by a string
	 * @return A string representation of the given object
	 */
	public String buildText(Object value)
	{
		ConnectionPoolAdapter pool = (ConnectionPoolAdapter) value;
		String bundleKey = buildKey(pool);
		return repository.getString(bundleKey, pool.displayString());
	}
}
