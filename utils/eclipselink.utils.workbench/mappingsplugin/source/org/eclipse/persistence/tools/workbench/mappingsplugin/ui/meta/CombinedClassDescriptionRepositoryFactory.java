/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
