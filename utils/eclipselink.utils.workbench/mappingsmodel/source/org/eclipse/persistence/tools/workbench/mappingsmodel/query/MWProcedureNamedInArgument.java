package org.eclipse.persistence.tools.workbench.mappingsmodel.query;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * @version 1.1
 * @since 1.1
 * @author Les Davis
 */
public final class MWProcedureNamedInArgument extends MWAbstractProcedureArgument
{
	/**
	 * Default constructor - for TopLink use only
	 */
	@SuppressWarnings("unused")
  	private MWProcedureNamedInArgument() {
  		super();
  	}

	MWProcedureNamedInArgument(MWProcedure procedure, String name) {
		super(procedure, name);
	}

	@Override
	public boolean isNamed() {
		return true;
	}

	@Override
	public boolean isNamedIn() {
		return true;
	}
	
	@Override
	public boolean isNamedOut() {
		return false;
	}
	
	@Override
	public boolean isNamedInOut() {
		return false;
	}
	
	@Override
	public boolean isUnnamedIn() {
		return false;
	}
		
	@Override
	public boolean isUnnamedOut() {
		return false;
	}
	
	@Override
	public boolean isUnnamedInOut() {
		return false;
	}

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWProcedureNamedInArgument.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractProcedureArgument.class);

		return descriptor;
	}
	
	protected void addRuntimeEclipseLinkArgument(org.eclipse.persistence.queries.StoredProcedureCall call) {
		if (getPassType().equals(MWAbstractProcedureArgument.VALUE_TYPE)) {
			call.addNamedArgumentValue(getArgumentName(), getArgumentValue());
		} else {
			if (!StringTools.stringIsEmpty(getFieldJavaClassName())) {
				call.addNamedArgument(getArgumentName(), getFieldName(), ClassTools.classForName(getFieldJavaClassName()));
			} else if (StringTools.stringIsEmpty(getFieldSubTypeName()) && StringTools.stringIsEmpty(getFieldName())) {
				call.addNamedArgument(getArgumentName(), getArgumentName(), getFieldSqlTypeCode());
			} else if (StringTools.stringIsEmpty(getFieldSubTypeName())) { 
				call.addNamedArgument(getArgumentName(), getFieldName(), getFieldSqlTypeCode());
			}  else {
				call.addNamedArgument(getArgumentName(), getFieldName(), getFieldSqlTypeCode(), getFieldSubTypeName());
			}
		}
	}

}