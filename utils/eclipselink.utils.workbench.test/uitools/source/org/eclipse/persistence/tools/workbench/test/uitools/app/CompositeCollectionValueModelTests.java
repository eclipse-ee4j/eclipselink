/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CompositeCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.Bag;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


public class CompositeCollectionValueModelTests extends TestCase {
	private Neighborhood neighborhood;
	private PropertyValueModel neighborhoodHolder;
	
	public static Test suite() {
		return new TestSuite(CompositeCollectionValueModelTests.class);
	}
	
	public CompositeCollectionValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.neighborhood = new Neighborhood("Hanna-Barbera");
		this.neighborhoodHolder = new SimplePropertyValueModel(this.neighborhood);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSynch1() {
		this.verifySynch(this.buildAllMembersComposite(this.neighborhoodHolder));
	}

	public void testSynch2() {
		this.verifySynch(this.buildAllMembersComposite2(this.neighborhoodHolder));
	}

	private void verifySynch(CollectionValueModel compositeCVM) {
		assertEquals(0, CollectionTools.size((Iterator) compositeCVM.getValue()));
		Bag familiesSynch = new SynchronizedBag(this.buildFamiliesAspectAdapter(this.neighborhoodHolder));
		Bag membersSynch = new SynchronizedBag(compositeCVM);
		this.populateNeighborhood(this.neighborhood);

		Family jetsons = this.neighborhood.familyNamed("Jetson");

		assertEquals(3, familiesSynch.size());
		assertEquals(12, CollectionTools.size(this.neighborhood.allMembers()));
		assertEquals(12, membersSynch.size());
		assertEquals(CollectionTools.bag(this.neighborhood.allMembers()), membersSynch);
		assertEquals(membersSynch, CollectionTools.bag((Iterator) compositeCVM.getValue()));

		jetsons.removeMember(jetsons.memberNamed("Astro"));
		assertEquals(3, familiesSynch.size());
		assertEquals(11, CollectionTools.size(this.neighborhood.allMembers()));
		assertEquals(11, membersSynch.size());
		assertEquals(CollectionTools.bag(this.neighborhood.allMembers()), membersSynch);
		assertEquals(membersSynch, CollectionTools.bag((Iterator) compositeCVM.getValue()));

		jetsons.removeMember(jetsons.memberNamed("Judy"));
		assertEquals(3, familiesSynch.size());
		assertEquals(10, CollectionTools.size(this.neighborhood.allMembers()));
		assertEquals(10, membersSynch.size());
		assertEquals(CollectionTools.bag(this.neighborhood.allMembers()), membersSynch);
		assertEquals(membersSynch, CollectionTools.bag((Iterator) compositeCVM.getValue()));

		jetsons.addMember("Fido");
		assertEquals(3, familiesSynch.size());
		assertEquals(11, CollectionTools.size(this.neighborhood.allMembers()));
		assertEquals(11, membersSynch.size());
		assertEquals(CollectionTools.bag(this.neighborhood.allMembers()), membersSynch);
		assertEquals(membersSynch, CollectionTools.bag((Iterator) compositeCVM.getValue()));

		this.neighborhood.removeFamily(jetsons);
		assertEquals(2, familiesSynch.size());
		assertEquals(7, CollectionTools.size(this.neighborhood.allMembers()));
		assertEquals(7, membersSynch.size());
		assertEquals(CollectionTools.bag(this.neighborhood.allMembers()), membersSynch);
		assertEquals(membersSynch, CollectionTools.bag((Iterator) compositeCVM.getValue()));

		Family bears = this.neighborhood.addFamily("Bear");
			bears.addMember("Yogi");
		assertEquals(3, familiesSynch.size());
		assertEquals(8, CollectionTools.size(this.neighborhood.allMembers()));
		assertEquals(8, membersSynch.size());
		assertEquals(CollectionTools.bag(this.neighborhood.allMembers()), membersSynch);
		assertEquals(membersSynch, CollectionTools.bag((Iterator) compositeCVM.getValue()));

		bears.addMember("Boo-Boo");
		assertEquals(3, familiesSynch.size());
		assertEquals(9, CollectionTools.size(this.neighborhood.allMembers()));
		assertEquals(9, membersSynch.size());
		assertEquals(CollectionTools.bag(this.neighborhood.allMembers()), membersSynch);
		assertEquals(membersSynch, CollectionTools.bag((Iterator) compositeCVM.getValue()));

		Neighborhood n2 = new Neighborhood("Hanna-Barbera 2");
		this.neighborhoodHolder.setValue(n2);
		this.populateNeighborhood(n2);
		assertEquals(3, familiesSynch.size());
		assertEquals(12, CollectionTools.size(n2.allMembers()));
		assertEquals(12, membersSynch.size());
		assertEquals(CollectionTools.bag(n2.allMembers()), membersSynch);
		assertEquals(membersSynch, CollectionTools.bag((Iterator) compositeCVM.getValue()));
	}

