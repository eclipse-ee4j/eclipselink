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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ColumnCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.TransactionalDescriptorComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;



final class RelationalDescriptorComponentFactory extends TransactionalDescriptorComponentFactory {


	static DefaultListChooserDialog buildReturningPolicyFieldDialog(
		MWRelationalClassDescriptor parentDescriptor, Collection fieldsToExclude, WorkbenchContext context, String topidId
	) {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("DATABASE_FIELD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("DATABASE_FIELD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildColumnStringConverter());
		builder.setListCellRenderer(buildColumnRenderer(context.getApplicationContext().getResourceRepository()));
		builder.setHelpTopicId(topidId);
		
		Collection eligibleFields = buildEligibleColumns(parentDescriptor, fieldsToExclude);
		builder.setCompleteList(CollectionTools.array(eligibleFields.iterator(), new MWColumn[eligibleFields.size()]));
		
		return new DefaultListChooserDialog(context, builder); 
	}

	private static StringConverter buildColumnStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWColumn) o).qualifiedName();
			}
		};
	}
	
	private static ListCellRenderer buildColumnRenderer(ResourceRepository resourceRepository) {
		return new AdaptableListCellRenderer(new ColumnCellRendererAdapter(resourceRepository));
	}

	private static Collection buildEligibleColumns(MWRelationalClassDescriptor desc, Collection fieldsToExclude) {
		Collection columns = new TreeSet();
		for (Iterator stream = desc.associatedTables(); stream.hasNext(); ) {
			MWTable table = (MWTable) stream.next();
			CollectionTools.addAll(columns, findEligibleColumns(table.columns(), fieldsToExclude));
		}
		return columns;
	}
	
	private static Iterator findEligibleColumns(Iterator tableFields, final Collection fieldsToExclude) {
		return new FilteringIterator(tableFields) {
			protected boolean accept(Object next) {
				return ! fieldsToExclude.contains(next);
			}
		};
	}

}
