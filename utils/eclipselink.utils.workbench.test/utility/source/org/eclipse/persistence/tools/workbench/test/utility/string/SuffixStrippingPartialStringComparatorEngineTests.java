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
package org.eclipse.persistence.tools.workbench.test.utility.string;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.string.AffixStrippingPartialStringComparatorEngine;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparatorEngine;
import org.eclipse.persistence.tools.workbench.utility.string.SimpleStringHolder;
import org.eclipse.persistence.tools.workbench.utility.string.StringHolder;

public class SuffixStrippingPartialStringComparatorEngineTests extends TestCase {
	
	public static Test suite() {
		return new TestSuite(SuffixStrippingPartialStringComparatorEngineTests.class);
	}
	
	public SuffixStrippingPartialStringComparatorEngineTests(String name) {
		super(name);
	}

	public void testSuffixVariable1() {
		Collection holders1 = new ArrayList(100);
		holders1.addAll(this.buildSuffixStringHolders("xxx", 5));
		holders1.addAll(this.buildSuffixStringHolders("yyy", 20));
		holders1.addAll(this.buildSuffixStringHolders("zzz", 75));

		Collection holders2 = new ArrayList(100);
		holders2.addAll(this.buildSuffixStringHolders("xxx", 5));
		holders2.addAll(this.buildSuffixStringHolders("yyy", 20));
		holders2.addAll(this.buildSuffixStringHolders("zzz", 75));

		class LocalEngine implements PartialStringComparatorEngine {
			public StringHolderPair[] match(StringHolder[] stringHolders1, StringHolder[] stringHolders2) {
				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("xxx", stringHolders1);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("yyy", stringHolders1);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertNoSuffixes("zzz", stringHolders1);

				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("xxx", stringHolders2);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("yyy", stringHolders2);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertNoSuffixes("zzz", stringHolders2);

				return new StringHolderPair[0];
			}
		}
		PartialStringComparatorEngine engine = this. buildFixedSuffixEngine(new LocalEngine(), 0.75f);
		engine.match((StringHolder[]) holders1.toArray(new StringHolder[holders1.size()]), (StringHolder[]) holders2.toArray(new StringHolder[holders2.size()]));
	}

	public void testSuffixVariable2() {
		Collection holders1 = new ArrayList(100);
		holders1.addAll(this.buildSuffixStringHolders("xxx", 5));
		holders1.addAll(this.buildSuffixStringHolders("yyy", 20));
		holders1.addAll(this.buildSuffixStringHolders("zzz", 75));

		Collection holders2 = new ArrayList(100);
		holders2.addAll(this.buildSuffixStringHolders("xxx", 5));
		holders2.addAll(this.buildSuffixStringHolders("yyy", 20));
		holders2.addAll(this.buildSuffixStringHolders("zzz", 40));
		holders2.addAll(this.buildSuffixStringHolders("aaazzz", 35));

		class LocalEngine implements PartialStringComparatorEngine {
			public StringHolderPair[] match(StringHolder[] stringHolders1, StringHolder[] stringHolders2) {
				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("xxx", stringHolders1);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("yyy", stringHolders1);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertNoSuffixes("zzz", stringHolders1);

				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("xxx", stringHolders2);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("yyy", stringHolders2);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertNoSuffixes("zzz", stringHolders2);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("aaa", stringHolders2);

				return new StringHolderPair[0];
			}
		}
		PartialStringComparatorEngine engine = this. buildFixedSuffixEngine(new LocalEngine(), 0.50f);
		engine.match((StringHolder[]) holders1.toArray(new StringHolder[holders1.size()]), (StringHolder[]) holders2.toArray(new StringHolder[holders2.size()]));
	}

