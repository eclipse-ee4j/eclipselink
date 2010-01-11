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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db.ColumnPairsPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;


/**
 * this is the panel used to pick (and/or build) the reference from the
 * mapping's "parent" descriptor's table(s) to the reference descriptor,
 * or vice-versa (target foreign key), or some combination of both(!)
 */
final class OneToOneTableReferencePanel
	extends AbstractTableReferencePanel
{

	OneToOneTableReferencePanel(PropertyValueModel oneToOneMappingHolder, WorkbenchContextHolder contextHolder) {
		super(oneToOneMappingHolder, contextHolder);
	}

	/**
	 * add a column for the "target foreign key" flag
	 */
	protected ColumnPairsPanel buildColumnPairsPanel() {
		return new ExpandedColumnPairsPanel(this.getSubjectHolder(), this.getWorkbenchContextHolder(), this.buildTableReferenceHolder());
	}

	/**
	 * the reference source can be on either side;
	 * default to a "source" foreign key
	 */
	protected MWTable defaultNewReferenceSourceTable() {
		Iterator candidateTables = this.mapping().getParentRelationalDescriptor().candidateTables();
		return candidateTables.hasNext() ? (MWTable) candidateTables.next() : null;
	}
	
	/**
	 * the reference target can be on either side
	 * default to a "source" foreign key
	 */
	protected MWTable defaultNewReferenceTargetTable() {
		MWRelationalDescriptor descriptor = (MWRelationalDescriptor) this.mapping().getReferenceDescriptor();
		if (descriptor == null) {
			return null;
		}
		Iterator candidateTables = descriptor.candidateTables();
		return candidateTables.hasNext() ? (MWTable) candidateTables.next() : null;
	}

	private MWOneToOneMapping mapping() {
		return (MWOneToOneMapping) this.subject();
	}
	
	
	// ********** member classes **********
	
	private static class ExpandedColumnPairsPanel extends ColumnPairsPanel {
		private ValueModel mappingHolder;
		
		private ExpandedColumnPairsPanel(ValueModel mappingHolder, WorkbenchContextHolder contextHolder, PropertyValueModel referenceHolder) {
			super(contextHolder, referenceHolder);
			this.mappingHolder = mappingHolder;
		}

		protected ColumnAdapter buildColumnAdapter() {
			return new ExpandedColumnPairsColumnAdapter(this, this.resourceRepository());
		}

		ValueModel getMappingHolder() {
			return this.mappingHolder;
		}

	}

	private static class ExpandedColumnPairsColumnAdapter implements ColumnAdapter {
		private ExpandedColumnPairsPanel panel;
		private ResourceRepository resourceRepository;

		private static final String[] COLUMN_NAME_KEYS = new String[] {
						"ONE_TO_ONE_SOURCE_FIELD_COLUMN",
						"ONE_TO_ONE_TARGET_FIELD_COLUMN",
						"ONE_TO_ONE_TARGET_FOREIGN_KEY"
					};
		private static final int COLUMN_COUNT = 3;
		private static final int SOURCE_COLUMN_COLUMN = 0;
		private static final int TARGET_COLUMN_COLUMN = 1;
		private static final int TARGET_FOREIGN_KEY_COLUMN = 2;


		private ExpandedColumnPairsColumnAdapter(ExpandedColumnPairsPanel panel, ResourceRepository resourceRepository) {
			super();
			this.panel = panel;
			this.resourceRepository = resourceRepository;
		}

		// ********** ColumnAdapter implementation **********

		public PropertyValueModel[] cellModels(Object subject) {
			MWColumnPair columnPair = (MWColumnPair) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[SOURCE_COLUMN_COLUMN] = this.buildSourceColumnAdapter(columnPair);
			result[TARGET_COLUMN_COLUMN] = this.buildTargetColumnAdapter(columnPair);
			result[TARGET_FOREIGN_KEY_COLUMN] = this.buildTargetForeignKeyAdapter(columnPair);

			return result;
		}

		public Class getColumnClass(int index) {
			switch (index) {
				case TARGET_FOREIGN_KEY_COLUMN:
					return Boolean.class;

				default:
					return Object.class;
			}
		}

		public int getColumnCount() {
			return COLUMN_COUNT;
		}

		public String getColumnName(int index) {
			return this.resourceRepository.getString(COLUMN_NAME_KEYS[index]);
		}

		public boolean isColumnEditable(int index) {
			return true;
		}

		// ********** internal methods **********

		private PropertyValueModel buildSourceColumnAdapter(MWColumnPair columnPair) {
			PropertyValueModel adapter = new PropertyAspectAdapter(MWColumnPair.SOURCE_COLUMN_PROPERTY, columnPair) {
				protected Object getValueFromSubject() {
					return ((MWColumnPair) this.subject).getSourceColumn();
				}
				protected void setValueOnSubject(Object value) {
					((MWColumnPair) this.subject).setSourceColumn((MWColumn) value);
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWColumn.NAME_PROPERTY);
		}

		private PropertyValueModel buildTargetColumnAdapter(MWColumnPair columnPair) {
			PropertyValueModel adapter = new PropertyAspectAdapter(MWColumnPair.TARGET_COLUMN_PROPERTY, columnPair) {
				protected Object getValueFromSubject() {
					return ((MWColumnPair) this.subject).getTargetColumn();
				}
				protected void setValueOnSubject(Object value) {
					((MWColumnPair) this.subject).setTargetColumn((MWColumn) value);
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWColumn.NAME_PROPERTY);
		}

		ValueModel mappingHolder() {
			return this.panel.getMappingHolder();
		}

		private PropertyValueModel buildTargetForeignKeyAdapter(MWColumnPair columnPair) {
			return new TargetForeignKeyAdapter(this.buildTargetForeignKeysAdapter(), this, columnPair);
		}

		private CollectionValueModel buildTargetForeignKeysAdapter() {
			return new CollectionAspectAdapter(this.mappingHolder(), MWOneToOneMapping.TARGET_FOREIGN_KEYS_COLLECTION) {
				protected Iterator getValueFromSubject() {
					return ((MWOneToOneMapping) this.subject).targetForeignKeys();
				}
			};
		}

	}

	private static class TargetForeignKeyAdapter extends CollectionPropertyValueModelAdapter {
		private ExpandedColumnPairsColumnAdapter columnAdapter;
		private MWColumnPair columnPair;

		public TargetForeignKeyAdapter(CollectionValueModel collectionHolder, ExpandedColumnPairsColumnAdapter columnAdapter, MWColumnPair columnPair) {
			super(collectionHolder);
			this.columnAdapter = columnAdapter;
			this.columnPair = columnPair;
		}

		// ********** ValueModel implementation **********

		/**
		 * always return a Boolean
		 */
		public Object getValue() {
			Object result = super.getValue();
			return (result == null) ? Boolean.FALSE : result;
		}

		// ********** PropertyValueModel implementation **********

		public void setValue(Object value) {
			if (this.booleanValue()) {
				if ( ! this.booleanValueOf(value)) {
					// the value is changing from true to false
					this.mapping().removeTargetForeignKey(this.columnPair);
				}
			} else {
				if (this.booleanValueOf(value)) {
					// the value is changing from false to true
					this.mapping().addTargetForeignKey(this.columnPair);
				}
			}
		}

		// ********** CollectionPropertyValueModelAdapter implementation **********

		protected Object buildValue() {
			return Boolean.valueOf(this.columnPairIsTargetForeignKey());
		}

		// ********** internal methods **********

		private boolean booleanValue() {
			return this.booleanValueOf(this.value);
		}

		private boolean booleanValueOf(Object b) {
			return (b == null) ? false : ((Boolean) b).booleanValue();
		}

		private ValueModel mappingHolder() {
			return this.columnAdapter.mappingHolder();
		}

		private MWOneToOneMapping mapping() {
			return (MWOneToOneMapping) this.mappingHolder().getValue();
		}

		private boolean columnPairIsTargetForeignKey() {
			MWOneToOneMapping mapping = this.mapping();
			if (mapping == null) {
				return false;		// the mapping will be null when the table is cleared out
			}
			return mapping.containsTargetForeignKey(this.columnPair);
		}

	}

}
