/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import org.eclipse.persistence.tools.workbench.framework.resources.AbstractIconResourceFileNameMap;

public final class MappingsPluginIconResourceFileNameMap extends AbstractIconResourceFileNameMap {
	
	private static final String[][] entries = {

		// Project
		{"project.relational", "mw/project/Project.relational.gif"},
		{"project.ox", "mw/project/Project.ox.gif"},
		{"project.eis", "mw/project/Project.eis.gif"},
		{"project.import.ejb-jar.xml", "basic/file/Project.import.ejb-jar.xml.gif"},
		{"project.export.ejb-jar.xml", "basic/file/Project.export.ejb-jar.xml.gif"},

		{"PROJECT.NEW", "basic/file/Project.new.gif"},
		{"GENERATE_XML", "mw/misc/GenerateXML.gif"},
		{"GENERATE_JAVA", "mw/misc/GenerateJava.gif"},

		{"project.remove", "basic/file/Project.remove.gif"},

		// Database
		{"database", "basic/database/Database.gif"},
		{"database.connected", "basic/database/Database.connected.gif"},
		{"database.connecting", "basic/database/Database.connecting.gif"},
		{"database.large", "basic/database/Database.large.gif"},
		{"database.error", "basic/database/Database.error.gif"},
		{"login", "basic/database/Login.gif"},
		{"logout", "basic/database/Logout.gif"},
		{"table.add", "basic/database/Table.add.gif"},
		{"table.addFromDatabase", "basic/database/Table.addFromDatabase.gif"},
		{"table", "basic/database/Table.gif"},
		{"table.remove", "basic/database/Table.remove.gif" },

		// Meta
		{"class.default", "basic/meta/Class.default.gif"},
		{"class.interface", "basic/meta/Class.interface.gif"},
		{"class.private", "basic/meta/Class.private.gif"},
		{"class.protected", "basic/meta/Class.protected.gif"},
		{"class.public", "basic/meta/Class.public.gif"},
		{"field.default", "basic/meta/Field.default.gif"},
		{"field.private", "basic/meta/Field.private.gif"},
		{"field.protected", "basic/meta/Field.protected.gif"},
		{"field.public", "basic/meta/Field.public.gif"},
		{"inherited", "basic/meta/Inherited.gif"},
		{"method.default", "basic/meta/Method.default.gif"},
		{"method.private", "basic/meta/Method.private.gif"},
		{"method.protected", "basic/meta/Method.protected.gif"},
		{"method.public", "basic/meta/Method.public.gif"},
		{"package", "basic/meta/Package.gif"},
		{"package.new", "basic/meta/Package.new.gif"},
		{"package.open", "basic/meta/Package.open.gif"},
		{"package.remove", "basic/meta/Package.remove.gif" },

		// Descriptor
		{"descriptor.advancedProperties", "mw/descriptor/Descriptor.advancedProperties.gif"},
		{"descriptor.aggregate", "mw/descriptor/Descriptor.aggregate.gif"},
		{"descriptor.bean", "mw/descriptor/Descriptor.bean.gif"},
		{"descriptor.class", "mw/descriptor/Descriptor.class.gif"},
		{"descriptor.convert", "mw/descriptor/Descriptor.convert.gif"},
		{"descriptor.interface", "mw/descriptor/Descriptor.interface.gif"},
		{"descriptor.new", "basic/meta/Class.new.gif"},
		{"descriptor.refresh", "basic/meta/Class.refresh.gif"},
		{"descriptor.remove", "basic/meta/Class.remove.gif" },	
		{"queryKey", "mw/query/QueryKey.gif"},
		{"queryKey.userDefined", "mw/query/QueryKey.userDefined.gif"},

		// OX Descriptor
		{"descriptor.ox", "mw/descriptor/Descriptor.ox.gif" },
		
		// EIS Descriptor
		{"descriptor.eis.root", "mw/descriptor/Descriptor.eis.root.gif" },
		{"descriptor.eis.root.bean", "mw/descriptor/Descriptor.eis.root.bean.gif"},
		{"descriptor.eis.composite", "mw/descriptor/Descriptor.eis.composite.gif" },

		// Mapping
		{"mapping.aggregate", "mw/mapping/Mapping.aggregate.gif"},
		{"mapping.directCollection", "mw/mapping/Mapping.directCollection.gif"},
		{"mapping.directMap", "mw/mapping/Mapping.directMap.gif"},
		{"mapping.directToField", "mw/mapping/Mapping.directToField.gif"},
		{"mapping.directToXmlType", "mw/mapping/Mapping.directToXmlType.gif"},
		{"mapping.holder", "mw/mapping/Mapping.holder.gif"},
		{"mapping.manyToMany", "mw/mapping/Mapping.manyToMany.gif"},
		{"mapping.objectType", "mw/mapping/Mapping.objectType.gif"},
		{"mapping.oneToMany", "mw/mapping/Mapping.oneToMany.gif"},
		{"mapping.oneToOne", "mw/mapping/Mapping.oneToOne.gif"},
		{"mapping.remove", "mw/mapping/Mapping.remove.gif"},
		{"mapping.serialized", "mw/mapping/Mapping.serialized.gif"},
		{"mapping.transformation", "mw/mapping/Mapping.transformation.gif"},
		{"mapping.typeConversion", "mw/mapping/Mapping.typeConversion.gif"},
		{"mapping.unmapped", "mw/mapping/Mapping.unmapped.gif"},
		{"mapping.variableOneToOne", "mw/mapping/Mapping.variableOneToOne.gif" },
			
		// XML Mapping
		{"mapping.xmlDirect", "mw/mapping/Mapping.xmlDirect.gif"},
		{"mapping.xmlDirectCollection", "mw/mapping/Mapping.xmlDirectCollection.gif"},
		{"mapping.compositeObject", "mw/mapping/Mapping.composite.gif"},
		{"mapping.compositeCollection", "mw/mapping/Mapping.compositeCollection.gif"},
		{"mapping.xmlDirectMap", "mw/mapping/Mapping.xmlDirectMap.gif"},
		{"mapping.anyAttribute", "mw/mapping/Mapping.anyAttribute.png"},
		{"mapping.collectionReference", "mw/mapping/Mapping.collectionReference.png"},
		{"mapping.mixedXmlContent", "mw/mapping/Mapping.mixedXmlContent.png"},
		{"mapping.objectReference", "mw/mapping/Mapping.objectReference.png"},
		{"mapping.xmlFragment", "mw/mapping/Mapping.xmlFragment.png"},
		{"mapping.xmlFragmentCollection", "mw/mapping/Mapping.xmlFragmentCollection.png"},
		
		// OX Mapping
		{"mapping.anyObject", "mw/mapping/Mapping.anyObject.gif"},
		{"mapping.anyCollection", "mw/mapping/Mapping.anyCollection.gif"},
		
		// EIS Mapping
		{"mapping.eisOneToOne", "mw/mapping/Mapping.eisOneToOne.gif"},
		{"mapping.eisOneToMany", "mw/mapping/Mapping.eisOneToMany.gif"},

		// Miscellaneous
		{"approve",     "basic/misc/Approve.gif"},
		{"automap",     "mw/misc/Automap.gif"},
		{"synchronize", "mw/misc/Synchronize.gif"},
		{"urgent",      "basic/misc/Forward.gif"},
		{"ignore",      "basic/misc/Ignore.gif"},
		{"shuttle.left",  "basic/misc/shuttleLeft.png"},
		{"shuttle.right", "basic/misc/shuttleRight.png"},
		
		// Advanced Policies
		{"policy.afterLoading",   "mw/policy/Policy.afterLoading.gif"},
		{"policy.copy",           "mw/policy/Policy.copy.gif"},
		{"policy.events",         "mw/policy/Policy.events.gif"},
		{"policy.caching",        "mw/policy/Policy.caching.gif"},
		{"policy.inheritance",    "mw/policy/Policy.inheritance.gif"},
		{"policy.instantiation",  "mw/policy/Policy.instantiation.gif"},
		{"policy.interfaceAlias", "mw/policy/Policy.interfaceAlias.gif"},		
		{"policy.locking",        "mw/policy/Policy.locking.gif"},		
		{"policy.multiTableInfo", "mw/policy/Policy.multiTableInfo.gif"},
		{"policy.sequencing",     "mw/policy/Policy.sequencing.gif"},		
	};

	protected String[][] getEntries() {
		return entries;
	}
}
