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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta;

import java.awt.Dialog;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;
import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.StringRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.ExternalClassLoadFailureContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.StatusDialog;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * Display the errors encountered while attempting to load
 * "external" classes from the "external" class repository.
 */
public class ExternalClassLoadFailuresDialog extends StatusDialog {

	public ExternalClassLoadFailuresDialog( 
		WorkbenchContext context, ExternalClassLoadFailureContainer failures
	) {
		super(
			context.buildExpandedResourceRepositoryContext(UiMetaBundle.class),
			buildStatus(failures),
			"ERROR_IMPORTING_CLASSES.TITLE",
			"CLASSES_COMPILED_AND_ON_CLASSPATH_ERROR_MESSAGE",
			"dialog.classesNotFound"
		);
	}
	
	public ExternalClassLoadFailuresDialog(
		WorkbenchContext context, Dialog owner, ExternalClassLoadFailureContainer failures
	) {
		super(
			context.buildExpandedResourceRepositoryContext(UiMetaBundle.class),
			owner,
			buildStatus(failures),
			"ERROR_IMPORTING_CLASSES.TITLE",
			"CLASSES_COMPILED_AND_ON_CLASSPATH_ERROR_MESSAGE",
			"dialog.classesNotFound"
		);
	}
	
	private static Collection buildStatus(ExternalClassLoadFailureContainer failures) {
		SortedSet failureClassNames = CollectionTools.sortedSet(failures.failureClassNames());
		Collection statusList = new Vector(failureClassNames.size());

		// Iterator through all the failures
		for (Iterator iter = failureClassNames.iterator(); iter.hasNext(); ) {
			Vector errors = new Vector();

			// Retrieve the failure
			String className = (String) iter.next();
			Throwable cause = failures.failureForClassNamed(className);

			// Add the main failure
			errors.add(new Error(cause));

			// Add the causes of the failure
			while (cause.getCause() != null) {
				cause = cause.getCause();
				// NPE with no description will not be displayed since the main
				// cause is sufficient
				if (!(cause instanceof NullPointerException) && (cause.getLocalizedMessage() != null)) {
					errors.add(new Error(cause));
				}
			}

			// Add the status to list
			StatusDialog.Status status = StatusDialog.createStatus(className, errors);
			statusList.add(status);
		}

		return statusList;
	}

	protected CellRendererAdapter buildNodeRenderer(Object value) {
		if (value instanceof String)
			return new ClassNameCellRendererAdapter();

		if (value instanceof Error)
			return new ErrorCellRendererAdapter();

		return super.buildNodeRenderer(value);
	}

	protected class ClassNameCellRendererAdapter extends AbstractCellRendererAdapter {
		public Icon buildIcon(Object value) {
			return resourceRepository().getIcon("class.public");
		}

		public String buildText(Object value) {
			String className = (String) value;

			if (className.indexOf(".") == -1) {
				return className + " " + resourceRepository().getString("DEFAULT_PACKAGE");
			}

			return ClassTools.shortNameForClassNamed(className) + " (" + ClassTools.packageNameForClassNamed(className) + ")";
		}
	}

	private static class Error {
		private final Throwable error;
		private String errorMessage;

		Error(Throwable error) {
			super();
			this.error = error;
		}

		private String buildErrorMessage(StringRepository repository) {
			// Retrieve specific error message
			if (this.error instanceof ExternalClassNotFoundException) {
				return repository.getString("ERROR_IMPORTING_CLASSES_ERROR_MESSAGE_CLASS_NOT_FOUND");
			}

			if (this.error instanceof InterfaceDescriptorCreationException) {
				return repository.getString("ERROR_IMPORTING_CLASSES_ERROR_MESSAGE_INTERFACE");
			}

			if (this.error instanceof IOException) {
				return repository.getString("ERROR_IMPORTING_CLASSES_ERROR_MESSAGE_IO", this.error.getLocalizedMessage());
			}

			// Not suitable since we can't i18n but we have to show something
			if (this.error.getLocalizedMessage() != null)
				return this.error.getLocalizedMessage();

			// Last resort
			return this.error.toString();
		}

		public String getErrorMessage(StringRepository repository) {
			if (this.errorMessage == null) {
				this.errorMessage = buildErrorMessage(repository);
			}
			return this.errorMessage;
		}
	}

	private class ErrorCellRendererAdapter extends AbstractCellRendererAdapter {
		public Icon buildIcon(Object value) {
			return resourceRepository().getIcon("error");
		}

		public String buildText(Object value) {
			return ((Error) value).getErrorMessage(resourceRepository());
		}
	}
}
