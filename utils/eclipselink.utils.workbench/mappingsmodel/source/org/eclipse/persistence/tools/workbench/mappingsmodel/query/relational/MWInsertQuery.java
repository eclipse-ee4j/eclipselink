package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.ObjectLevelModifyQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;

public final class MWInsertQuery extends MWAbstractCustomQuery {


	//TopLink use only
	private MWInsertQuery() {
		super();
	}

	public MWInsertQuery(MWQueryManager queryManager) {
		super(queryManager);
	}
	
	// ******************* Static Methods *******************

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWInsertQuery.class);
		descriptor.getDescriptorInheritancePolicy().setParentClass(MWAbstractCustomQuery.class);

		return descriptor;
	}

	@Override
	protected DatabaseQuery buildRuntimeQuery() {
		return new InsertObjectQuery();
	}
	
}
