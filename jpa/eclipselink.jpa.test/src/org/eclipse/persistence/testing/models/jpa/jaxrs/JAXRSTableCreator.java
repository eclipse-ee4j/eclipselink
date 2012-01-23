/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - inital implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.jaxrs;

import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class JAXRSTableCreator extends TogglingFastTableCreator {
	public JAXRSTableCreator() {
		setName("JAXRSProject");
		dropTableConstraints(JUnitTestCase.getServerSession());

		addTableDefinition(buildADDRESSTable());
		addTableDefinition(buildPHONENUMBERTable());
		addTableDefinition(buildCUSTOMERTable());

	}

	// CREATE TABLE "CUSTOMER" ( "ID" NUMBER NOT NULL ENABLE, "FIRST_NAME"
	// VARCHAR2(50), "LAST_NAME" VARCHAR2(50), CONSTRAINT "CUSTOMER_PK" PRIMARY
	// KEY ("ID") ENABLE ) /

	// CREATE TABLE "ADDRESS" ( "ID" NUMBER NOT NULL ENABLE, "STREET"
	// VARCHAR2(50), "CITY" VARCHAR2(50), CONSTRAINT "ADDRESS_PK" PRIMARY KEY
	// ("ID") ENABLE, CONSTRAINT "ADDRESS_FK" FOREIGN KEY ("ID") REFERENCES
	// "CUSTOMER" ("ID") ENABLE ) /

	// CREATE TABLE "PHONENUMBER" ( "ID" NUMBER NOT NULL ENABLE, "TYPE"
	// VARCHAR2(50), "NUM" VARCHAR2(50), "ID_CUSTOMER" NUMBER, CONSTRAINT
	// "PHONE_NUMBER_PK" PRIMARY KEY ("ID") ENABLE, CONSTRAINT "PHONE_NUMBER_FK"
	// FOREIGN KEY ("ID_CUSTOMER") REFERENCES "CUSTOMER" ("ID") ENABLE ) /

	/**
	 * Dropping old tables/foreign keys from schema change.
	 */
	public void replaceTables(DatabaseSession session) {

		super.replaceTables(session);
	}

	/**
	 * Drop table constraints
	 */
	public void dropTableConstraints(Session session) {

		if (session.getLogin().getPlatform().isOracle()) {
			try {
				session.executeNonSelectingCall(new SQLCall(
						"drop table CUSTOMER CASCADE CONSTRAINTS"));
			} catch (Exception e) {
			}
			try {
				session.executeNonSelectingCall(new SQLCall(
						"drop table ADDRESS CASCADE CONSTRAINTS"));
			} catch (Exception e) {
			}
			try {
				session.executeNonSelectingCall(new SQLCall(
						"drop table PHONENUMBER CASCADE CONSTRAINTS"));
			} catch (Exception e) {
			}

		} else {
			try {
				session.executeNonSelectingCall(new SQLCall(
						"drop table CUSTOMER"));
			} catch (Exception e) {
			}
			try {
				session.executeNonSelectingCall(new SQLCall(
						"drop table ADDRESS"));
			} catch (Exception e) {
			}
			try {
				session.executeNonSelectingCall(new SQLCall(
						"drop table PHONENUMBER"));
			} catch (Exception e) {
			}
		}
	}

	public TableDefinition buildCUSTOMERTable() {
		TableDefinition table = new TableDefinition();
		table.setName("CUSTOMER");

		FieldDefinition field = new FieldDefinition();
		field.setName("ID");
		field.setTypeName("NUMERIC");
		field.setSize(15);
		field.setShouldAllowNull(false);
		field.setIsPrimaryKey(true);
		field.setUnique(false);
		field.setIsIdentity(true);
		table.addField(field);

		FieldDefinition field1 = new FieldDefinition();
		field1.setName("FIRST_NAME");
		field1.setTypeName("VARCHAR2");
		field1.setSize(50);
		field1.setShouldAllowNull(true);
		field1.setIsPrimaryKey(false);
		field1.setUnique(false);
		field1.setIsIdentity(false);
		table.addField(field1);

		FieldDefinition field2 = new FieldDefinition();
		field2.setName("LAST_NAME");
		field2.setTypeName("VARCHAR2");
		field2.setSize(50);
		field2.setShouldAllowNull(true);
		field2.setIsPrimaryKey(false);
		field2.setUnique(false);
		field2.setIsIdentity(false);
		table.addField(field2);

		return table;
	}

	public TableDefinition buildADDRESSTable() {
		TableDefinition table = new TableDefinition();
		table.setName("ADDRESS");

		FieldDefinition field = new FieldDefinition();
		field.setName("ID");
		field.setTypeName("NUMERIC");
		field.setSize(15);
		field.setShouldAllowNull(false);
		field.setIsPrimaryKey(true);
		field.setUnique(false);
		field.setIsIdentity(true);
		field.setForeignKeyFieldName("CUSTOMER.ID");
		table.addField(field);

		FieldDefinition fieldSTREET = new FieldDefinition();
		fieldSTREET.setName("STREET");
		fieldSTREET.setTypeName("VARCHAR2");
		fieldSTREET.setSize(50);
		fieldSTREET.setSubSize(0);
		fieldSTREET.setIsPrimaryKey(false);
		fieldSTREET.setIsIdentity(false);
		fieldSTREET.setUnique(false);
		fieldSTREET.setShouldAllowNull(true);
		table.addField(fieldSTREET);

		FieldDefinition fieldCITY = new FieldDefinition();
		fieldCITY.setName("CITY");
		fieldCITY.setTypeName("VARCHAR2");
		fieldCITY.setSize(50);
		fieldCITY.setSubSize(0);
		fieldCITY.setIsPrimaryKey(false);
		fieldCITY.setIsIdentity(false);
		fieldCITY.setUnique(false);
		fieldCITY.setShouldAllowNull(true);
		table.addField(fieldCITY);

		return table;
	}

	public TableDefinition buildPHONENUMBERTable() {
		TableDefinition table = new TableDefinition();
		table.setName("PHONENUMBER");

		FieldDefinition field = new FieldDefinition();
		field.setName("ID");
		field.setTypeName("NUMERIC");
		field.setSize(15);
		field.setShouldAllowNull(false);
		field.setIsPrimaryKey(true);
		field.setUnique(false);
		field.setIsIdentity(false);
		table.addField(field);

		FieldDefinition field1 = new FieldDefinition();
		field1.setName("TYPE");
		field1.setTypeName("VARCHAR2");
		field1.setSize(50);
		field1.setShouldAllowNull(true);
		field1.setIsPrimaryKey(false);
		field1.setUnique(false);
		field1.setIsIdentity(false);
		table.addField(field1);

		FieldDefinition field3 = new FieldDefinition();
		field3.setName("NUM");
		field3.setTypeName("VARCHAR2");
		field3.setSize(50);
		field3.setShouldAllowNull(true);
		field3.setIsPrimaryKey(false);
		field3.setUnique(false);
		field3.setIsIdentity(false);
		table.addField(field3);

		FieldDefinition field4 = new FieldDefinition();
		field4.setName("ID_CUSTOMER");
		field4.setTypeName("NUMERIC");
		field4.setSize(15);
		field4.setShouldAllowNull(true);
		field4.setIsPrimaryKey(false);
		field4.setUnique(false);
		field4.setIsIdentity(false);
		field4.setForeignKeyFieldName("CUSTOMER.ID");
		table.addField(field4);

		return table;
	}

}
