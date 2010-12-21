package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import java.util.List;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

public abstract class MWAbstractCustomQuery extends MWModel	implements MWRelationalQuery {

	private volatile MWRelationalSpecificQueryOptions relationalOptions;

	//TopLink use only
	protected MWAbstractCustomQuery() {
		super();
	}

	MWAbstractCustomQuery(MWQueryManager queryManager) {
		super(queryManager);
	}

	@Override
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.relationalOptions = new MWRelationalSpecificQueryOptions(this);
		this.relationalOptions.setQueryFormatToSql();
	}

	@Override
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.relationalOptions);
	}

	// ******************* Static Methods *******************

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWAbstractCustomQuery.class);

		InheritancePolicy ip = descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWInsertQuery.class, "insert");
		ip.addClassIndicator(MWDeleteQuery.class, "delete");
		ip.addClassIndicator(MWUpdateQuery.class, "update");
		ip.addClassIndicator(MWCustomReadObjectQuery.class, "read-object");
		ip.addClassIndicator(MWCustomReadAllQuery.class, "read-all");

		XMLCompositeObjectMapping relationalOptionsMaping = new XMLCompositeObjectMapping();
		relationalOptionsMaping.setAttributeName("relationalOptions");
		relationalOptionsMaping.setReferenceClass(MWRelationalSpecificQueryOptions.class);
		relationalOptionsMaping.setXPath("relational-options");
		descriptor.addMapping(relationalOptionsMaping);

		return descriptor;
	}

	public void formatSetToEjbql() {
		// Nothing needs to be done here
	}
	
	public void formatSetToSql() {
		// Nothing needs to be done here
	}
	
	// ******************* Accessors *******************

	public MWRelationalSpecificQueryOptions getRelationalOptions() {
		return this.relationalOptions;
	}

	public String getQueryFormatType() {
		return this.relationalOptions.getQueryFormatType();
	}

	public void setQueryFormatType(String type) {
		this.relationalOptions.setQueryFormatType(type);
	}

	public MWQueryFormat getQueryFormat() {
		return this.relationalOptions.getQueryFormat();
	}

	public void setSQLString(String sql) {
		setQueryFormatType(MWRelationalQuery.SQL_FORMAT);
		((MWSQLQueryFormat)relationalOptions.getQueryFormat()).setQueryString(sql);
	}
	
	public TriStateBoolean isCacheStatement() {
		return this.relationalOptions.isCacheStatement();
	}

	public void setCacheStatement(TriStateBoolean cacheStatement) {
		this.relationalOptions.setCacheStatement(cacheStatement);
	}


	public TriStateBoolean isBindAllParameters() {
		return this.relationalOptions.isBindAllParameters();
	}

	public void setBindAllParameters(TriStateBoolean bindAllParameters) {
		this.relationalOptions.setBindAllParameters(bindAllParameters);
	}

	public boolean isPrepare() {
		return this.relationalOptions.isPrepare();
	}

	public void setPrepare(boolean bindAllParameters) {
		this.relationalOptions.setPrepare(bindAllParameters);
	}

	public void notifyExpressionsToRecalculateQueryables() {
		this.relationalOptions.notifyExpressionsToRecalculateQueryables();
	}
	
	protected abstract DatabaseQuery buildRuntimeQuery();

	public void adjustFromRuntimeQuery(DatabaseQuery runtimeQuery) {
		relationalOptions.adjustFromRuntime(runtimeQuery);
	}

	public void adjustRuntimeQuery(DatabaseQuery runtimeQuery) {
		relationalOptions.adjustRuntimeQuery(runtimeQuery);
	}

	public boolean isContentEmpty() {
		MWQueryFormat format = this.relationalOptions.getQueryFormat();
		
		if (format.getType().equals(MWRelationalQuery.SQL_FORMAT) || format.getType().equals(MWRelationalQuery.EJBQL_FORMAT)) {
			return StringTools.stringIsEmpty(((MWStringQueryFormat)format).getQueryString());
		}
		
		if (format.getType().equals(MWRelationalQuery.STORED_PROCEDURE_FORMAT)) {
			return StringTools.stringIsEmpty(((MWStoredProcedureQueryFormat)format).getProcedure().getName());
		}
		return true;
	}
}