	public void testNoTransformer() {
		Bag synchBag = new SynchronizedBag(this.buildBogusAllMembersComposite(this.neighborhoodHolder));
		boolean exCaught = false;
		try {
			this.populateNeighborhood(this.neighborhood);
		} catch (IllegalStateException ex) {
			if (ex.getMessage().indexOf("CompositeCollectionValueModel.transform(Object)") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
		assertEquals(0, synchBag.size());
	}

	public void testDuplicateItem() {
		Bag synchBag = new SynchronizedBag(this.buildAllMembersComposite(this.neighborhoodHolder));
		this.populateNeighborhood(this.neighborhood);
		boolean exCaught = false;
		try {
			this.neighborhood.addFamily(this.neighborhood.familyNamed("Jetson"));
		} catch (IllegalStateException ex) {
			if (ex.getMessage().indexOf("duplicate component") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
		assertEquals(12, synchBag.size());
	}

	public void testHasListeners() {
		CompositeCollectionValueModel compositeCVM = this.buildAllMembersComposite(this.neighborhoodHolder);
		SynchronizedBag synchBag = new SynchronizedBag(compositeCVM);
		this.populateNeighborhood(this.neighborhood);
		Family jetsons = this.neighborhood.familyNamed("Jetson");

		assertTrue(compositeCVM.hasAnyCollectionChangeListeners(ValueModel.VALUE));
		assertTrue(jetsons.hasAnyCollectionChangeListeners(Family.MEMBERS_COLLECTION));

		compositeCVM.removeCollectionChangeListener(ValueModel.VALUE, synchBag);
		assertFalse(compositeCVM.hasAnyCollectionChangeListeners(ValueModel.VALUE));
		assertFalse(jetsons.hasAnyCollectionChangeListeners(Family.MEMBERS_COLLECTION));

		compositeCVM.addCollectionChangeListener(ValueModel.VALUE, synchBag);
		assertTrue(compositeCVM.hasAnyCollectionChangeListeners(ValueModel.VALUE));
		assertTrue(jetsons.hasAnyCollectionChangeListeners(Family.MEMBERS_COLLECTION));
	}

	private void populateNeighborhood(Neighborhood n) {
		Family family1 = n.addFamily("Flintstone");
			family1.addMember("Fred");
			family1.addMember("Wilma");
			family1.addMember("Pebbles");
			family1.addMember("Dino");
		Family family2 = n.addFamily("Rubble");
			family2.addMember("Barney");
			family2.addMember("Betty");
			family2.addMember("Bamm-Bamm");
		Family family3 = n.addFamily("Jetson");
			family3.addMember("George");
			family3.addMember("Jane");
			family3.addMember("Judy");
			family3.addMember("Elroy");
			family3.addMember("Astro");
	}

	private CollectionValueModel buildFamiliesAspectAdapter(ValueModel communeHolder) {
		return new CollectionAspectAdapter(communeHolder, Neighborhood.FAMILIES_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((Neighborhood) this.subject).families();
			}
		};
	}

	CollectionValueModel buildMembersAdapter(Family family) {
		return new CollectionAspectAdapter(Family.MEMBERS_COLLECTION, family) {
			protected Iterator getValueFromSubject() {
				return ((Family) this.subject).members();
			}
		};
	}

	private CompositeCollectionValueModel buildAllMembersComposite(ValueModel communeHolder) {
		// override #transform(Object)
		return new CompositeCollectionValueModel(this.buildFamiliesAspectAdapter(communeHolder)) {
			protected CollectionValueModel transform(Object value) {
				return CompositeCollectionValueModelTests.this.buildMembersAdapter((Family) value);
			}
		};
	}

	private CollectionValueModel buildAllMembersComposite2(ValueModel communeHolder) {
		// build a custom Transformer
		return new CompositeCollectionValueModel(this.buildFamiliesAspectAdapter(communeHolder), this.buildTransformer());
	}

	private CollectionValueModel buildBogusAllMembersComposite(ValueModel communeHolder) {
		// NO Transformer
		return new CompositeCollectionValueModel(this.buildFamiliesAspectAdapter(communeHolder));
	}

	private Transformer buildTransformer() {
		return new Transformer() {
			public Object transform(Object value) {
				return CompositeCollectionValueModelTests.this.buildMembersAdapter((Family) value);
			}
			public String toString() {
				return "Local Transformer";
			}
		};
	}


// ********** inner classes **********

	/**
	 * inner class
	 */
	private class Neighborhood extends AbstractModel {
		private String name;
			public static final String NAME_PROPERTY = "name";
		private Collection families = new ArrayList();
			public static final String FAMILIES_COLLECTION = "families";
	
		public Neighborhood(String name) {
			super();
			this.name = name;
		}
	
		public String getName() {
			return this.name;
		}
		
		public void setName(String name) {
			Object old = this.name;
			this.name = name;
			this.firePropertyChanged(NAME_PROPERTY, old, name);
		}
	
		public Iterator families() {
			return this.families.iterator();
		}
	
		public Family addFamily(String familyName) {
			return this.addFamily(new Family(familyName));
		}

		// backdoor to allow duplicates
		public Family addFamily(Family family) {
			this.addItemToCollection(family, this.families, FAMILIES_COLLECTION);
			return family;
		}

		public void removeFamily(Family family) {
			this.removeItemFromCollection(family, this.families, FAMILIES_COLLECTION);
		}
	
		public Family familyNamed(String familyName) {
			for (Iterator stream = this.families.iterator(); stream.hasNext(); ) {
				Family family = (Family) stream.next();
				if (family.getName().equals(familyName)) {
					return family;
				}
			}
			throw new IllegalArgumentException(familyName);
		}
	
		public Iterator allMembers() {
			return new CompositeIterator(this.membersIterators());
		}
	
		private Iterator membersIterators() {
			return new TransformationIterator(this.families()) {
				protected Object transform(Object next) {
					return ((Family) next).members();
				}
			};
		}
	
		public Member memberNamed(String familyName, String memberName) {
			return this.familyNamed(familyName).memberNamed(memberName);
		}
	
		public void toString(StringBuffer sb) {
			sb.append(this.name);
		}

	}


	/**
	 * inner class
	 */
	private class Family extends AbstractModel {
		private String name;
			public static final String NAME_PROPERTY = "name";
		private Collection members = new ArrayList();
			public static final String MEMBERS_COLLECTION = "members";
	
		public Family(String name) {
			super();
			this.name = name;
		}
	
		public String getName() {
			return this.name;
		}
		
		public void setName(String name) {
			Object old = this.name;
			this.name = name;
			this.firePropertyChanged(NAME_PROPERTY, old, name);
		}
	
		public Iterator members() {
			return this.members.iterator();
		}
	
		public Member addMember(String memberName) {
			Member member = new Member(memberName);
			this.addItemToCollection(member, this.members, MEMBERS_COLLECTION);
			return member;
		}
		
		public void removeMember(Member member) {
			this.removeItemFromCollection(member, this.members, MEMBERS_COLLECTION);
		}
	
		public Member memberNamed(String memberName) {
			for (Iterator stream = this.members.iterator(); stream.hasNext(); ) {
				Member member = (Member) stream.next();
				if (member.getName().equals(memberName)) {
					return member;
				}
			}
			throw new IllegalArgumentException(memberName);
		}
	
		public void toString(StringBuffer sb) {
			sb.append(this.name);
		}
	
	}


	/**
	 * inner class
	 */
	private class Member extends AbstractModel {
		private String name;
			public static final String NAME_PROPERTY = "name";

		public Member(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	
		public void setName(String name) {
			Object old = this.name;
			this.name = name;
			this.firePropertyChanged(NAME_PROPERTY, old, name);
		}

		public void toString(StringBuffer sb) {
			sb.append(this.name);
		}

	}

}
