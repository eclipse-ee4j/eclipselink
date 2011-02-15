/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.utils.jpa.query.spi.IMappingType;

/**
 * This object stores the various choices available for content assist.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ContentAssistItems
{
	/**
	 * The set of possible abstract schema names, which are the entity names.
	 */
	private Set<String> abstractSchemaNames;

	/**
	 * The set of possible identification variables.
	 */
	private Set<String> identificationVariables;

	/**
	 * The set of possible JPQL identifiers.
	 */
	private Set<String> identifiers;

	/**
	 * The set of possible properties, which are either state fields or
	 * collection valued fields.
	 */
	private Map<String, IMappingType> properties;

	/**
	 * Creates a new <code>ContentAssistItems</code>.
	 */
	ContentAssistItems()
	{
		super();
		initialize();
	}

	/**
	 * Returns the possible abstract schema names, which are the entity names.
	 *
	 * @return The possible abstract schema names that are allowed at the
	 * position of the cursor within the query
	 */
	public Iterator<String> abstractSchemaNames()
	{
		return new ArrayList<String>(abstractSchemaNames).iterator();
	}

	public void addAbstractSchemaName(String abstractSchemaName)
	{
		abstractSchemaNames.add(abstractSchemaName);
	}

	public void addIdentificationVariable(String identificationVariable)
	{
		identificationVariables.add(identificationVariable);
	}

	public void addIdentifier(String identifier)
	{
		identifiers.add(identifier);
	}

	void addProperty(String property, IMappingType mappingType)
	{
		properties.put(property, mappingType);
	}

	/**
	 * Determines whether this holder has at least one item.
	 *
	 * @return <code>true</code> if there is at least one item; <code>false</code>
	 * if this holder is empty
	 */
	public boolean hasItems()
	{
		return !properties.isEmpty()          ||
		       !identifiers.isEmpty()         ||
		       !abstractSchemaNames.isEmpty() ||
		       !identificationVariables.isEmpty();
	}

	/**
	 * Returns the list of possible identification variables.
	 *
	 * @return The list of possible identification variables
	 */
	public Iterator<String> identificationVariables()
	{
		return new ArrayList<String>(identificationVariables).iterator();
	}

	/**
	 * Returns the list of possible JPQL identifiers.
	 *
	 * @return The list of possible JPQL identifiers
	 */
	public Iterator<String> identifiers()
	{
		return new ArrayList<String>(identifiers).iterator();
	}

	private void initialize()
	{
		identifiers             = new HashSet<String>();
		abstractSchemaNames     = new HashSet<String>();
		identificationVariables = new HashSet<String>();
		properties              = new HashMap<String, IMappingType>();
	}

	public Iterator<String> items()
	{
		List<String> items = new ArrayList<String>();
		items.addAll(abstractSchemaNames);
		items.addAll(identificationVariables);
		items.addAll(identifiers);
		items.addAll(properties.keySet());
		return items.iterator();
	}

	/**
	 * Retrieves the {@link IMappingType} for the property with the given name.
	 *
	 * @param propertyName The name of the property that should be part of this
	 * holder
	 * @return The {@link IMappingType} for the property with the given name
	 */
	public IMappingType mappingType(String propertyName)
	{
		return properties.get(propertyName);
	}

	/**
	 * Returns the names of the state fields and collection valued fields.
	 *
	 * @return The list of possible choices that are the names of state fields
	 * and collection valued fields
	 */
	public Iterator<String> properties()
	{
		return new ArrayList<String>(properties.keySet()).iterator();
	}

	boolean remove(String identifier)
	{
		return identifiers.remove(identifier)             ||
		       abstractSchemaNames.remove(identifier)     ||
		       identificationVariables.remove(identifier) ||
		       properties.remove(identifier) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		if (!identifiers.isEmpty())
		{
			sb.append(identifiers);
		}

		if (!abstractSchemaNames.isEmpty())
		{
			if (sb.length() > 0)
			{
				sb.append(", ");
			}

			sb.append(abstractSchemaNames);
		}

		if (!identificationVariables.isEmpty())
		{
			if (sb.length() > 0)
			{
				sb.append(", ");
			}

			sb.append(identificationVariables);
		}

		if (!properties.isEmpty())
		{
			if (sb.length() > 0)
			{
				sb.append(", ");
			}

			sb.append(properties);
		}

		return sb.toString();
	}
}