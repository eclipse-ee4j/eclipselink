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
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * The "classpath" class description repository wraps a classpath and returns
 * class descriptions for each of the class files found on the classpath. The UI can use
 * this in conjunction with ClasspathClassDescription.Adapter.
 */
public class ClasspathClassDescriptionRepository implements ClassDescriptionRepository {
	protected Classpath classpath;

	/**
	 * wrap the specified classpath
	 */
	public ClasspathClassDescriptionRepository(String classpath) {
		super();
		this.classpath = new Classpath(classpath);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository#classDescriptions()
	 */
	public Iterator classDescriptions() {
		return new CompositeIterator(this.entriesClassDescriptions());
	}

	protected Iterator entriesClassDescriptions() {
		return new TransformationIterator(this.classpathEntries()) {
			protected Object transform(Object next) {
				return classDescriptionsFor((Classpath.Entry) next);
			}
			private Iterator classDescriptionsFor(final Classpath.Entry entry) {
				return new TransformationIterator(entry.classNamesStream(ClasspathClassDescriptionRepository.this.classNameFilter())) {
					protected Object transform(Object next) {
						return new ClasspathClassDescription((String) next, entry.fileName());
					}
				};
			}
		};
	}

	/**
	 * Return the entries in the classpath.
	 */
	protected Iterator classpathEntries() {
		return new ArrayIterator(this.classpath.getEntries());
	}

	/**
	 * Return the class names for the specified classpath entry.
	 */
	protected Filter classNameFilter() {
		return Filter.NULL_INSTANCE;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository#refreshClassDescriptions()
	 */
	public void refreshClassDescriptions() {
		// do nothing, since the class descriptions are recalculated every time
	}

}
