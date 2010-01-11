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

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAbstractAnyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlElementTypeableMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaComplexTypeChooser;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaValue;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;


public class XmlMappingComponentFactory 
	extends MappingComponentFactory
{
	// **************** Wildcard checkbox *************************************
	
	/** mappingHolder expects a MWAbstractAnyMapping */
	public static JCheckBox buildWildcardCheckBox(ValueModel mappingHolder, ApplicationContext context) {
		JCheckBox checkBox = 
			buildCheckBox(
				"WILDCARD_CHECK_BOX", 
				new CheckBoxModelAdapter(buildWildcardHolder(mappingHolder)), 
				context.getResourceRepository()
			);
		context.getHelpManager().addTopicID(checkBox, "mapping.wildcard");
		return checkBox;
	}	
	
	private static PropertyValueModel buildWildcardHolder(ValueModel mappingHolder) {
		return new PropertyAspectAdapter(mappingHolder, MWAbstractAnyMapping.WILDCARD_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWAbstractAnyMapping) subject).isWildcardMapping());
			}
			
			protected void setValueOnSubject(Object value) {
				((MWAbstractAnyMapping) subject).setWildcardMapping(((Boolean) value).booleanValue());
			}
		};
	}
	
	// **************** Default root element type **********************************
	
	static JLabel buildElementTypeLabel(ResourceRepository resourceRepository) {
		return buildLabel("ELEMENT_TYPE_LABEL", resourceRepository);
	}	
	
	static SchemaComplexTypeChooser buildElementTypeChooser(ValueModel xmlMappingHolder, WorkbenchContextHolder contextHolder, JLabel label, String propertyString) {
		return new SchemaComplexTypeChooser(contextHolder, buildSchemaValue(xmlMappingHolder), buildElementTypeHolder(xmlMappingHolder, propertyString), label);
	}

	private static SchemaValue buildSchemaValue(final ValueModel xmlMappingHolder) {
		return new SchemaValue() {
			public MWXmlSchema schema() {
				return schemaContextSchema(xmlMappingHolder);
			}
		};
	}

	/** Return the schema associated with the mapping's schema context */
	private static MWXmlSchema schemaContextSchema(ValueModel xmlMappingHolder) {
		return (((MWXmlMapping) xmlMappingHolder.getValue()).schemaContext() == null) ? null : ((MWXmlMapping) xmlMappingHolder.getValue()).schemaContext().getSchema();
	}

	private static PropertyValueModel buildElementTypeHolder(ValueModel mappingHolder, String propertyString) {
		return new PropertyAspectAdapter(mappingHolder, propertyString) {
			protected Object getValueFromSubject() {
				return ((MWXmlElementTypeableMapping) this.subject).getElementType();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWXmlElementTypeableMapping) this.subject).setElementType((MWComplexTypeDefinition) value);
			}
		};
	}
	
}