	public void testSuffixVariable3() {
		Collection holders1 = new ArrayList(100);
		holders1.addAll(this.buildSuffixStringHolders("xxx", 5));
		holders1.addAll(this.buildSuffixStringHolders("yyy", 20));
		holders1.addAll(this.buildSuffixStringHolders("zzz", 75));

		Collection holders2 = new ArrayList(100);
		holders2.addAll(this.buildSuffixStringHolders("xxx", 5));
		holders2.addAll(this.buildSuffixStringHolders("yyy", 20));
		holders2.addAll(this.buildSuffixStringHolders("zzz", 40));
		holders2.addAll(this.buildSuffixStringHolders("zzzaaa", 35));
		holders2.add(new SimpleStringHolder("zzz"));		// add a string that is ALL suffix!

		class LocalEngine implements PartialStringComparatorEngine {
			public StringHolderPair[] match(StringHolder[] stringHolders1, StringHolder[] stringHolders2) {
				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("xxx", stringHolders1);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("yyy", stringHolders1);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertNoSuffixes("zzz", stringHolders1);

				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("xxx", stringHolders2);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("yyy", stringHolders2);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertNoSuffixes("zzz", stringHolders2);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertNoSuffixes("zzzaaa", stringHolders2);

				return new StringHolderPair[0];
			}
		}
		PartialStringComparatorEngine engine = this. buildFixedSuffixEngine(new LocalEngine(), 0.25f);
		engine.match((StringHolder[]) holders1.toArray(new StringHolder[holders1.size()]), (StringHolder[]) holders2.toArray(new StringHolder[holders2.size()]));
	}

	public void testSuffixFixed() {
		Collection holders1 = new ArrayList(100);
		holders1.addAll(this.buildSuffixStringHolders("xxx", 5));
		holders1.addAll(this.buildSuffixStringHolders("yyy", 20));
		holders1.addAll(this.buildSuffixStringHolders("zzz", 75));

		Collection holders2 = new ArrayList(100);
		holders2.addAll(this.buildSuffixStringHolders("xxx", 5));
		holders2.addAll(this.buildSuffixStringHolders("yyy", 20));
		holders2.addAll(this.buildSuffixStringHolders("zzz", 40));
		holders2.addAll(this.buildSuffixStringHolders("zzzaaa", 35));
		holders2.add(new SimpleStringHolder("zzz"));		// add a string that is ALL suffix!

		class LocalEngine implements PartialStringComparatorEngine {
			public StringHolderPair[] match(StringHolder[] stringHolders1, StringHolder[] stringHolders2) {
				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("xxx", stringHolders1);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertNoSuffixes("yyy", stringHolders1);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertNoSuffixes("zzz", stringHolders1);

				SuffixStrippingPartialStringComparatorEngineTests.this.assertSomeSuffixes("xxx", stringHolders2);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertNoSuffixes("yyy", stringHolders2);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertNoSuffixes("zzz", stringHolders2);
				SuffixStrippingPartialStringComparatorEngineTests.this.assertNoSuffixes("zzzaaa", stringHolders2);

				return new StringHolderPair[0];
			}
		}
		PartialStringComparatorEngine engine = this. buildVariableSuffixEngine(new LocalEngine(), 20);
		engine.match((StringHolder[]) holders1.toArray(new StringHolder[holders1.size()]), (StringHolder[]) holders2.toArray(new StringHolder[holders2.size()]));
	}

	private Collection buildSuffixStringHolders(String suffix, int count) {
		Collection result = new ArrayList(count);
		for (int i = 1; i <= count; i++) {
			result.add(new SimpleStringHolder(i + suffix));
		}
		return result;
	}

	void assertNoSuffixes(String suffix, StringHolder[] stringHolders) {
		for (int i = stringHolders.length; i-- > 0;) {
			if (stringHolders[i].getString().endsWith(suffix)) {
				fail("suffix should have been stripped: " + suffix);
			}
		}
	}

	void assertSomeSuffixes(String suffix, StringHolder[] stringHolders) {
		for (int i = stringHolders.length; i-- > 0;) {
			if (stringHolders[i].getString().endsWith(suffix)) {
				return;
			}
		}
		fail("suffix should NOT have been stripped: " + suffix);
	}

	private PartialStringComparatorEngine buildVariableSuffixEngine(PartialStringComparatorEngine engine, int threshold) {
		return AffixStrippingPartialStringComparatorEngine.forSuffixStripping(engine, threshold);
	}

	private PartialStringComparatorEngine buildFixedSuffixEngine(PartialStringComparatorEngine engine, float threshold) {
		return AffixStrippingPartialStringComparatorEngine.forSuffixStripping(engine, threshold);
	}

}
