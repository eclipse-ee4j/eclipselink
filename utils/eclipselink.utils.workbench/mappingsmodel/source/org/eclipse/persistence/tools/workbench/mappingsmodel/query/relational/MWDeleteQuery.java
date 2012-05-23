package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.ObjectLevelModifyQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;


public final class MWDeleteQuery extends MWAbstractCustomQuery {

	private MWDeleteQuery() {
		super();
	}

	MWDeleteQuery(MWQueryManager queryManager) {
		super(queryManager);
	}

	// ******************* Static Methods *******************

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWDeleteQuery.class);
		descriptor.getDescriptorInheritancePolicy().setParentClass(MWAbstractCustomQuery.class);

		return descriptor;
	}
	
	@Override
	protected DatabaseQuery buildRuntimeQuery() {
		return new DeleteObjectQuery();
	}

}
