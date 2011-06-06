package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;

public final class MWNullContainerAccessor extends MWContainerAccessor {

	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only (sorta) */
	public MWNullContainerAccessor() {
		super();
	}

	public MWNullContainerAccessor(MWNode parent) {
		super(parent);
	}

	@Override
	public String accessorDisplayString() {
		return null;
	}

	// **************** Runtime conversion ************************************
	
	public void adjustRuntimeMapping(AbstractCompositeObjectMapping mapping) {
		//no-op
	}
	
	public void adjustRuntimeMapping(AbstractCompositeCollectionMapping mapping) {
		//no-op
	}
	
	// **************** TopLink methods ************************************
	@Override
	public MWContainerAccessor valueForTopLink() {
		return null;
	}
	
	@Override
	public boolean isNull() {
		return true;
	}

	@Override
	public boolean isMethods() {
		return false;
	}
	
	@Override
	public boolean isAttribute() {
		return false;
	}
}
