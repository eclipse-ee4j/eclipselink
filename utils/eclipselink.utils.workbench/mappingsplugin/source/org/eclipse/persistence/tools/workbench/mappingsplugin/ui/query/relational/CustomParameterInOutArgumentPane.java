package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractProcedureArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractProcedureInOutputArgument;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;

public final class CustomParameterInOutArgumentPane extends AbstractSubjectPanel {


	public CustomParameterInOutArgumentPane(PropertyValueModel argumentHolder, WorkbenchContextHolder workbenchContextHolder) {
		super(argumentHolder, workbenchContextHolder);
	}
		
	@Override
	protected void initializeLayout() {
		
		GridBagConstraints constraints = new GridBagConstraints();

		JComponent inFieldChooser = buildLabeledTextField("STORED_PROCEDURE_PROPERTIES_PAGE_INFIELD_NAME_COLUMN", buildInArgumentDocumentModel());
		
		constraints.gridx      	= 0;
		constraints.gridy      	= 0;
		constraints.gridwidth  	= 2;
		constraints.gridheight 	= 1;
		constraints.weightx    	= 1;
		constraints.weighty    	= 0;
		constraints.fill       	= GridBagConstraints.HORIZONTAL;
		constraints.anchor     	= GridBagConstraints.PAGE_START;
		constraints.insets 		= new Insets(5, 0, 0, 0);
		
		add(inFieldChooser, constraints);

		JComponent outFieldField = buildLabeledTextField("STORED_PROCEDURE_PROPERTIES_PAGE_OUTFIELD_NAME_COLUMN", buildOutFieldNameDocument());
		
		constraints.gridx      	= 0;
		constraints.gridy      	= 1;
		constraints.gridwidth  	= 2;
		constraints.gridheight 	= 1;
		constraints.weightx    	= 1;
		constraints.weighty    	= 0;
		constraints.fill       	= GridBagConstraints.HORIZONTAL;
		constraints.anchor     	= GridBagConstraints.PAGE_START;
		constraints.insets 		= new Insets(10, 0, 0, 0);
		
		add(outFieldField, constraints);

		JComponent sqlTypeChooser = buildLabeledComboBox("STORED_PROCEDURE_PROPERTIES_PAGE_FIELD_SQL_TYPE_NAME_COLUMN", buildFieldSQLTypeComboboxModel());
	
		constraints.gridx      	= 0;
		constraints.gridy      	= 2;
		constraints.gridwidth  	= 2;
		constraints.gridheight 	= 1;
		constraints.weightx    	= 1;
		constraints.weighty    	= 0;
		constraints.fill       	= GridBagConstraints.HORIZONTAL;
		constraints.anchor     	= GridBagConstraints.PAGE_START;
		constraints.insets 		= new Insets(10, 0, 0, 0);
		
		add(sqlTypeChooser, constraints);
		
		JComponent sqlSubTypeChooser = buildLabeledComboBox("STORED_PROCEDURE_PROPERTIES_PAGE_FIELD_SQL_SUB_TYPE_NAME_COLUMN", buildFieldSubTypeSQLTypeComboboxModel());

		constraints.gridx      	= 0;
		constraints.gridy      	= 3;
		constraints.gridwidth  	= 2;
		constraints.gridheight 	= 1;
		constraints.weightx    	= 1;
		constraints.weighty    	= 0;
		constraints.fill       	= GridBagConstraints.HORIZONTAL;
		constraints.anchor     	= GridBagConstraints.PAGE_START;
		constraints.insets 		= new Insets(10, 0, 0, 0);

		add(sqlSubTypeChooser, constraints);
		
		JLabel javaTypeChooserLabel = buildLabel("STORED_PROCEDURE_PROPERTIES_PAGE_JAVA_CLASS_TYPE_COLUMN");
		
		constraints.gridx      	= 0;
		constraints.gridy      	= 4;
		constraints.gridwidth  	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx    	= 0;
		constraints.weighty    	= 0;
		constraints.fill       	= GridBagConstraints.HORIZONTAL;
		constraints.anchor     	= GridBagConstraints.PAGE_START;
		constraints.insets 		= new Insets(10, 0, 0, 40);

		add(javaTypeChooserLabel, constraints);

		JComponent javaTypeChooser = ClassChooserTools.buildPanel(buildClassTransformer(), 
				buildClassRepositoryHolder(),
				ClassChooserTools.buildDeclarableReferenceFilter(),
				getWorkbenchContextHolder());
		
		constraints.gridx      	= 1;
		constraints.gridy      	= 4;
		constraints.gridwidth  	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx    	= 1;
		constraints.weighty    	= 0;
		constraints.fill       	= GridBagConstraints.HORIZONTAL;
		constraints.anchor     	= GridBagConstraints.PAGE_START;
		constraints.insets 		= new Insets(10, 0, 0, 0);
		javaTypeChooserLabel.setLabelFor(javaTypeChooser);
		
		add(javaTypeChooser, constraints);
		
		JComponent nestedFieldField = buildLabeledTextField("STORED_PROCEDURE_PROPERTIES_PAGE_NESTED_TYPE_FIELD_NAME_COLUMN", buildNestedTypeFieldNameDocument());
		
		constraints.gridx      	= 0;
		constraints.gridy      	= 5;
		constraints.gridwidth  	= 2;
		constraints.gridheight 	= 1;
		constraints.weightx    	= 1;
		constraints.weighty    	= 1;
		constraints.fill       	= GridBagConstraints.HORIZONTAL;
		constraints.anchor     	= GridBagConstraints.PAGE_START;
		constraints.insets 		= new Insets(10, 0, 0, 0);
	
		add(nestedFieldField, constraints);	
		
	}
	
