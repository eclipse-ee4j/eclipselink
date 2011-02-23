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
 * The default implementation of {@link ContentAssistItems}.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultContentAssistItems implements ContentAssistItems {

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
	 * The set of possible properties, which are either state fields or collection valued fields.
	 */
	private Map<String, IMappingType> properties;

	/**
	 * The identification variables mapped to their abstract schema names.
	 */
	private Map<String, String> rangeIdentificationVariables;

	/**
	 * Creates a new <code>DefaultContentAssistItems</code>.
	 */
	DefaultContentAssistItems() {
		super();
		initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<String> abstractSchemaNames() {
		return new ArrayList<String>(abstractSchemaNames).iterator();
	}

	public void addAbstractSchemaName(String abstractSchemaName) {
		abstractSchemaNames.add(abstractSchemaName);
	}

	public void addIdentificationVariable(String identificationVariable) {
		identificationVariables.add(identificationVariable);
	}

	public void addIdentifier(String identifier) {
		identifiers.add(identifier);
	}

	void addProperty(String property, IMappingType mappingType) {
		properties.put(property, mappingType);
	}

	public void addRangeIdentificationVariable(String identificationVariable,
	                                           String abstractSchemaName) {

		rangeIdentificationVariables.put(identificationVariable, abstractSchemaName);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getAbstractSchemaName(String identificationVariable) {
		return rangeIdentificationVariables.get(identificationVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	public IMappingType getMappingType(String propertyName) {
		return properties.get(propertyName);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasItems() {
		return !properties.isEmpty()          ||
		       !identifiers.isEmpty()         ||
		       !abstractSchemaNames.isEmpty() ||
		       !identificationVariables.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<String> identificationVariables() {
		return new ArrayList<String>(identificationVariables).iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<String> identifiers() {
		return new ArrayList<String>(identifiers).iterator();
	}

	private void initialize() {
		identifiers                  = new HashSet<String>();
		abstractSchemaNames          = new HashSet<String>();
		identificationVariables      = new HashSet<String>();
		rangeIdentificationVariables = new HashMap<String, String>();
		properties                   = new HashMap<String, IMappingType>();
	}

	public Iterator<String> items() {
		List<String> items = new ArrayList<String>();
		items.addAll(abstractSchemaNames);
		items.addAll(identificationVariables);
		items.addAll(identifiers);
		items.addAll(properties.keySet());
		return items.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<String> properties() {
		return new ArrayList<String>(properties.keySet()).iterator();
	}

	boolean remove(String identifier) {
		return identifiers.remove(identifier)             ||
		       abstractSchemaNames.remove(identifier)     ||
		       identificationVariables.remove(identifier) ||
		       properties.remove(identifier) != null      ||
		       rangeIdentificationVariables.remove(identifier) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		if (!identifiers.isEmpty()) {
			sb.append(identifiers);
		}

		if (!abstractSchemaNames.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(abstractSchemaNames);
		}

		if (!identificationVariables.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(identificationVariables);
		}

		if (!properties.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(properties);
		}

		if (sb.length() == 0) {
			sb.append("<No Default Proposals>");
		}

		return sb.toString();
	}
}