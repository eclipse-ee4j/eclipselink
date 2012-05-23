package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;

public final class MWCustomReadAllQuery extends MWAbstractCustomQuery {

	//TopLink use only
	private MWCustomReadAllQuery() {
		super();
	}

	MWCustomReadAllQuery(MWQueryManager queryManager) {
		super(queryManager);
	}
	
	// ******************* Static Methods *******************

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWCustomReadAllQuery.class);
		descriptor.getDescriptorInheritancePolicy().setParentClass(MWAbstractCustomQuery.class);

		return descriptor;
	}

	@Override
	protected DatabaseQuery buildRuntimeQuery() {
		return new ReadAllQuery();
	}
}
