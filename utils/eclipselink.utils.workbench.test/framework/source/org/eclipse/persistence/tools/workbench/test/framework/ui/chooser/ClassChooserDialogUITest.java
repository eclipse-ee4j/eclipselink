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
package org.eclipse.persistence.tools.workbench.test.framework.ui.chooser;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClasspathClassDescription;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClasspathClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


/**
 * Simple test class for playing around with the ClassChooserDialog.
 * 
 * Optional command line parm:
 * 	the classpath used to populate the "available" classes list;
 * if this is not specified, all the classes on the current classpath will be used
 */
public class ClassChooserDialogUITest {
	private String classpath;


	public static void main(String[] args) throws Exception {
		new ClassChooserDialogUITest().exec(args);
	}
	
	private ClassChooserDialogUITest() {
		super();
	}

	private void exec(String[] args) throws Exception {
		if ((args == null) || (args.length == 0)) {
			this.classpath = Classpath.completeClasspath().path();
		}  else {
			this.classpath = args[0];
		}

		UIManager.setLookAndFeel(this.lookAndFeel());
		Window window = new JFrame(ClassTools.shortClassNameForObject(this));
		window.setLocation(300, 300);
		window.setSize(400, 150);
		window.setVisible(true);

		ClassDescriptionRepository repository = buildClassDescriptionRepository(this.classpath);

		ClassChooserDialog dialog = null;
		for (boolean cancel = true; cancel; ) {
			dialog = this.buildDialog(repository, window);
			dialog.setInitialSelection(this.initialSelection(repository));
			dialog.show();
			cancel = dialog.wasCanceled();
		}
		System.out.println("selected class: " + dialog.selection());
		System.exit(0);
	}

	private ClassChooserDialog buildDialog(ClassDescriptionRepository repository, Window window) {
		return ClassChooserDialog.createDialog(
						repository,
						new ClasspathClassDescription.Adapter(),
						this.buildWorkbenchContext(window)
				);
	}

	private Object initialSelection(ClassDescriptionRepository repository) {
		for (Iterator stream = repository.classDescriptions(); stream.hasNext(); ) {
			ClasspathClassDescription ccd = (ClasspathClassDescription) stream.next();
			if (ccd.getClassName().equals("java.util.Vector")) {
				return ccd;
			}
		}
		return repository.classDescriptions().next();
	}

	private String lookAndFeel() {
		String laf;
		laf = UIManager.getSystemLookAndFeelClassName();
//		laf = UIManager.getCrossPlatformLookAndFeelClassName();	// Metal LAF
//		laf = com.sun.java.swing.plaf.windows.WindowsLookAndFeel.class.getName();
//		laf = com.sun.java.swing.plaf.motif.MotifLookAndFeel.class.getName();
//		laf = oracle.bali.ewt.olaf.OracleLookAndFeel.class.getName();
		return laf;
	}

	static ClassDescriptionRepository buildClassDescriptionRepository(String classpath) {
		return new LocalClasspathClassDescriptionRepository(classpath);
	}

	private WorkbenchContext buildWorkbenchContext(final Window window) {
		return new TestWorkbenchContext() {
			public Window getCurrentWindow() {
				return window;
			}
		};
	}

	private static class LocalClasspathClassDescriptionRepository extends ClasspathClassDescriptionRepository {
		private Collection classDescriptions;
		LocalClasspathClassDescriptionRepository(String classpath) {
			super(classpath);
		}

		public Iterator classDescriptions() {
			if (this.classDescriptions == null) {
				this.classDescriptions = new ArrayList(10000);
				CollectionTools.addAll(this.classDescriptions, super.classDescriptions());
			}
			return this.classDescriptions.iterator();
		}

		public void refreshClassDescriptions() {
			this.classDescriptions = null;
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
