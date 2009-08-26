package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import java.util.List;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedure;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public final class MWStoredProcedureQueryFormat extends MWQueryFormat {

	private MWProcedure procedure;
		public static final String PROCEDURE_PROPERTY = "procedure";
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	@SuppressWarnings("unused")
	private MWStoredProcedureQueryFormat() {
		super();
	}

	MWStoredProcedureQueryFormat(MWRelationalSpecificQueryOptions parent) {
		super(parent);
	}

	@Override
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(procedure);
	}

	/**
	 * initialize persistent state
	 */
	@Override
	protected void initialize(Node modelObject) {
		super.initialize(modelObject);
		procedure = new MWProcedure(this);
	}
	
	@Override String getType() {
		return MWRelationalQuery.STORED_PROCEDURE_FORMAT;
	}

	public MWProcedure getProcedure()
	{
		return this.procedure;
	}

	public void setProcedure(MWProcedure procedure)
	{
		MWProcedure old = getProcedure();
		this.procedure = procedure;
		firePropertyChanged(PROCEDURE_PROPERTY, old, procedure);
	}

	//persistence
	public static XMLDescriptor buildDescriptor()
	{
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWStoredProcedureQueryFormat.class);
		descriptor.getInheritancePolicy().setParentClass(MWQueryFormat.class);

		// 1-1 to the procedure
		XMLCompositeObjectMapping procedureMapping = new XMLCompositeObjectMapping();
		procedureMapping.setAttributeName("procedure");
		procedureMapping.setReferenceClass(MWProcedure.class);
		procedureMapping.setXPath("procedure");
		descriptor.addMapping(procedureMapping);

		return descriptor;
	}
	
	public static XMLDescriptor buildStandaloneDescriptor() {
		// Unchanged from standalone
		return buildDescriptor();
	}

	@Override
	void convertFromRuntime(DatabaseQuery runtimeQuery) {};
	
	@Override
	void convertToRuntime(DatabaseQuery runtimeQuery) {
		runtimeQuery.setCall(this.procedure.buildRuntimeCall());		
	}

}
