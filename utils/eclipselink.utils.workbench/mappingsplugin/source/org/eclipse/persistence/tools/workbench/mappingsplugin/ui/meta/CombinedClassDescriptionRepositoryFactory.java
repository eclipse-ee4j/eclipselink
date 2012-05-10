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

import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepositoryFactory;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


/**
 * Straightforward implementation of factory interface for a "combined"
 * class description repository. You can configure whether the repository
 * produced by the factory will return more than one class with the
 * same name.
 */
public class CombinedClassDescriptionRepositoryFactory
	implements ClassDescriptionRepositoryFactory
{
	private ClassRepositoryHolder classRepositoryHolder;
	private Filter classNameFilter;
	protected boolean repositoryWillReturnDuplicateClassNames;

	public CombinedClassDescriptionRepositoryFactory(ClassRepositoryHolder classRepositoryHolder, Filter classNameFilter) {
		super();
		this.classRepositoryHolder = classRepositoryHolder;
		this.classNameFilter = classNameFilter;
		this.repositoryWillReturnDuplicateClassNames = false;
	}

	/**
	 * Set whether the repository created by the factory will return
	 * duplicate class names. The default is false.
	 * Set to true if you are going to refresh
	 * the class and want to allow the user to select which version
	 * of the class from the classpath to refresh.
	 */
	public void setRepositoryWillReturnDuplicateClassNames(boolean repositoryWillReturnDuplicateClassNames) {
		this.repositoryWillReturnDuplicateClassNames = repositoryWillReturnDuplicateClassNames;
	}

	public ClassDescriptionRepository createClassDescriptionRepository() {
		CombinedClassDescriptionRepository repository = new CombinedClassDescriptionRepository(this.classRepositoryHolder.getClassRepository(), this.classNameFilter);
		repository.setReturnsDuplicateClassNames(this.repositoryWillReturnDuplicateClassNames);
		return repository;
	}

}
