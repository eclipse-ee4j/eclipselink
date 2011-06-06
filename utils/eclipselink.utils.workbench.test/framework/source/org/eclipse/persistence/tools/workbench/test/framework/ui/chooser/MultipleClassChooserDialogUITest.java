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
package org.eclipse.persistence.tools.workbench.test.framework.ui.chooser;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClasspathClassDescription;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClasspathClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.MultipleClassChooserDialog;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


/**
 * Simple test class for playing around with the MultipleClassChooserDialog.
 * 
 * Optional command line parm:
 * 	the classpath used to populate the "available" classes list;
 * if this is not specified, all the classes on the current classpath will be used
 */
public class MultipleClassChooserDialogUITest {
	private String classpath;

	public static void main(String[] args) {
		new MultipleClassChooserDialogUITest().exec(args);
	}
	
	private MultipleClassChooserDialogUITest() {
		super();
	}

	private void exec(String[] args) {
		if ((args == null) || (args.length == 0)) {
			this.classpath = Classpath.completeClasspath().path();
		}  else {
			this.classpath = args[0];
		}
		MultipleClassChooserDialog dialog = null;
		for (boolean cancel = true; cancel; ) {
			dialog = this.buildDialog();
			dialog.show();
			cancel = dialog.wasCanceled();
		}
		System.out.println("selected classes:");
		for (Iterator stream = dialog.selectedClassDescriptions(); stream.hasNext(); ) {
			System.out.print("\t");
			System.out.print(stream.next());
			System.out.println();
		}
		System.out.println("*****");
		System.exit(0);
	}

	private MultipleClassChooserDialog buildDialog() {
		return new MultipleClassChooserDialog(
						this.buildWorkbenchContext(),
						this.buildClassDescriptionRepository(),
						new ClasspathClassDescription.Adapter()
				);
	}

	/**
	 * 
	 */
	private ClassDescriptionRepository buildClassDescriptionRepository() {
		return new LocalClasspathClassDescriptionRepository(this.classpath);
	}

	private WorkbenchContext buildWorkbenchContext() {
		return new TestWorkbenchContext(null, "org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPluginIconResourceFileNameMap");
	}
	
	private static class LocalClasspathClassDescriptionRepository extends ClasspathClassDescriptionRepository {
		LocalClasspathClassDescriptionRepository(String classpath) {
			super(classpath);
		}

		/**
		 * filter out all the "local" and "anonymous" classes
		 */
		protected Filter classNameFilter() {
			return new Filter() {
				public boolean accept(Object o) {
					return ClassTools.classNamedIsDeclarable((String) o);
				}
			};
		}

	}

}
