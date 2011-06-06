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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;


public class PropertyAspectAdapterTests extends TestCase {
	private TestSubject subject1;
	private PropertyValueModel subjectHolder1;
	private PropertyAspectAdapter aa1;
	private PropertyChangeEvent event1;
	private PropertyChangeListener listener1;

	private TestSubject subject2;

	private PropertyChangeEvent multipleValueEvent;

	private PropertyChangeEvent customValueEvent;


	public static Test suite() {
		return new TestSuite(PropertyAspectAdapterTests.class);
	}
	
	public PropertyAspectAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.subject1 = new TestSubject("foo", "test subject 1");
		this.subjectHolder1 = new SimplePropertyValueModel(this.subject1);
		this.aa1 = this.buildAspectAdapter(this.subjectHolder1);
		this.listener1 = this.buildValueChangeListener1();
		this.aa1.addPropertyChangeListener(ValueModel.VALUE, this.listener1);
		this.event1 = null;

		this.subject2 = new TestSubject("bar", "test subject 2");
	}

	private PropertyAspectAdapter buildAspectAdapter(ValueModel subjectHolder) {
		return new PropertyAspectAdapter(subjectHolder, TestSubject.NAME_PROPERTY) {
			// this is not a aspect adapter - the value is determined by the aspect name
			protected Object getValueFromSubject() {
				if (this.propertyNames[0] == TestSubject.NAME_PROPERTY) {
					return ((TestSubject) this.subject).getName();
				} else if (this.propertyNames[0] == TestSubject.DESCRIPTION_PROPERTY) {
					return ((TestSubject) this.subject).getDescription();
				} else {
					throw new IllegalStateException("invalid aspect name: " + this.propertyNames[0]);
				}
			}
			protected void setValueOnSubject(Object value) {
				if (this.propertyNames[0] == TestSubject.NAME_PROPERTY) {
					((TestSubject) this.subject).setName((String) value);
				} else if (this.propertyNames[0] == TestSubject.DESCRIPTION_PROPERTY) {
					((TestSubject) this.subject).setDescription((String) value);
				} else {
					throw new IllegalStateException("invalid aspect name: " + this.propertyNames[0]);
				}
			}
		};
	}

	private PropertyChangeListener buildValueChangeListener1() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				PropertyAspectAdapterTests.this.value1Changed(e);
			}
		};
	}

	void value1Changed(PropertyChangeEvent e) {
		this.event1 = e;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSubjectHolder() {
		assertEquals("foo", this.aa1.getValue());
		assertNull(this.event1);

		this.subjectHolder1.setValue(this.subject2);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getPropertyName());
		assertEquals("foo", this.event1.getOldValue());
		assertEquals("bar", this.event1.getNewValue());
		assertEquals("bar", this.aa1.getValue());
		
		this.event1 = null;
		this.subjectHolder1.setValue(null);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getPropertyName());
		assertEquals("bar", this.event1.getOldValue());
		assertNull(this.event1.getNewValue());
		assertNull(this.aa1.getValue());
		
		this.event1 = null;
		this.subjectHolder1.setValue(this.subject1);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getPropertyName());
		assertEquals(null, this.event1.getOldValue());
		assertEquals("foo", this.event1.getNewValue());
		assertEquals("foo", this.aa1.getValue());
	}

	public void testPropertyChange() {
		assertEquals("foo", this.aa1.getValue());
		assertNull(this.event1);

		this.subject1.setName("baz");
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getPropertyName());
		assertEquals("foo", this.event1.getOldValue());
		assertEquals("baz", this.event1.getNewValue());
		assertEquals("baz", this.aa1.getValue());
		
		this.event1 = null;
		this.subject1.setName(null);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getPropertyName());
		assertEquals("baz", this.event1.getOldValue());
		assertEquals(null, this.event1.getNewValue());
		assertEquals(null, this.aa1.getValue());
		
		this.event1 = null;
		this.subject1.setName("foo");
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getPropertyName());
		assertEquals(null, this.event1.getOldValue());
		assertEquals("foo", this.event1.getNewValue());
		assertEquals("foo", this.aa1.getValue());
	}

	public void testGetValue() {
		assertEquals("foo", this.subject1.getName());
		assertEquals("foo", this.aa1.getValue());
	}

	public void testStaleValue() {
		assertEquals("foo", this.subject1.getName());
		assertEquals("foo", this.aa1.getValue());

		this.aa1.removePropertyChangeListener(ValueModel.VALUE, this.listener1);
		assertEquals(null, this.aa1.getValue());

		this.aa1.addPropertyChangeListener(ValueModel.VALUE, this.listener1);
		assertEquals("foo", this.aa1.getValue());

		this.aa1.removePropertyChangeListener(ValueModel.VALUE, this.listener1);
		this.subjectHolder1.setValue(this.subject2);
		assertEquals(null, this.aa1.getValue());

		this.aa1.addPropertyChangeListener(ValueModel.VALUE, this.listener1);
		assertEquals("bar", this.aa1.getValue());
	}

	public void testSetValue() {
		this.aa1.setValue("baz");
		assertEquals("baz", this.aa1.getValue());
		assertEquals("baz", this.subject1.getName());
	}

	public void testHasListeners() {
		assertTrue(this.aa1.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(this.subject1.hasAnyPropertyChangeListeners(TestSubject.NAME_PROPERTY));
		this.aa1.removePropertyChangeListener(ValueModel.VALUE, this.listener1);
		assertFalse(this.subject1.hasAnyPropertyChangeListeners(TestSubject.NAME_PROPERTY));
		assertFalse(this.aa1.hasAnyPropertyChangeListeners(ValueModel.VALUE));

		PropertyChangeListener listener2 = this.buildValueChangeListener1();
		this.aa1.addPropertyChangeListener(listener2);
		assertTrue(this.aa1.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(this.subject1.hasAnyPropertyChangeListeners(TestSubject.NAME_PROPERTY));
		this.aa1.removePropertyChangeListener(listener2);
		assertFalse(this.subject1.hasAnyPropertyChangeListeners(TestSubject.NAME_PROPERTY));
		assertFalse(this.aa1.hasAnyPropertyChangeListeners(ValueModel.VALUE));
	}

	public void testMultipleAspectAdapter() {
		TestSubject testSubject = new TestSubject("fred", "husband");
		PropertyValueModel testSubjectHolder = new SimplePropertyValueModel(testSubject);
		PropertyValueModel testAA = this.buildMultipleAspectAdapter(testSubjectHolder);
		PropertyChangeListener testListener = this.buildMultipleValueChangeListener();
		testAA.addPropertyChangeListener(ValueModel.VALUE, testListener);
		assertEquals("fred:husband", testAA.getValue());

		this.multipleValueEvent = null;
		testSubject.setName("wilma");
		assertEquals("wilma:husband", testAA.getValue());
		assertEquals("fred:husband", this.multipleValueEvent.getOldValue());
		assertEquals("wilma:husband", this.multipleValueEvent.getNewValue());

		this.multipleValueEvent = null;
		testSubject.setDescription("wife");
		assertEquals("wilma:wife", testAA.getValue());
		assertEquals("wilma:husband", this.multipleValueEvent.getOldValue());
		assertEquals("wilma:wife", this.multipleValueEvent.getNewValue());
	}

	private PropertyValueModel buildMultipleAspectAdapter(ValueModel subjectHolder) {
		return new PropertyAspectAdapter(subjectHolder, TestSubject.NAME_PROPERTY, TestSubject.DESCRIPTION_PROPERTY) {
			protected Object getValueFromSubject() {
				TestSubject ts = (TestSubject) this.subject;
				return ts.getName() + ":" + ts.getDescription();
			}
		};
	}

	private PropertyChangeListener buildMultipleValueChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				PropertyAspectAdapterTests.this.multipleValueChanged(e);
			}
		};
	}

	void multipleValueChanged(PropertyChangeEvent e) {
		this.multipleValueEvent = e;
	}

	/**
	 * test a bug where we would call #buildValue() in
	 * #engageNonNullSubject(), when we needed to call
	 * it in #engageSubject(), so the cached value would
	 * be rebuilt when the this.subject was set to null
	 */
	public void testCustomBuildValueWithNullSubject() {
		TestSubject customSubject = new TestSubject("fred", "laborer");
		PropertyValueModel customSubjectHolder = new SimplePropertyValueModel(customSubject);
		PropertyValueModel customAA = this.buildCustomAspectAdapter(customSubjectHolder);
		PropertyChangeListener customListener = this.buildCustomValueChangeListener();
		customAA.addPropertyChangeListener(ValueModel.VALUE, customListener);
		assertEquals("fred", customAA.getValue());

		this.customValueEvent = null;
		customSubject.setName("wilma");
		assertEquals("wilma", customAA.getValue());
		assertEquals("fred", this.customValueEvent.getOldValue());
		assertEquals("wilma", this.customValueEvent.getNewValue());

		this.customValueEvent = null;
		customSubjectHolder.setValue(null);
		// this would fail - the value would be null...
		assertEquals("<unnamed>", customAA.getValue());
		assertEquals("wilma", this.customValueEvent.getOldValue());
		assertEquals("<unnamed>", this.customValueEvent.getNewValue());
	}

	private PropertyValueModel buildCustomAspectAdapter(ValueModel subjectHolder) {
		return new PropertyAspectAdapter(subjectHolder, TestSubject.NAME_PROPERTY) {
			protected Object buildValue() {
				TestSubject ts = (TestSubject) this.subject;
				return (ts == null) ? "<unnamed>" : ts.getName();
			}
		};
	}

	private PropertyChangeListener buildCustomValueChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				PropertyAspectAdapterTests.this.customValueChanged(e);
			}
		};
	}

	void customValueChanged(PropertyChangeEvent e) {
		this.customValueEvent = e;
	}


	// ********** inner class **********
	
	private class TestSubject extends AbstractModel {
		private String name;
		public static final String NAME_PROPERTY = "name";
		private String description;
		public static final String DESCRIPTION_PROPERTY = "description";
	
		public TestSubject(String name, String description) {
			this.name = name;
			this.description = description;
		}
		public String getName() {
			return this.name;
		}
		public void setName(String name) {
			Object old = this.name;
			this.name = name;
			this.firePropertyChanged(NAME_PROPERTY, old, name);
		}
		public String getDescription() {
			return this.description;
		}
		public void setDescription(String description) {
			Object old = this.description;
			this.description = description;
			this.firePropertyChanged(DESCRIPTION_PROPERTY, old, description);
		}
	}

}
