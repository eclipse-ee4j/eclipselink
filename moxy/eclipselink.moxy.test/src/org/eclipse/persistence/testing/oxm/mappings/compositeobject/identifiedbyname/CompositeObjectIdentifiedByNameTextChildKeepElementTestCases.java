package org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname;

import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;

public class CompositeObjectIdentifiedByNameTextChildKeepElementTestCases extends CompositeObjectIdentifiedByNameTextChildTestCases{

	public CompositeObjectIdentifiedByNameTextChildKeepElementTestCases(			String name) throws Exception {
		super(name);
		Project p = new CompositeObjectIdentifiedByNameTextProject();
		((XMLCompositeObjectMapping)p.getDescriptor(EmployeeWithObjects.class).getMappingForAttributeName("salary")).setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
		setProject(p);
	}

}
