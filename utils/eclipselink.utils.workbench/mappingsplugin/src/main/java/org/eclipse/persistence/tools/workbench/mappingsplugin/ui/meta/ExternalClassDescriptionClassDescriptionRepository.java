/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;


/**
 * The "external" class description repository wraps the MW class repository.
 * The UI can use this in conjunction with ExternalClassDescriptionClassDescriptionAdapter.
 */
public class ExternalClassDescriptionClassDescriptionRepository
    implements ClassDescriptionRepository
{
    protected MWClassRepository repository;

    /**
     * Wrap the specified repository
     */
    public ExternalClassDescriptionClassDescriptionRepository(MWClassRepository repository) {
        super();
        this.repository = repository;
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository#refreshClassDescriptions()
     */
    public void refreshClassDescriptions() {
        this.repository.refreshExternalClassDescriptions();
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository#classDescriptions()
     */
    public Iterator classDescriptions() {
        return this.filteredExternalClassDescriptions(this.repository.externalClassDescriptions());
    }

    /**
     * Filter out the "array", "local", and "anonymous" classes from the original list.
     * (Hopefully there are no "array" classes....)
     */
    protected Iterator filteredExternalClassDescriptions(Iterator externalClassDescriptions) {
        return new FilteringIterator(externalClassDescriptions) {
            protected boolean accept(Object o) {
                return ExternalClassDescriptionClassDescriptionRepository.this.accept(((ExternalClassDescription) o).getName());
            }
        };
    }

    protected boolean accept(String externalClassDescriptionName) {
        return ClassTools.classNamedIsDeclarable(externalClassDescriptionName);
    }

}
