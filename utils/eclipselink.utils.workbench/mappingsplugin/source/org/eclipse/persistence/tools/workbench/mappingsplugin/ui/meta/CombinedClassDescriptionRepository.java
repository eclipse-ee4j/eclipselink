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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ClassDescription;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;


/**
 * The "combined" class description repository wraps the MW class repository
 * and returns a combination of the "external" types and the new
 * "internal" types.
 * The UI can use this in conjunction with ClassDescriptionAdapter.
 */
public class CombinedClassDescriptionRepository
	implements ClassDescriptionRepository
{
	protected MWClassRepository repository;
	protected Filter typeNameFilter;
	protected boolean returnsDuplicateClassNames;
	protected Set typeNames;


	// ********** constructors **********

	/**
	 * Wrap the specified repository, filtering its combined types.
	 * Duplicate types will be filtered out.
	 */
	public CombinedClassDescriptionRepository(MWClassRepository repository, Filter typeNameFilter) {
		super();
		this.repository = repository;
		this.typeNameFilter = typeNameFilter;
		this.returnsDuplicateClassNames = false;
	}

	/**
	 * Wrap the specified repository, returning all of its combined types.
	 * Duplicate types will be filtered out.
	 */
	public CombinedClassDescriptionRepository(MWClassRepository repository) {
		this(repository, Filter.NULL_INSTANCE);
	}


	// ********** ClassDescriptionRepository implementation **********

	/**
	 * Refresh the repository's "external" types, the "internal" types do
	 * not need to be refreshed.
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository#refreshClassDescriptions()
	 */
	public void refreshClassDescriptions() {
		this.typeNames = null;
		this.repository.refreshExternalClassDescriptions();
	}

	/**
	 * Filter the repository's combined types.
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository#classDescriptions()
	 */
	public Iterator classDescriptions() {
		return this.filteredCombinedTypes(this.repository.combinedTypes());
	}


	// ********** public API **********

	/**
	 * Set whether the repository will return duplicate class names.
	 * The default is false. Set to true if you are going to refresh
	 * the class and want to allow the user to select which version
	 * of the class from the classpath to refresh.
	 */
	public void setReturnsDuplicateClassNames(boolean returnsDuplicateClassNames) {
		this.returnsDuplicateClassNames = returnsDuplicateClassNames;
	}

	/**
	 * Return whether the repository will return duplicate class names.
	 * The default is false.
	 */
	public boolean returnsDuplicateClassNames() {
		return this.returnsDuplicateClassNames;
	}


	// ********** filtering **********

	/**
	 * Filter the original list using the client-supplied filter and an internal
	 * filter for duplicates.
	 */
	protected Iterator filteredCombinedTypes(Iterator combinedTypes) {
		return new FilteringIterator(combinedTypes) {
			protected boolean accept(Object o) {
				return CombinedClassDescriptionRepository.this.accept(((ClassDescription) o).getName());
			}
		};
	}

	protected boolean accept(String typeName) {
		return this.typeNameFilter.accept(typeName) && this.acceptDuplicate(typeName);
	}

	/**
	 * check for duplicates, if appropriate
	 */
	protected boolean acceptDuplicate(String typeName) {
		if (this.returnsDuplicateClassNames) {
			return true;
		}
		if (this.typeNames == null) {
			this.typeNames = new HashSet(10000);	// this will be big...
		}
		// @see java.util.Set#add(Object)
		return this.typeNames.add(typeName);
	}

}
