package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import java.util.List;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;

/**
 * Locking policy is not currently supported in OX descriptors but may eventually be supported.
 * @author lddavis
 *
 */

public class MWOXDescriptorLockingPolicy extends MWDescriptorLockingPolicy
implements MWXmlNode, MWXpathContext {

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWOXDescriptorLockingPolicy.class);
		descriptor.getInheritancePolicy().setParentClass(MWDescriptorLockingPolicy.class);
				
		return descriptor;
	}

	// **************** Constructors ******************************************
	
	private MWOXDescriptorLockingPolicy() {
		super();
	}
	
	MWOXDescriptorLockingPolicy(MWOXTransactionalPolicy descriptor) {
		super(descriptor);
	}

	// **************** Convenience *******************************************
	
	private MWOXDescriptor oxDescriptor() {
		return (MWOXDescriptor) ((MWTransactionalPolicy) this.getParent()).getParent();
	}

	public void resolveXpaths() {
	}

	public void schemaChanged(SchemaChange change) {
	}

	@Override
	protected void checkLockFieldSpecifiedForLockingPolicy(List newProblems) {
	}

	public MWDataField getVersionLockField() {
		return null;
	}

	public void setVersionLockField(MWDataField newLockField) {
	}

	public MWSchemaContextComponent schemaContext(MWXmlField xmlField) {
		return oxDescriptor().getSchemaContext();
	}

	public MWXpathSpec xpathSpec(MWXmlField xmlField) {
		return null;
	}	

}