	private Document buildInArgumentDocumentModel() {
		return new DocumentAdapter(buildArgumentFieldNameAdapter());
	}
	
	private ComboBoxModelAdapter buildFieldSQLTypeComboboxModel() {
		return new ComboBoxModelAdapter(buildJdbcTypesListHolder(), buildFieldTypeAdapter());
	}

	private ComboBoxModelAdapter buildFieldSubTypeSQLTypeComboboxModel() {
		return new ComboBoxModelAdapter(buildJdbcTypesListHolder(), buildFieldSubTypeNameAdapter());
	}
	
	protected PropertyValueModel buildClassTransformer() {
		return new TransformationPropertyValueModel(buildClassNameHolder()) {
			@Override
			protected Object transform(Object value) {
				if (value == null || "".equals((String)value) || subject() == null) {
					return null;
				}
				return ((MWAbstractProcedureArgument)subject()).typeNamed((String)value);
			}
			@Override
			protected Object reverseTransform(Object value) {
				if (value == null) {
					return null;
				}
				return ((MWClass)value).fullName();
			}
		};
	}
	
	protected PropertyValueModel buildClassNameHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWAbstractProcedureArgument.FIELD_JAVA_CLASS_NAME_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWAbstractProcedureArgument)subject).getFieldJavaClassName();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWAbstractProcedureArgument)subject).setFieldJavaClassName((String)value);
			}
		};
	}
	
	private PropertyValueModel buildArgumentFieldNameAdapter() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWAbstractProcedureArgument.FIELD_NAME_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWAbstractProcedureArgument)subject).getFieldName();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWAbstractProcedureArgument)subject).setFieldName((String)value);
			}
		};
	}
	
	private Document buildOutFieldNameDocument() {
		return new DocumentAdapter(buildArgumentOutFieldNameAdapter());
	}
	

	private PropertyValueModel buildArgumentOutFieldNameAdapter() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWAbstractProcedureInOutputArgument.OUT_FIELD_NAME_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWAbstractProcedureInOutputArgument)subject).getOutFieldName();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWAbstractProcedureInOutputArgument)subject).setOutFieldName((String)value);
			}
		};
	}


	private PropertyValueModel buildFieldTypeAdapter() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWAbstractProcedureArgument.FIELD_SQL_TYPE_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWAbstractProcedureArgument)subject).getFieldSqlTypeName();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWAbstractProcedureArgument)subject).setFieldSqlTypeName((String)value);
			}
		};
	}
	
	private PropertyValueModel buildFieldSubTypeNameAdapter() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWAbstractProcedureArgument.FIELD_SQL_TYPE_NAME_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWAbstractProcedureArgument)subject).getFieldSubTypeName();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWAbstractProcedureArgument)subject).setFieldSubTypeName((String)value);
			}
		};
	}

	private Document buildNestedTypeFieldNameDocument() {
		return new DocumentAdapter(buildNestedTypeFieldNameAdapter());
	}
	
	private PropertyValueModel buildNestedTypeFieldNameAdapter() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWAbstractProcedureArgument.NESTED_TYPE_FIELD_NAME_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWAbstractProcedureArgument)subject).getNestedTypeFieldName();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWAbstractProcedureArgument)subject).setNestedTypeFieldName((String)value);
			}
		};
	}
	
	private CollectionValueModel buildJdbcTypesHolder() {
		return new CollectionAspectAdapter(getSubjectHolder(), MWAbstractProcedureInOutputArgument.JDBC_TYPES_PROPERTY) {
			@Override
			protected Iterator<String> getValueFromSubject() {
				return MWAbstractProcedureInOutputArgument.jdbcTypeNames();
			}
			@Override
			protected int sizeFromSubject() {
				return MWAbstractProcedureInOutputArgument.jdbcTypesSize();
			}
		};
	}

	private ListValueModel buildJdbcTypesListHolder() {
		return new CollectionListValueModelAdapter(buildJdbcTypesHolder());
	}
	
	private ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return ((MWModel) CustomParameterInOutArgumentPane.this.subject()).getRepository();
			}
		};
	}

//	private ValueModel<String> buildDefaultInFieldNameModel() {
//		return new SimplePropertyValueModel<String>("in_Field");
//	}
	
}
