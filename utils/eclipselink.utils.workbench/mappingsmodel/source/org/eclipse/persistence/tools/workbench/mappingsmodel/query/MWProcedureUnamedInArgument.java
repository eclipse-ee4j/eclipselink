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
public final class MWProcedureUnamedInArgument extends MWAbstractProcedureArgument
{
	/**
	 * Default constructor - for TopLink use only
	 */
	@SuppressWarnings("unused")
  	private MWProcedureUnamedInArgument() {
  		super();
  	}

	MWProcedureUnamedInArgument(MWProcedure procedure) {
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
		return true;
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
		descriptor.setJavaClass(MWProcedureUnamedInArgument.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractProcedureArgument.class);

		return descriptor;
	}
	
	protected void addRuntimeEclipseLinkArgument(StoredProcedureCall call) {
		if (getPassType().equals(MWAbstractProcedureArgument.VALUE_TYPE)) {
			call.addUnamedArgumentValue(getArgumentValue());
		} else {
			if (StringTools.stringIsEmpty(getFieldSubTypeName())) {
				if (!StringTools.stringIsEmpty(getFieldJavaClassName())) {
					call.addUnamedArgument(getFieldName(), ClassTools.classForName(getFieldJavaClassName()));
				} else {
					call.addUnamedArgument(getFieldName(), getFieldSqlTypeCode());
				}
			} else {
				if (StringTools.stringIsEmpty(getNestedTypeFieldName())) {
					call.addUnamedArgument(getFieldName(), getFieldSqlTypeCode(), getFieldSubTypeName(), new DatabaseField(getNestedTypeFieldName()));
				} else {
					call.addUnamedArgument(getFieldName(), getFieldSqlTypeCode(), getFieldSubTypeName());
				}
			}
		}
	}

}
