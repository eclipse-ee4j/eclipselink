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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.table.TableCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.FieldTransformerAssociationsPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTableCellRenderer;


final class XmlFieldTransformerAssociationsPanel
	extends FieldTransformerAssociationsPanel
{
	// **************** Constructors ******************************************
	
	/** Expects a MWXmlTransformationMapping object */
	XmlFieldTransformerAssociationsPanel(ValueModel transformationMappingHolder, WorkbenchContextHolder contextHolder) {
		super(transformationMappingHolder, contextHolder);
	}
	
	
	// **************** Initialization ****************************************
	
	protected ActionListener buildAddFieldTransformerAssociationAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				MWXmlTransformationMapping transformationMapping = 
					(MWXmlTransformationMapping) XmlFieldTransformerAssociationsPanel.this.getSubjectHolder().getValue();
				WorkbenchContext context = 
					XmlFieldTransformerAssociationsPanel.this.getWorkbenchContext();
				
				XmlFieldTransformerAssociationEditingDialog.promptToAddFieldTransformerAssociation(transformationMapping, context);
			}
		};
	}
	
	protected ActionListener buildEditFieldTransformerAssociationAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				MWXmlFieldTransformerAssociation association = 
					(MWXmlFieldTransformerAssociation) XmlFieldTransformerAssociationsPanel.this.selectedFieldTransformerAssociation();
				WorkbenchContext context = 
					XmlFieldTransformerAssociationsPanel.this.getWorkbenchContext();
				
				XmlFieldTransformerAssociationEditingDialog.promptToEditFieldTransformerAssociation(association, context);
			}
		};
	}
	
	protected ColumnAdapter buildFieldTransformerAssociationsColumnAdapter() {
		return new XmlFieldTransformationAssociationsColumnAdapter(this.resourceRepository());
	}
	
	protected TableCellRenderer buildFieldColumnCellRenderer() {
		return new SimpleTableCellRenderer() {
			protected String buildText(Object value) {
				if ("".equals(value)) {
					return XmlFieldTransformerAssociationsPanel.this.resourceRepository().getString("NONE_SELECTED");
				}
				else {
					return (String) value;
				}
			}
		};
	}
	
	
	// **************** Member classes ****************************************
	
	protected static class XmlFieldTransformationAssociationsColumnAdapter
		extends FieldTransformationAssociationsColumnAdapter
	{
		XmlFieldTransformationAssociationsColumnAdapter(ResourceRepository resourceRepository) {
			super(resourceRepository);
		}
		
		public String getColumnName(int index) {
			if (index == FIELD_COLUMN) {
				return this.resourceRepository.getString("TRANSFORMATION_MAPPING_XPATH_COLUMN_LABEL");
			}
			else {
				return super.getColumnName(index);
			}
		}
		
		public Class getColumnClass(int index) {
			if (index == FIELD_COLUMN) {
				return String.class;
			}
			else {
				return super.getColumnClass(index);
			}
		}
		
		protected PropertyValueModel buildFieldAdapter(MWFieldTransformerAssociation association) {
			return new PropertyAspectAdapter(
				MWXmlField.XPATH_PROPERTY, 
				((MWXmlFieldTransformerAssociation) association).getXmlField()
			) {
				protected Object getValueFromSubject() {
					return ((MWXmlField) this.subject).getXpath();
				}
			};
		}
	}
}
