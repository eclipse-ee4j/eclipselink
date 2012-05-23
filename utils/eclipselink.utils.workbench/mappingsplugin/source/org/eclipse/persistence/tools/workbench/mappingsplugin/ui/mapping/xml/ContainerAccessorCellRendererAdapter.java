package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAttributeContainerAccessor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWContainerAccessor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWMethodContainerAccessor;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;

public final class ContainerAccessorCellRendererAdapter extends
		AbstractCellRendererAdapter {

	// **************** Variables *********************************************
	
	private final ResourceRepository resourceRepository;
	
	// **************** Constructors ******************************************
	
	public ContainerAccessorCellRendererAdapter(ResourceRepository resourceRepository) {
		super();
		this.resourceRepository = resourceRepository;
	}
	
	// **************** CellRendererAdapter contract **************************
	
	public Icon buildIcon(Object value) {
		MWContainerAccessor accessor = (MWContainerAccessor) value;
		String text = this.buildText(accessor);
		if (text == null) {
			return null;
		}
		if (accessor instanceof MWMethodContainerAccessor) {
			return this.resourceRepository.getIcon("method.public");
		}
		if (accessor instanceof MWAttributeContainerAccessor) {
			return this.resourceRepository.getIcon("field.public");
		}
		throw new IllegalArgumentException("unknown transformer: " + accessor);
	}
	
	public String buildText(Object value) {
		return ((MWContainerAccessor) value).accessorDisplayString();
	}
	
	public String buildToolTipText(Object value) {
		MWContainerAccessor accessor = (MWContainerAccessor) value;
		String text = this.buildText(accessor);
		if (text == null) {
			return null;
		}
		if (accessor instanceof MWMethodContainerAccessor) {
			return this.resourceRepository.getString("ACCESSOR_METHODS_TOOLTIP", text);
		}
		if (accessor instanceof MWAttributeContainerAccessor) {
			return this.resourceRepository.getString("ACCESSOR_ATTRIBUTE_TOOLTIP", text);
		}
		throw new IllegalArgumentException("unknown transformer: " + accessor);
	}

}
