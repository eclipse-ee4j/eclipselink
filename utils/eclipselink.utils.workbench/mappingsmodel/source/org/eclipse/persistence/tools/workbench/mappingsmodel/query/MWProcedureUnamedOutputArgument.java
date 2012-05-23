package org.eclipse.persistence.tools.workbench.mappingsmodel.query;


import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * @version 1.1
 * @since 1.1
 * @author Les Davis
 */
public final class MWProcedureUnamedOutputArgument extends MWAbstractProcedureArgument
{
	/**
	 * Default constructor - for TopLink use only
	 */
	@SuppressWarnings("unused")
  	private MWProcedureUnamedOutputArgument() {
  		super();
  	}

	MWProcedureUnamedOutputArgument(MWProcedure procedure) {
		super(procedure, null);
	}

	@Override
	public boolean isNamed() {
		return false;
	}

	@Override
	public boolean isNamedIn() {
		return false;
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
		return true;
	}
	
	@Override
	public boolean isUnnamedInOut() {
		return false;
	}

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWProcedureUnamedOutputArgument.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractProcedureArgument.class);

		return descriptor;
	}
	
	protected void addRuntimeEclipseLinkArgument(StoredProcedureCall call) {
		if (StringTools.stringIsEmpty(getFieldSubTypeName())) { 
			call.addUnamedOutputArgument(getFieldName(), getFieldSqlTypeCode());
		}  else {
			if (!StringTools.stringIsEmpty(getFieldJavaClassName()) && !StringTools.stringIsEmpty(getNestedTypeFieldName())) {
				call.addUnamedOutputArgument(getFieldName(), getFieldSqlTypeCode(), getFieldSubTypeName(), ClassTools.classForName(getFieldJavaClassName()), new DatabaseField(getNestedTypeFieldName()));
			} else if (!StringTools.stringIsEmpty(getFieldJavaClassName())) {
				call.addUnamedOutputArgument(getFieldName(), getFieldSqlTypeCode(), getFieldSubTypeName(), ClassTools.classForName(getFieldJavaClassName()));				
			} else {
				call.addUnamedOutputArgument(getFieldName(), getFieldSqlTypeCode(), getFieldSubTypeName());
			}
		}
	}

}