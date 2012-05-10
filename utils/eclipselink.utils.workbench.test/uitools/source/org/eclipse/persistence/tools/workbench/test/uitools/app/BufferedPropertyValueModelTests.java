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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;


public class BufferedPropertyValueModelTests extends TestCase {
	private Employee employee;
	private PropertyValueModel employeeHolder;
	PropertyChangeEvent employeeEvent;

	private PropertyValueModel idAdapter;
	private PropertyValueModel nameAdapter;
	private PropertyValueModel hireDateAdapter;
	PropertyChangeEvent adapterEvent;

	private BufferedPropertyValueModel.Trigger trigger;
	private PropertyValueModel bufferedIDHolder;
	private PropertyValueModel bufferedNameHolder;
	private PropertyValueModel bufferedHireDateHolder;
	PropertyChangeEvent bufferedEvent;

	public static Test suite() {
		return new TestSuite(BufferedPropertyValueModelTests.class);
	}
	
	public BufferedPropertyValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		this.employee = new Employee(17, "Freddy", new Date());
		this.employeeHolder = new SimplePropertyValueModel(this.employee);

		this.trigger = new BufferedPropertyValueModel.Trigger();

		this.idAdapter = this.buildIDAdapter(this.employeeHolder);
		this.bufferedIDHolder = new BufferedPropertyValueModel(this.idAdapter, this.trigger);

		this.nameAdapter = this.buildNameAdapter(this.employeeHolder);
		this.bufferedNameHolder = new BufferedPropertyValueModel(this.nameAdapter, this.trigger);

