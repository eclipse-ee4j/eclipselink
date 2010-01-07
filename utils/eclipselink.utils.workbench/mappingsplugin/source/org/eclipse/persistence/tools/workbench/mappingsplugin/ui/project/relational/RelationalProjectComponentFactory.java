/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational;

import java.util.Iterator;

import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.TableCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ExtendedListValueModelWrapper;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;


public class RelationalProjectComponentFactory extends SwingComponentFactory {

	public static ListChooser buildTableChooser(PropertyValueModel mwModelHolder, PropertyValueModel selectedTableHolder, DefaultListChooserDialog.Builder dialogBuilder, WorkbenchContextHolder contextHolder) {
		ListChooser listChooser = 
			new DefaultListChooser(
				new ComboBoxModelAdapter(
					buildExtendedTablesHolder(mwModelHolder),  
					selectedTableHolder
				),
				contextHolder,
                buildTableNodeSelector(contextHolder),
				dialogBuilder 
			);
		listChooser.setRenderer(buildMWTableListCellRenderer(contextHolder.getWorkbenchContext().getApplicationContext().getResourceRepository()));

		return listChooser;
	}

	/**
	 * the database will never change, but the MWModel might
	 */
	private static ValueModel buildDatabaseAdapter(PropertyValueModel mwModelHolder) {	
		return new TransformationPropertyValueModel(mwModelHolder) {
			protected Object transform(Object value) {
				return (value == null) ? null : ((MWModel) value).getDatabase();
			}
			protected Object reverseTransform(Object value) {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * tack a null on the front of the list of tables
	 * (to represent "none selected")
	 */
	public static ListValueModel buildExtendedTablesHolder(PropertyValueModel mwModelHolder) {
		return new ExtendedListValueModelWrapper(buildSortedTablesHolder(buildDatabaseAdapter(mwModelHolder)));
	}

	/**
	 * sort the list of tables
	 */
	private static ListValueModel buildSortedTablesHolder(ValueModel databaseHolder) {
		return new SortedListValueModelAdapter(buildTableNameAdapter(databaseHolder));
	}
	
	/**
	 * listen to each table's name
	 */
	private static ListValueModel buildTableNameAdapter(ValueModel databaseHolder) {
		return new ItemPropertyListValueModelAdapter(buildTablesAdapter(databaseHolder), MWTable.QUALIFIED_NAME_PROPERTY);
	}

	/**
	 * adapt the database's tables
	 */
	private static CollectionValueModel buildTablesAdapter(ValueModel databaseHolder) {
		return new CollectionAspectAdapter(databaseHolder, MWDatabase.TABLES_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWDatabase) this.subject).tables();
			}
			protected int sizeFromSubject() {
				return ((MWDatabase) this.subject).tablesSize();
			}
		};
	}

	private static ListCellRenderer buildMWTableListCellRenderer(ResourceRepository resourceRepository) {
		return new AdaptableListCellRenderer(new TableCellRendererAdapter(resourceRepository));
	}

    
    public static NodeSelector buildTableNodeSelector(final WorkbenchContextHolder contextHolder) {
        return new NodeSelector() {       
            public void selectNodeFor(Object item) {
                RelationalProjectNode projectNode = (RelationalProjectNode) contextHolder.getWorkbenchContext().getNavigatorSelectionModel().getSelectedProjectNodes()[0];
                projectNode.selectTableNodeFor((MWTable) item, contextHolder.getWorkbenchContext().getNavigatorSelectionModel());
            }
        };
    }
}
