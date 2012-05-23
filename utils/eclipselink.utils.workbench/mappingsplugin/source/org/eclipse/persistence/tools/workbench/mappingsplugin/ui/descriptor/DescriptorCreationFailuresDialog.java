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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;
import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.StringRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.DescriptorCreationFailureContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.DescriptorCreationFailureEvent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.StatusDialog;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * Display the errors encountered while attempting to create descriptors
 * from classes
 */
public class DescriptorCreationFailuresDialog extends StatusDialog {

	private final DescriptorCreationFailureContainer failures;

	public DescriptorCreationFailuresDialog(DescriptorCreationFailureContainer failures, WorkbenchContext context) {
		super(context.buildExpandedResourceRepositoryContext(UiDescriptorBundle.class),
			buildStatus(failures),
			"ERROR_CREATING_DESCRIPTORS_TITLE",
			"XML_PROJECTS_DONT_SUPPORT_INTERFACE_DESCRIPTORS_MESSAGE",
			"dialog.descriptorsNotCreated");

		this.failures = failures;
	}

	private static Collection buildStatus(DescriptorCreationFailureContainer failures) {
		SortedSet failuresSet = CollectionTools.sortedSet(failures.failureEvents(), new Comparator() {
			public int compare(Object object1, Object object2) {
				DescriptorCreationFailureEvent event1 = (DescriptorCreationFailureEvent) object1;
				DescriptorCreationFailureEvent event2 = (DescriptorCreationFailureEvent) object2;
				return event1.getClassName().compareTo(event2.getClassName());
			}
		});
		Collection statusList = new Vector(failuresSet.size());

		// Iterator through all the failures
		for (Iterator iter = failuresSet.iterator(); iter.hasNext(); ) {

			// Retrieve the failure
			DescriptorCreationFailureEvent event = (DescriptorCreationFailureEvent) iter.next();
			Error error = new Error(event);

			// Add the status to list
			StatusDialog.Status status = StatusDialog.createStatus(event.getClassName(), Collections.singletonList(error));
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

	//remove the descriptors before returning from the dialog
	protected boolean preConfirm() {		
		Iterator failureEvents = this.failures.failureEvents();
		while (failureEvents.hasNext()){
			
			DescriptorCreationFailureEvent event = (DescriptorCreationFailureEvent) failureEvents.next();
			Object source = event.getSource();
			if (source instanceof MWDescriptor) {
				((MWDescriptor) source).getProject().removeDescriptor((MWDescriptor) source);
			}
		}		return super.preConfirm();
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

		private String errorMessage;
		private final DescriptorCreationFailureEvent event;

		Error(DescriptorCreationFailureEvent event) {
			super();
			this.event = event;
		}

		private String buildErrorMessage(StringRepository repository) {
			return repository.getString(this.event.getResourceStringKey());
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
