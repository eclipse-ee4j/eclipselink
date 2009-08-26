package org.eclipse.persistence.tools.workbench.mappingsmodel.query;

import java.util.List;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * @version 1.1
 * @since 1.1
 * @author Les Davis
 */
@SuppressWarnings("nls")
public abstract class MWAbstractProcedureInOutputArgument extends MWAbstractProcedureArgument
{

	private String outFieldName;
		public static final String OUT_FIELD_NAME_PROPERTY = "outFieldName";

	/**
 	 * Default constructor for TopLink use only
 	 */
 	protected MWAbstractProcedureInOutputArgument() {
 		super();
 	}

	MWAbstractProcedureInOutputArgument(MWProcedure manager, String name) {
		super(manager, name);
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.outFieldName = "";
	}

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAbstractProcedureInOutputArgument.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractProcedureArgument.class);

		XMLDirectMapping outFieldNameMapping = new XMLDirectMapping();
		outFieldNameMapping.setAttributeName("outFieldName");
		outFieldNameMapping.setGetMethodName("getOutFieldNameForTopLink");
		outFieldNameMapping.setSetMethodName("setOutFieldNameForTopLink");
		outFieldNameMapping.setXPath("out-field-name/text()");
		descriptor.addMapping(outFieldNameMapping);

		return descriptor;
	}

	@Override
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.buildOutFieldJavaClassRequiredToPassByValueProblem(currentProblems);
	}

	protected void buildOutFieldJavaClassRequiredToPassByValueProblem(List currentProblems) {
		if (VALUE_TYPE.equals(this.getPassType()) && (StringTools.stringIsEmpty(getOutFieldName()) || StringTools.stringIsEmpty(getFieldJavaClassName()))) {
			buildProblem(ProblemConstants.STORED_PROCEDURE_INOUTARGUMENT_REQUIREMENTS);
		}
	}

	//	Getter/Setter for the inField Name
	 public String getOutFieldName(){
		 if (this.outFieldName == null) {
			 return "";
		 } else {
			 return this.outFieldName;
		 }
	 }

	 public void setOutFieldName(String name){
		 String oldOutFieldName = this.outFieldName;
		 this.outFieldName = name;
		 this.firePropertyChanged(OUT_FIELD_NAME_PROPERTY,oldOutFieldName, this.outFieldName);
	 }

	 /**
	  * For toplink  use only
	  */
	 @SuppressWarnings("unused")
	 private void setOutFieldNameForTopLink(String outFieldName){
		 this.outFieldName = outFieldName;
	 }

	 /**
	  * For toplink  use only
	  */
	 @SuppressWarnings("unused")
	 private String getOutFieldNameForTopLink(){
		 return this.outFieldName;
	 }
}