		this.hireDateAdapter = this.buildHireDateAdapter(this.employeeHolder);
		this.bufferedHireDateHolder = new BufferedPropertyValueModel(this.hireDateAdapter, this.trigger);
	}

	private PropertyValueModel buildIDAdapter(ValueModel eHolder) {
		return new PropertyAspectAdapter(eHolder, Employee.ID_PROPERTY) {
			protected Object getValueFromSubject() {
				return new Integer(((Employee) this.subject).getID());
			}
			protected void setValueOnSubject(Object value) {
				((Employee) this.subject).setID(((Integer) value).intValue());
			}
		};
	}

	private PropertyValueModel buildNameAdapter(ValueModel eHolder) {
		return new PropertyAspectAdapter(eHolder, Employee.NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((Employee) this.subject).getName();
			}
			protected void setValueOnSubject(Object value) {
				((Employee) this.subject).setName((String) value);
			}
		};
	}

	private PropertyValueModel buildHireDateAdapter(ValueModel eHolder) {
		return new PropertyAspectAdapter(eHolder, Employee.HIRE_DATE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((Employee) this.subject).getHireDate();
			}
			protected void setValueOnSubject(Object value) {
				((Employee) this.subject).setHireDate((Date) value);
			}
		};
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetValue() {
		PropertyChangeListener bufferedListener = this.buildBufferedListener();
		this.bufferedIDHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);
		this.bufferedNameHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);
		this.bufferedHireDateHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);

		assertEquals(new Integer(17), this.idAdapter.getValue());
		assertEquals(new Integer(17), this.bufferedIDHolder.getValue());

		assertEquals("Freddy", this.employee.getName());
		assertEquals("Freddy", this.nameAdapter.getValue());
		assertEquals("Freddy", this.bufferedNameHolder.getValue());

		Date temp = this.employee.getHireDate();
		assertEquals(temp, this.employee.getHireDate());
		assertEquals(temp, this.hireDateAdapter.getValue());
		assertEquals(temp, this.bufferedHireDateHolder.getValue());

		this.bufferedIDHolder.setValue(new Integer(323));
		assertEquals(17, this.employee.getID());
		assertEquals(new Integer(17), this.idAdapter.getValue());
		assertEquals(new Integer(323), this.bufferedIDHolder.getValue());

		this.bufferedNameHolder.setValue("Ripley");
		assertEquals("Freddy", this.employee.getName());
		assertEquals("Freddy", this.nameAdapter.getValue());
		assertEquals("Ripley", this.bufferedNameHolder.getValue());

		this.bufferedHireDateHolder.setValue(null);
		assertEquals(temp, this.employee.getHireDate());
		assertEquals(temp, this.hireDateAdapter.getValue());
		assertEquals(null, this.bufferedHireDateHolder.getValue());
	}

	public void testAccept() {
		PropertyChangeListener bufferedListener = this.buildBufferedListener();
		this.bufferedIDHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);
		this.bufferedNameHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);
		this.bufferedHireDateHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);

		this.bufferedIDHolder.setValue(new Integer(323));
		assertEquals(17, this.employee.getID());
		assertEquals(new Integer(17), this.idAdapter.getValue());
		assertEquals(new Integer(323), this.bufferedIDHolder.getValue());

		this.bufferedNameHolder.setValue("Ripley");
		assertEquals("Freddy", this.employee.getName());
		assertEquals("Freddy", this.nameAdapter.getValue());
		assertEquals("Ripley", this.bufferedNameHolder.getValue());

		Date temp = this.employee.getHireDate();
		this.bufferedHireDateHolder.setValue(null);
		assertEquals(temp, this.employee.getHireDate());
		assertEquals(temp, this.hireDateAdapter.getValue());
		assertEquals(null, this.bufferedHireDateHolder.getValue());

		this.trigger.accept();

		assertEquals(323, this.employee.getID());
		assertEquals(new Integer(323), this.idAdapter.getValue());
		assertEquals(new Integer(323), this.bufferedIDHolder.getValue());

		assertEquals("Ripley", this.employee.getName());
		assertEquals("Ripley", this.nameAdapter.getValue());
		assertEquals("Ripley", this.bufferedNameHolder.getValue());

		assertEquals(null, this.employee.getHireDate());
		assertEquals(null, this.hireDateAdapter.getValue());
		assertEquals(null, this.bufferedHireDateHolder.getValue());
	}

	public void testReset() {
		PropertyChangeListener bufferedListener = this.buildBufferedListener();
		this.bufferedIDHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);
		this.bufferedNameHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);
		this.bufferedHireDateHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);

		this.bufferedIDHolder.setValue(new Integer(323));
		assertEquals(17, this.employee.getID());
		assertEquals(new Integer(17), this.idAdapter.getValue());
		assertEquals(new Integer(323), this.bufferedIDHolder.getValue());

		this.bufferedNameHolder.setValue("Ripley");
		assertEquals("Freddy", this.employee.getName());
		assertEquals("Freddy", this.nameAdapter.getValue());
		assertEquals("Ripley", this.bufferedNameHolder.getValue());

		Date temp = this.employee.getHireDate();
		this.bufferedHireDateHolder.setValue(null);
		assertEquals(temp, this.employee.getHireDate());
		assertEquals(temp, this.hireDateAdapter.getValue());
		assertEquals(null, this.bufferedHireDateHolder.getValue());

		this.trigger.reset();

		assertEquals(17, this.employee.getID());
		assertEquals(new Integer(17), this.idAdapter.getValue());
		assertEquals(new Integer(17), this.bufferedIDHolder.getValue());

		assertEquals("Freddy", this.employee.getName());
		assertEquals("Freddy", this.nameAdapter.getValue());
		assertEquals("Freddy", this.bufferedNameHolder.getValue());

		assertEquals(temp, this.employee.getHireDate());
		assertEquals(temp, this.hireDateAdapter.getValue());
		assertEquals(temp, this.bufferedHireDateHolder.getValue());
	}

	public void testLazyListening() {
		assertTrue(((AbstractModel) this.bufferedIDHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.bufferedNameHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.bufferedHireDateHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));

		assertTrue(((AbstractModel) this.idAdapter).hasNoPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.nameAdapter).hasNoPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.hireDateAdapter).hasNoPropertyChangeListeners(ValueModel.VALUE));

		assertTrue(this.employee.hasNoPropertyChangeListeners(Employee.ID_PROPERTY));
		assertTrue(this.employee.hasNoPropertyChangeListeners(Employee.NAME_PROPERTY));
		assertTrue(this.employee.hasNoPropertyChangeListeners(Employee.HIRE_DATE_PROPERTY));

		PropertyChangeListener bufferedListener = this.buildBufferedListener();
		this.bufferedIDHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);
		this.bufferedNameHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);
		this.bufferedHireDateHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);

		assertTrue(((AbstractModel) this.bufferedIDHolder).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.bufferedNameHolder).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.bufferedHireDateHolder).hasAnyPropertyChangeListeners(ValueModel.VALUE));

		assertTrue(((AbstractModel) this.idAdapter).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.nameAdapter).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.hireDateAdapter).hasAnyPropertyChangeListeners(ValueModel.VALUE));

		assertTrue(this.employee.hasAnyPropertyChangeListeners(Employee.ID_PROPERTY));
		assertTrue(this.employee.hasAnyPropertyChangeListeners(Employee.NAME_PROPERTY));
		assertTrue(this.employee.hasAnyPropertyChangeListeners(Employee.HIRE_DATE_PROPERTY));

		this.bufferedIDHolder.removePropertyChangeListener(ValueModel.VALUE, bufferedListener);
		this.bufferedNameHolder.removePropertyChangeListener(ValueModel.VALUE, bufferedListener);
		this.bufferedHireDateHolder.removePropertyChangeListener(ValueModel.VALUE, bufferedListener);

		assertTrue(((AbstractModel) this.bufferedIDHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.bufferedNameHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.bufferedHireDateHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));

		assertTrue(((AbstractModel) this.idAdapter).hasNoPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.nameAdapter).hasNoPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.hireDateAdapter).hasNoPropertyChangeListeners(ValueModel.VALUE));

		assertTrue(this.employee.hasNoPropertyChangeListeners(Employee.ID_PROPERTY));
		assertTrue(this.employee.hasNoPropertyChangeListeners(Employee.NAME_PROPERTY));
		assertTrue(this.employee.hasNoPropertyChangeListeners(Employee.HIRE_DATE_PROPERTY));
	}

	public void testPropertyChange1() {
		PropertyChangeListener bufferedListener = this.buildBufferedListener();
		this.bufferedNameHolder.addPropertyChangeListener(ValueModel.VALUE, bufferedListener);

		PropertyChangeListener adapterListener = this.buildAdapterListener();
		this.nameAdapter.addPropertyChangeListener(ValueModel.VALUE, adapterListener);

		PropertyChangeListener employeeListener = this.buildEmployeeListener();
		this.employee.addPropertyChangeListener(Employee.NAME_PROPERTY, employeeListener);

		this.verifyPropertyChanges();
	}

	public void testPropertyChange2() {
		PropertyChangeListener bufferedListener = this.buildBufferedListener();
		this.bufferedNameHolder.addPropertyChangeListener(bufferedListener);

		PropertyChangeListener adapterListener = this.buildAdapterListener();
		this.nameAdapter.addPropertyChangeListener(adapterListener);

		PropertyChangeListener employeeListener = this.buildEmployeeListener();
		this.employee.addPropertyChangeListener(employeeListener);

		this.verifyPropertyChanges();
	}

	private void verifyPropertyChanges() {
		this.bufferedEvent = null;
		this.adapterEvent = null;
		this.employeeEvent = null;
		this.bufferedNameHolder.setValue("Ripley");
		this.verifyEvent(this.bufferedEvent, this.bufferedNameHolder, ValueModel.VALUE, "Freddy", "Ripley");
		assertNull(this.adapterEvent);
		assertNull(this.employeeEvent);

		this.bufferedEvent = null;
		this.adapterEvent = null;
		this.employeeEvent = null;
		this.bufferedNameHolder.setValue("Charlie");
		this.verifyEvent(this.bufferedEvent, this.bufferedNameHolder, ValueModel.VALUE, "Ripley", "Charlie");
		assertNull(this.adapterEvent);
		assertNull(this.employeeEvent);

		this.bufferedEvent = null;
		this.adapterEvent = null;
		this.employeeEvent = null;
		this.trigger.accept();
		assertNull(this.bufferedEvent);
		this.verifyEvent(this.adapterEvent, this.nameAdapter, ValueModel.VALUE, "Freddy", "Charlie");
		this.verifyEvent(this.employeeEvent, this.employee, Employee.NAME_PROPERTY, "Freddy", "Charlie");

		this.bufferedEvent = null;
		this.adapterEvent = null;
		this.employeeEvent = null;
		this.bufferedNameHolder.setValue("Jason");
		this.verifyEvent(this.bufferedEvent, this.bufferedNameHolder, ValueModel.VALUE, "Charlie", "Jason");
		assertNull(this.adapterEvent);
		assertNull(this.employeeEvent);

		this.bufferedEvent = null;
		this.adapterEvent = null;
		this.employeeEvent = null;
		this.trigger.reset();
		this.verifyEvent(this.bufferedEvent, this.bufferedNameHolder, ValueModel.VALUE, "Jason", "Charlie");
		assertNull(this.adapterEvent);
		assertNull(this.employeeEvent);
	}

	private PropertyChangeListener buildBufferedListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				BufferedPropertyValueModelTests.this.bufferedEvent = e;
			}
		};
	}

	private PropertyChangeListener buildAdapterListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				BufferedPropertyValueModelTests.this.adapterEvent = e;
			}
		};
	}

	private PropertyChangeListener buildEmployeeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				BufferedPropertyValueModelTests.this.employeeEvent = e;
			}
		};
	}

	private void verifyEvent(PropertyChangeEvent event, Object source, String propertyName, Object oldValue, Object newValue) {
		assertEquals(source, event.getSource());
		assertEquals(propertyName, event.getPropertyName());
		assertEquals(oldValue, event.getOldValue());
		assertEquals(newValue, event.getNewValue());
	}


	// ********** inner class **********

	private class Employee extends AbstractModel {
		private int id;
			public static final String ID_PROPERTY = "id";
		private String name;
			public static final String NAME_PROPERTY = "name";
		private Date hireDate;
			public static final String HIRE_DATE_PROPERTY = "hireDate";

		Employee(int id, String name, Date hireDate) {
			super();
			this.id = id;
			this.name = name;
			this.hireDate = hireDate;
		}
		int getID() {
			return this.id;
		}
		void setID(int id) {
			int old = this.id;
			this.id = id;
			this.firePropertyChanged(ID_PROPERTY, old, id);
		}
		String getName() {
			return this.name;
		}
		void setName(String name) {
			Object old = this.name;
			this.name = name;
			this.firePropertyChanged(NAME_PROPERTY, old, name);
		}
		Date getHireDate() {
			return this.hireDate;
		}
		void setHireDate(Date hireDate) {
			Object old = this.hireDate;
			this.hireDate = hireDate;
			this.firePropertyChanged(HIRE_DATE_PROPERTY, old, hireDate);
		}
		public void toString(StringBuffer sb) {
			sb.append(this.name);
		}
	}
}
