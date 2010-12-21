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
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

// JDK
import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionBrokerAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;

// Mapping Workbench

/**
 * This <code>LabelDecorator</code> formats {@link SessionAdapter} objects by
 * using its display string along with its type (Database, Server or Broker) and
 * its data source type (either EIS, Database or XML).
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class SessionCellRendererAdapter extends AbstractCellRendererAdapter
{
	/**
	 * The first section of the key used to retrieve the localized string. The
	 * session type is appended after, followed by the data source type.
	 */
	private final String prefixKey;

	/**
	 * The repository used to retrieve the localized string and icon.
	 */
	private final ResourceRepository repository;

	/**
	 * Creates a new <code>SessionLabelDecorator</code>.
	 *
	 * @param repository The repository used to retrieve localized string and the
	 * icon that decorates objects of type {@link SessionAdapter}
	 */
	public SessionCellRendererAdapter(ResourceRepository repository)
	{
		this(repository, "PROJECT_SESSIONS_SESSION_TYPE");
	}

	/**
	 * Creates a new <code>SessionLabelDecorator</code>.
	 * 
	 * @param repository The repository used to retrieve localized string and the
	 * icon that decorates objects of type {@link SessionAdapter}
	 * @param prefixKey The first section of the key used to retrieve the
	 * localized string. The session type is appended after, followed by the data
	 * source type
	 */
	public SessionCellRendererAdapter(ResourceRepository repository, String prefixKey)
	{
		super();

		this.repository = repository;
		this.prefixKey = prefixKey;
	}

	/**
	 * Creates the string that will be used to retrieve the localized string from
	 * the resource bundle.
	 *
	 * @param session The session to be formatted
	 * @param prefixKey The string to be appended at the beginning of the key
	 * @return The fully created key
	 */
	private static String buildKey(SessionAdapter session,
											 String prefixKey)
	{
		StringBuffer sb = new StringBuffer(prefixKey);

		// Add Session Type
		if (session instanceof ServerSessionAdapter)
		{
			sb.append("_SERVER");
		}
		else if (session instanceof DatabaseSessionAdapter)
		{
			sb.append("_DATABASE");
		}
		else if (session instanceof SessionBrokerAdapter)
		{
			sb.append("_BROKER");
		}

		// Add Data Source Type
		if (session.platformIsXml())
		{
			sb.append("_XML");
		}
		else if (session.platformIsRdbms())
		{
			sb.append("_RDBMS");
		}
		else if (session.platformIsEis())
		{
			sb.append("_EIS");
		}

		return sb.toString();
	}

	/**
	 * Retrieves the key mapped to the icon that reflects the given session.
	 *
	 * @param session The {@link SessionAdapter} to be represented by an icon
	 * @return The key of the icon to be used
	 */
	public static String iconKey(SessionAdapter session)
	{
		return buildKey(session, "SESSION");
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
		SessionAdapter session = (SessionAdapter) value;
		String bundleKey = buildKey(session, "SESSION_DISPLAY_STRING_TITLE_BAR");
		return this.repository.getString(bundleKey, session.displayString(), session.getDataSourceName());
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
		SessionAdapter session = (SessionAdapter) value;
		return this.repository.getIcon(iconKey(session));
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
		SessionAdapter session = (SessionAdapter) value;
		String bundleKey = buildKey(session, this.prefixKey);
		return this.repository.getString(bundleKey, session.displayString(), session.getDataSourceName());
	}
}
