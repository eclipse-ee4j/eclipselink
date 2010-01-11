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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import java.util.Iterator;
import javax.swing.JLabel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWXmlProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaComplexTypeChooser;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaContextChooser;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaRepositoryValue;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaRootElementChooser;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaValue;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


final class XmlDescriptorComponentFactory extends SwingComponentFactory {


	// **************** Schema Context ****************************************
	
	static JLabel buildSchemaContextLabel(ResourceRepository resourceRepository) {
		return buildLabel("SCHEMA_CONTEXT_CHOOSER_LABEL", resourceRepository);
	}
	
	static SchemaContextChooser buildSchemaContextChooser(ValueModel xmlDescriptorHolder, WorkbenchContextHolder contextHolder, JLabel label) {
		SchemaContextChooser schemaContextChooser = new SchemaContextChooser(contextHolder, buildSchemaRepositoryValue(xmlDescriptorHolder), buildSchemaContextComponentHolder(xmlDescriptorHolder), label);
		contextHolder.getWorkbenchContext().getApplicationContext().getHelpManager().addTopicID(schemaContextChooser, "descriptor.xml.schemaContext");
		return schemaContextChooser;
	}
	
	private static SchemaRepositoryValue buildSchemaRepositoryValue(final ValueModel xmlDescriptorHolder) {
		return new SchemaRepositoryValue() {
			public Iterator schemas() {
				return ((MWXmlProject) ((MWXmlDescriptor) xmlDescriptorHolder.getValue()).getProject()).getSchemaRepository().schemas();
			}
		};
	}
	
	private static PropertyValueModel buildSchemaContextComponentHolder(ValueModel xmlDescriptorHolder) {
		return new PropertyAspectAdapter(xmlDescriptorHolder, MWXmlDescriptor.SCHEMA_CONTEXT_PROPERTY) {
			public Object getValueFromSubject() {
				MWXmlDescriptor descriptor = (MWXmlDescriptor) this.subject;
				return (descriptor.getSchemaContext() == null) ? null : descriptor.getSchemaContext();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWXmlDescriptor) this.subject).setSchemaContext((MWSchemaContextComponent) value);
			}
		};
	}
	
	
	// **************** Default root element **********************************
	
	static JLabel buildDefaultRootElementLabel(ResourceRepository resourceRepository) {
		return buildLabel("DEFAULT_ROOT_ELEMENT_LABEL", resourceRepository);
	}	
	
	static SchemaRootElementChooser buildDefaultRootElementChooser(ValueModel xmlDescriptorHolder, WorkbenchContextHolder contextHolder, JLabel label) {
		return new SchemaRootElementChooser(contextHolder, buildSchemaValue(xmlDescriptorHolder), buildDefaultRootElementHolder(xmlDescriptorHolder), label);
	}
	
	// **************** Default root element type **********************************
	
	static JLabel buildDefaultRootElementTypeLabel(ResourceRepository resourceRepository) {
		return buildLabel("DEFAULT_ROOT_ELEMENT_TYPE_LABEL", resourceRepository);
	}	
	
	static SchemaComplexTypeChooser buildDefaultRootElementTypeChooser(ValueModel oxDescriptorHolder, WorkbenchContextHolder contextHolder, JLabel label) {
		return new SchemaComplexTypeChooser(contextHolder, buildSchemaValue(oxDescriptorHolder), buildDefaultRootElementTypeHolder(oxDescriptorHolder), label);
	}

	private static SchemaValue buildSchemaValue(final ValueModel xmlDescriptorHolder) {
		return new SchemaValue() {
			public MWXmlSchema schema() {
				return schemaContextSchema(xmlDescriptorHolder);
			}
		};
	}
	
	/** Return the schema associated with the descriptor's schema context */
	private static MWXmlSchema schemaContextSchema(ValueModel xmlDescriptorHolder) {
		return (((MWXmlDescriptor) xmlDescriptorHolder.getValue()).getSchemaContext() == null) ? null : ((MWXmlDescriptor) xmlDescriptorHolder.getValue()).getSchemaContext().getSchema();
	}
	
	private static PropertyValueModel buildDefaultRootElementHolder(ValueModel xmlDescriptorHolder) {
		return new PropertyAspectAdapter(xmlDescriptorHolder, MWXmlDescriptor.DEFAULT_ROOT_ELEMENT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWXmlDescriptor) this.subject).getDefaultRootElement();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWXmlDescriptor) this.subject).setDefaultRootElement((MWElementDeclaration) value);
			}
		};
	}
	
	private static PropertyValueModel buildDefaultRootElementTypeHolder(ValueModel oxDescriptorHolder) {
		return new PropertyAspectAdapter(oxDescriptorHolder, MWOXDescriptor.DEFAULT_ROOT_ELEMENT_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWOXDescriptor) this.subject).getDefaultRootElementType();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWOXDescriptor) this.subject).setDefaultRootElementType((MWComplexTypeDefinition) value);
			}
		};
	}

}
