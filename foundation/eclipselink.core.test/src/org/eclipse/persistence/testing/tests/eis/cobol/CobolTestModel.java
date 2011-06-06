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
package org.eclipse.persistence.testing.tests.eis.cobol;

import java.util.*;
import org.eclipse.persistence.internal.eis.cobol.*;
import org.eclipse.persistence.internal.helper.*;

public class CobolTestModel extends org.eclipse.persistence.testing.framework.TestModel {
    public CobolTestModel() {
        super();
        addTest(new CobolTestBaseSuite());
    }

    public static CobolRow getConversionRow() {
        Vector fields = new Vector();
        Vector values = new Vector();

        fields.addElement(new DatabaseField("emp-ssn"));
        values.addElement("123456789");

        fields.addElement(new DatabaseField("emp-salary"));
        values.addElement("25000.25");

        fields.addElement(new DatabaseField("emp-num-depend"));
        values.addElement("2");

        fields.addElement(new DatabaseField("emp-num-vacation"));
        values.addElement("-245");

        fields.addElement(new DatabaseField("emp-name"));
        Vector name = new Vector();
        Vector nameFields = new Vector();
        Vector nameValues = new Vector();
        nameFields.addElement(new DatabaseField("emp-name-first"));
        nameValues.addElement("John");
        nameFields.addElement(new DatabaseField("emp-name-last"));
        nameValues.addElement("Doe");
        name.addElement(new CobolRow(nameFields, nameValues));
        values.addElement(name);

        fields.addElement(new DatabaseField("emp-dependents"));
        Vector dependents = new Vector();
        Vector wifeFields = new Vector();
        Vector wifeValues = new Vector();
        wifeFields.addElement(new DatabaseField("emp-dependents-name"));
        wifeValues.addElement("Jane Doe");
        wifeFields.addElement(new DatabaseField("emp-dependents-ssn"));
        wifeValues.addElement("987654321");
        dependents.addElement(new CobolRow(wifeFields, wifeValues));
        Vector sonFields = new Vector();
        Vector sonValues = new Vector();
        sonFields.addElement(new DatabaseField("emp-dependents-name"));
        sonValues.addElement("John Doe Jr.");
        sonFields.addElement(new DatabaseField("emp-dependents-ssn"));
        sonValues.addElement("835873949");
        dependents.addElement(new CobolRow(sonFields, sonValues));
        values.addElement(dependents);

        fields.addElement(new DatabaseField("emp-tasks"));
        Vector tasks = new Vector();
        tasks.addElement("1001");
        tasks.addElement("1002");
        tasks.addElement("1003");
        tasks.addElement("1004");
        values.addElement(tasks);

        return new CobolRow(fields, values);
    }

    public static RecordMetaData getConversionRecord() {
        RecordMetaData record = new RecordMetaData("emp-record");

        ElementaryFieldMetaData ssn = new ElementaryFieldMetaData("emp-ssn", record);
        ssn.setSize(5);
        ssn.setOffset(0);
        ssn.setType(FieldMetaData.PACKED_DECIMAL);
        record.addField(ssn);

        ElementaryFieldMetaData salary = new ElementaryFieldMetaData("emp-salary", record);
        salary.setSize(7);
        salary.setOffset(5);
        salary.setType(FieldMetaData.ALPHA_NUMERIC);
        salary.setDecimalPosition(6);
        record.addField(salary);

        ElementaryFieldMetaData numDepend = new ElementaryFieldMetaData("emp-num-depend", record);
        numDepend.setSize(1);
        numDepend.setOffset(12);
        numDepend.setType(FieldMetaData.BINARY);
        record.addField(numDepend);

        ElementaryFieldMetaData vacationDays = new ElementaryFieldMetaData("emp-num-vacation", record);
        vacationDays.setSize(2);
        vacationDays.setOffset(13);
        vacationDays.setType(FieldMetaData.BINARY);
        vacationDays.setIsSigned(true);
        record.addField(vacationDays);

        CompositeFieldMetaData name = new CompositeFieldMetaData("emp-name", record);
        name.setOffset(15);
        ElementaryFieldMetaData lastName = new ElementaryFieldMetaData("emp-name-first", record);
        lastName.setSize(15);
        lastName.setOffset(15);
        lastName.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        name.addField(lastName);
        ElementaryFieldMetaData firstName = new ElementaryFieldMetaData("emp-name-last", record);
        firstName.setSize(15);
        firstName.setOffset(30);
        firstName.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        name.addField(firstName);

        record.addField(name);

        ElementaryFieldMetaData tasks = new ElementaryFieldMetaData("emp-tasks", record);
        tasks.setSize(4);
        tasks.setOffset(45);
        tasks.setType(FieldMetaData.ALPHA_NUMERIC);
        tasks.setArraySize(4);
        record.addField(tasks);

        ElementaryFieldMetaData department = new ElementaryFieldMetaData("emp-depart", record);
        department.setSize(15);
        department.setOffset(45);
        department.setType(FieldMetaData.ALPHA_NUMERIC);
        department.setFieldRedefined(tasks);
        department.setIsFieldRedefine(true);
        record.addField(department);

        CompositeFieldMetaData dependents = new CompositeFieldMetaData("emp-dependents", record);
        dependents.setOffset(61);
        dependents.setArraySize(10);
        dependents.setDependentFieldName("emp-num-depend");
        ElementaryFieldMetaData fullName = new ElementaryFieldMetaData("emp-dependents-name", record);
        fullName.setSize(30);
        fullName.setOffset(61);
        fullName.setType(FieldMetaData.ALPHA_NUMERIC);
        dependents.addField(fullName);
        ElementaryFieldMetaData number = new ElementaryFieldMetaData("emp-dependents-ssn", record);
        number.setSize(9);
        number.setOffset(91);
        number.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        dependents.addField(number);
        record.addField(dependents);

        return record;
    }

    public static String getSimpleCopyBookString() {
        return "01  emp-record." + Helper.cr() + "04  emp-ssn                   pic 9(12) comp-3." + Helper.cr() + "04  emp-name." + Helper.cr() + "06  emp-name-last       pic x(15)." + Helper.cr() + "06  emp-name-first      pic x(15)." + Helper.cr() + "06  emp-name-mi         pic x." + Helper.cr() + "04  emp-addr." + Helper.cr() + "06  emp-addr-street." + Helper.cr() + "08	emp-addr-street-no	pic 9(5)." + Helper.cr() + "08	emp-addr-street-name	pic x(15)." + Helper.cr() + "06  emp-addr-st         pic x(2)." + Helper.cr() + "06  emp-addr-zip        pic x(9).";
    }

    public static String getDeepNestedCopyBookString() {
        return "01 everything-record." + Helper.cr() + "03 everything-universe." + Helper.cr() + "05 universe-galaxycluster." + Helper.cr() + "07 galaxycluster-galaxy." + Helper.cr() + "09 galaxy-solarsystem." + Helper.cr() + "11 solarsystem-planet." + Helper.cr() + "13 planet-continent." + Helper.cr() + "15 continent-country." + Helper.cr() + "17 country-region." + Helper.cr() + "19 region-state." + Helper.cr() + "21 state-county." + Helper.cr() + "23 county-city." + Helper.cr() + "25 city-neighborhood." + Helper.cr() + "27 neighborhood-block." + Helper.cr() + "29 block-house." + Helper.cr() + "31 house-person." + Helper.cr() + "33 person-organ." + Helper.cr() + "35 organ-tissue." + Helper.cr() + "37 tissue-cell." + Helper.cr() + "39 cell-molecule." + Helper.cr() + "41 molecule-atom." + Helper.cr() + "43 atom-nucleus." + Helper.cr() + "45 nucleus-nuetron." + Helper.cr() + "47 nuetron-quark." + Helper.cr() + "49 quark-infinity.";
    }

    public static String getMultipleRecordString() {
        return Helper.cr() + Helper.cr() + Helper.cr() + Helper.cr() + Helper.cr() + "     *sldkfjaweitwoieawoeirgfa;e" + Helper.cr() + "     sherdfgkjhdsfglkjsdhfguheriuh" + Helper.cr() + Helper.cr() + "01 emp-time." + Helper.cr() + "05 time-hour			pic 99." + Helper.cr() + "05 time-minute			pic xx." + Helper.cr() + "05 time-second			pic xx." + Helper.cr() + Helper.cr() + "*********************" + "**************************" + Helper.cr() + Helper.cr() + Helper.cr() + "01 MEMBER-TABLE." + Helper.cr() + Helper.cr() + "05 CLUB-NAME PIC X(20)." + Helper.cr() + Helper.cr() + "05 NUM-MEMBERS PIC 9(5)." + Helper.cr() + Helper.cr() + "05 MEMBERS." + Helper.cr() + Helper.cr() + "10  MEM-NAME    PIC X(20)." + Helper.cr() + Helper.cr() + "10  MEM-NUM     PIC 9(15)." + Helper.cr() + Helper.cr() + Helper.cr() + Helper.cr() + "PROCEDURE DIVISION." + Helper.cr() + getSimpleCopyBookString();
    }

    public static String getOccursDependsCopyBookString() {
        return "01 club-record." + Helper.cr() + "05 club-enrollment pic 9(5)." + Helper.cr() + "05 club-members " + Helper.cr() + "occurs 1 to 50 times" + Helper.cr() + " depending on club-enrollment." + Helper.cr() + "10 member-name pic x(20)." + Helper.cr() + "10 member-number pic x(5).";
    }

    public static String getRedefinesCopyBookString() {
        return "01  emp-record." + Helper.cr() + "04  emp-ssn                   pic 9(12) comp-3." + Helper.cr() + "04  emp-ssn-int redefines emp-ssn    pic 9(12) comp." + Helper.cr() + "04  emp-addr." + Helper.cr() + "06  emp-addr-street." + Helper.cr() + "08	emp-addr-street-no	pic 9(5)." + Helper.cr() + "08	emp-addr-street-name	pic x(5)." + Helper.cr() + "06  emp-addr-rr redefines  emp-addr-street." + Helper.cr() + "08   	emp-addr-rr-no		pic 9(15)." + Helper.cr() + "08	emp-addr-rr-box		pic 9(5)." + Helper.cr() + "06  emp-addr-zip        pic x(9).";
    }

    public static String getComplexPicStatementCopyBookString() {
        return "01 emp-record." + Helper.cr() + "04 emp-salary      pic S9(5)V99 sign leading seperate." + Helper.cr() + "04 emp-ex         pic Z(5).ZZ." + Helper.cr() + "04 emp-ex2        pic -9(4).99." + Helper.cr() + "04 emp-ex3     pic +ZZ99.99." + Helper.cr() + "04 emp-ex4      pic ----9.99-." + Helper.cr() + "04 emp-ex5       pic S9(5).99.";
    }

    public static String getFillerCopyBookString() {
        return "01 emp-record." + Helper.cr() + "04     pic x(5)." + Helper.cr() + "04." + Helper.cr() + "07     pic xx." + Helper.cr() + "07    pic xx.";
    }

    public static RecordMetaData getFillerRecord() {
        RecordMetaData record = new RecordMetaData("emp-record");

        ElementaryFieldMetaData fill1 = new ElementaryFieldMetaData("filler", record);
        fill1.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        fill1.setSize(5);
        fill1.setOffset(0);
        record.addField(fill1);

        CompositeFieldMetaData fillComp = new CompositeFieldMetaData("filler", record);
        record.addField(fillComp);
        ElementaryFieldMetaData fill2 = new ElementaryFieldMetaData("filler", record);
        fill2.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        fill2.setSize(2);
        fill2.setOffset(5);
        fillComp.addField(fill2);
        ElementaryFieldMetaData fill3 = new ElementaryFieldMetaData("filler", record);
        fill3.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        fill3.setSize(2);
        fill3.setOffset(7);
        fillComp.addField(fill3);

        return record;
    }

    public static RecordMetaData getComplexPicStatementRecord() {
        RecordMetaData record = new RecordMetaData("emp-record");

        ElementaryFieldMetaData salary = new ElementaryFieldMetaData("emp-salary", record);
        salary.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        salary.setSize(8);
        salary.setOffset(0);
        salary.setDecimalPosition(5);
        salary.setIsSigned(true);
        record.addField(salary);

        ElementaryFieldMetaData ex = new ElementaryFieldMetaData("emp-ex", record);
        ex.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        ex.setSize(8);
        ex.setOffset(8);
        record.addField(ex);

        ElementaryFieldMetaData ex2 = new ElementaryFieldMetaData("emp-ex2", record);
        ex2.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        ex2.setSize(8);
        ex2.setOffset(16);
        record.addField(ex2);

        ElementaryFieldMetaData ex3 = new ElementaryFieldMetaData("emp-ex3", record);
        ex3.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        ex3.setSize(8);
        ex3.setOffset(24);
        record.addField(ex3);

        ElementaryFieldMetaData ex4 = new ElementaryFieldMetaData("emp-ex4", record);
        ex4.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        ex4.setSize(9);
        ex4.setOffset(32);
        record.addField(ex4);

        ElementaryFieldMetaData ex5 = new ElementaryFieldMetaData("emp-ex5", record);
        ex5.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        ex5.setSize(8);
        ex5.setOffset(41);
        ex5.setIsSigned(true);
        record.addField(ex5);

        return record;
    }

    public static RecordMetaData getRedefinesRecord() {
        RecordMetaData record = new RecordMetaData("emp-record");

        ElementaryFieldMetaData ssn = new ElementaryFieldMetaData("emp-ssn", record);
        ssn.setType(ElementaryFieldMetaData.PACKED_DECIMAL);
        ssn.setSize(7);
        ssn.setOffset(0);
        record.addField(ssn);

        ElementaryFieldMetaData ssnInt = new ElementaryFieldMetaData("emp-ssn-int", record);
        ssnInt.setType(ElementaryFieldMetaData.BINARY);
        ssnInt.setSize(5);
        ssnInt.setOffset(0);
        ssnInt.setIsFieldRedefine(true);
        ssnInt.setFieldRedefined(ssn);
        record.addField(ssnInt);

        CompositeFieldMetaData address = new CompositeFieldMetaData("emp-addr", record);
        CompositeFieldMetaData street = new CompositeFieldMetaData("emp-addr-street", record);
        ElementaryFieldMetaData streetNumber = new ElementaryFieldMetaData("emp-addr-street-no", record);
        streetNumber.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        streetNumber.setSize(5);
        streetNumber.setOffset(7);
        street.addField(streetNumber);
        ElementaryFieldMetaData streetName = new ElementaryFieldMetaData("emp-addr-street-name", record);
        streetName.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        streetName.setSize(5);
        streetName.setOffset(12);
        street.addField(streetName);
        address.addField(street);
        CompositeFieldMetaData ruralRoute = new CompositeFieldMetaData("emp-addr-rr", record);
        ruralRoute.setIsFieldRedefine(true);
        ruralRoute.setFieldRedefined(street);
        ElementaryFieldMetaData ruralRouteNum = new ElementaryFieldMetaData("emp-addr-rr-no", record);
        ruralRouteNum.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        ruralRouteNum.setSize(15);
        ruralRouteNum.setOffset(7);
        ruralRoute.addField(ruralRouteNum);
        ElementaryFieldMetaData ruralRouteBox = new ElementaryFieldMetaData("emp-addr-rr-box", record);
        ruralRouteBox.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        ruralRouteBox.setSize(5);
        ruralRouteBox.setOffset(22);
        ruralRoute.addField(ruralRouteBox);
        address.addField(ruralRoute);
        ElementaryFieldMetaData zip = new ElementaryFieldMetaData("emp-addr-zip", record);
        zip.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        zip.setSize(9);
        zip.setOffset(27);
        address.addField(zip);

        record.addField(address);
        return record;
    }

    public static RecordMetaData getOccursDependsRecord() {
        RecordMetaData record = new RecordMetaData("club-record");

        ElementaryFieldMetaData enrollment = new ElementaryFieldMetaData("club-enrollment", record);
        enrollment.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        enrollment.setSize(5);
        enrollment.setOffset(0);
        record.addField(enrollment);

        CompositeFieldMetaData members = new CompositeFieldMetaData("club-members", record);
        members.setArraySize(49);
        members.setDependentFieldName("club-enrollment");
        ElementaryFieldMetaData memName = new ElementaryFieldMetaData("member-name", record);
        memName.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        memName.setSize(20);
        memName.setOffset(5);
        members.addField(memName);
        ElementaryFieldMetaData memNum = new ElementaryFieldMetaData("member-number", record);
        memNum.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        memNum.setSize(5);
        memNum.setOffset(25);
        members.addField(memNum);
        record.addField(members);

        return record;
    }

    public static Vector getMultipleRecords() {
        Vector records = new Vector(2);
        RecordMetaData record = new RecordMetaData("emp-time");

        ElementaryFieldMetaData hour = new ElementaryFieldMetaData("time-hour", record);
        hour.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        hour.setSize(2);
        hour.setOffset(0);
        record.addField(hour);

        ElementaryFieldMetaData min = new ElementaryFieldMetaData("time-minute", record);
        min.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        min.setSize(2);
        min.setOffset(2);
        record.addField(min);

        ElementaryFieldMetaData sec = new ElementaryFieldMetaData("time-second", record);
        sec.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        sec.setSize(2);
        sec.setOffset(4);
        record.addField(sec);

        records.addElement(record);

        RecordMetaData record2 = new RecordMetaData("MEMBER-TABLE");

        ElementaryFieldMetaData name = new ElementaryFieldMetaData("CLUB-NAME", record2);
        name.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        name.setSize(20);
        name.setOffset(0);
        record2.addField(name);

        ElementaryFieldMetaData number = new ElementaryFieldMetaData("NUM-MEMBERS", record2);
        number.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        number.setSize(5);
        number.setOffset(20);
        record2.addField(number);

        CompositeFieldMetaData member = new CompositeFieldMetaData("MEMBERS", record2);
        ElementaryFieldMetaData memName = new ElementaryFieldMetaData("MEM-NAME", record2);
        memName.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        memName.setSize(20);
        memName.setOffset(25);
        member.addField(memName);
        ElementaryFieldMetaData memNum = new ElementaryFieldMetaData("MEM-NUM", record2);
        memNum.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        memNum.setSize(15);
        memNum.setOffset(45);
        member.addField(memNum);
        record2.addField(member);

        records.addElement(record2);

        return records;
    }

    public static RecordMetaData getDeepNestedRecord() {
        RecordMetaData everything = new RecordMetaData("everything-record");

        CompositeFieldMetaData universe = new CompositeFieldMetaData("everything-universe", everything);
        everything.addField(universe);
        CompositeFieldMetaData galaxyCluster = new CompositeFieldMetaData("universe-galaxycluster", everything);
        universe.addField(galaxyCluster);
        CompositeFieldMetaData galaxy = new CompositeFieldMetaData("galaxycluster-galaxy", everything);
        galaxyCluster.addField(galaxy);
        CompositeFieldMetaData solarSystem = new CompositeFieldMetaData("galaxy-solarsystem", everything);
        galaxy.addField(solarSystem);
        CompositeFieldMetaData planet = new CompositeFieldMetaData("solarsystem-planet", everything);
        solarSystem.addField(planet);
        CompositeFieldMetaData continent = new CompositeFieldMetaData("planet-continent", everything);
        planet.addField(continent);
        CompositeFieldMetaData country = new CompositeFieldMetaData("continent-country", everything);
        continent.addField(country);
        CompositeFieldMetaData region = new CompositeFieldMetaData("country-region", everything);
        country.addField(region);
        CompositeFieldMetaData state = new CompositeFieldMetaData("region-state", everything);
        region.addField(state);
        CompositeFieldMetaData county = new CompositeFieldMetaData("state-county", everything);
        state.addField(county);
        CompositeFieldMetaData city = new CompositeFieldMetaData("county-city", everything);
        county.addField(city);
        CompositeFieldMetaData neighborhood = new CompositeFieldMetaData("city-neighborhood", everything);
        city.addField(neighborhood);
        CompositeFieldMetaData block = new CompositeFieldMetaData("neighborhood-block", everything);
        neighborhood.addField(block);
        CompositeFieldMetaData house = new CompositeFieldMetaData("block-house", everything);
        block.addField(house);
        CompositeFieldMetaData person = new CompositeFieldMetaData("house-person", everything);
        house.addField(person);
        CompositeFieldMetaData organ = new CompositeFieldMetaData("person-organ", everything);
        person.addField(organ);
        CompositeFieldMetaData tissue = new CompositeFieldMetaData("organ-tissue", everything);
        organ.addField(tissue);
        CompositeFieldMetaData cell = new CompositeFieldMetaData("tissue-cell", everything);
        tissue.addField(cell);
        CompositeFieldMetaData molecule = new CompositeFieldMetaData("cell-molecule", everything);
        cell.addField(molecule);
        CompositeFieldMetaData atom = new CompositeFieldMetaData("molecule-atom", everything);
        molecule.addField(atom);
        CompositeFieldMetaData nucleus = new CompositeFieldMetaData("atom-nucleus", everything);
        atom.addField(nucleus);
        CompositeFieldMetaData nuetron = new CompositeFieldMetaData("nucleus-nuetron", everything);
        nucleus.addField(nuetron);
        CompositeFieldMetaData quark = new CompositeFieldMetaData("nuetron-quark", everything);
        nuetron.addField(quark);
        CompositeFieldMetaData infinity = new CompositeFieldMetaData("quark-infinity", everything);
        quark.addField(infinity);

        return everything;
    }

    public static RecordMetaData getSimpleRecord() {
        RecordMetaData record = new RecordMetaData("emp-record");

        ElementaryFieldMetaData ssn = new ElementaryFieldMetaData("emp-ssn", record);
        ssn.setSize(7);
        ssn.setOffset(0);
        ssn.setType(FieldMetaData.PACKED_DECIMAL);
        record.addField(ssn);

        CompositeFieldMetaData name = new CompositeFieldMetaData("emp-name", record);
        ElementaryFieldMetaData lastName = new ElementaryFieldMetaData("emp-name-last", record);
        lastName.setSize(15);
        lastName.setOffset(7);
        lastName.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        name.addField(lastName);
        ElementaryFieldMetaData firstName = new ElementaryFieldMetaData("emp-name-first", record);
        firstName.setSize(15);
        firstName.setOffset(22);
        firstName.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        name.addField(firstName);
        ElementaryFieldMetaData middleInit = new ElementaryFieldMetaData("emp-name-mi", record);
        middleInit.setSize(1);
        middleInit.setOffset(37);
        middleInit.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        name.addField(middleInit);

        record.addField(name);

        CompositeFieldMetaData address = new CompositeFieldMetaData("emp-addr", record);
        CompositeFieldMetaData street = new CompositeFieldMetaData("emp-addr-street", record);
        ElementaryFieldMetaData number = new ElementaryFieldMetaData("emp-addr-street-no", record);
        number.setSize(5);
        number.setOffset(38);
        number.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        street.addField(number);
        ElementaryFieldMetaData streetName = new ElementaryFieldMetaData("emp-addr-street-name", record);
        streetName.setSize(15);
        streetName.setOffset(43);
        streetName.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        street.addField(streetName);
        address.addField(street);

        ElementaryFieldMetaData state = new ElementaryFieldMetaData("emp-addr-st", record);
        state.setSize(2);
        state.setOffset(58);
        state.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        address.addField(state);
        ElementaryFieldMetaData zip = new ElementaryFieldMetaData("emp-addr-zip", record);
        zip.setSize(9);
        zip.setOffset(60);
        zip.setType(ElementaryFieldMetaData.ALPHA_NUMERIC);
        address.addField(zip);

        record.addField(address);
        return record;
    }

    public static boolean compareCobolRows(CobolRow row1, CobolRow row2) {
        if (row1 == row2) {
            return true;
        } else if ((row1 == null) || (row2 == null)) {
            return false;
        }

        if (row1.size() != row2.size()) {
            return false;
        }

        Enumeration fieldsEnum = row1.getFields().elements();
        while (fieldsEnum.hasMoreElements()) {
            DatabaseField field = (DatabaseField)fieldsEnum.nextElement();
            DatabaseField fieldMatch = row2.getField(field);
            if (fieldMatch == null) {
                return false;
            } else if (!compareValues(row1.get(field), row2.get(fieldMatch))) {
                return false;
            }
        }
        return true;
    }

    private static boolean compareValues(Object value1, Object value2) {
        if (value1 == value2) {
            return true;
        } else if ((value1 == null) || (value2 == null)) {
            return false;
        }
        if (value1 instanceof List) {
            if (compareListFieldValues((List)value1, (List)value2)) {
                return true;
            }
        }
        if (value1.getClass() != value2.getClass()) {
            return false;
        }
        if (value1 instanceof String) {
            if (value1.equals(value2)) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    private static boolean compareListFieldValues(List value1, List value2) {
        Iterator elementIter1 = value1.iterator();
        Iterator elementIter2 = value2.iterator();
        while (elementIter1.hasNext()) {
            Object value = elementIter1.next();
            if (value instanceof CobolRow) {
                if (!compareCobolRows((CobolRow)value, (CobolRow)elementIter2.next())) {
                    return false;
                }
            } else {
                if (!value.equals(elementIter2.next())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean compareCompositeObjects(CompositeObject object1, CompositeObject object2) {
        if (object1 == object2) {
            return true;
        } else if ((object1 == null) || (object2 == null)) {
            return false;
        }

        if (!object1.getName().equals(object2.getName())) {
            return false;
        }

        if (object1.getFields().size() != object2.getFields().size()) {
            return false;
        }

        Enumeration fieldsEnum = object1.getFields().elements();
        while (fieldsEnum.hasMoreElements()) {
            FieldMetaData field = (FieldMetaData)fieldsEnum.nextElement();
            FieldMetaData fieldMatch = object2.getFieldNamed(field.getName());
            if (fieldMatch == null) {
                return false;
            }
            if (!compareFields(field, fieldMatch)) {
                return false;
            }
        }
        return true;
    }

    public static boolean compareFields(FieldMetaData field1, FieldMetaData field2) {
        if (field1 == field2) {
            return true;
        } else if ((field1 == null) || (field2 == null)) {
            return false;
        }

        if ((field1.getArraySize() != field2.getArraySize()) || (!field1.getDependentFieldName().equals(field2.getDependentFieldName()))) {
            return false;
        }

        if (field1.isComposite() && field2.isComposite()) {
            if (!compareCompositeObjects((CompositeObject)field1, (CompositeObject)field2)) {
                return false;
            }
        } else if (field1.isComposite() || field2.isComposite()) {
            return false;
        } else {
            if (!compareElementaryFields((ElementaryFieldMetaData)field1, (ElementaryFieldMetaData)field2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean compareElementaryFields(ElementaryFieldMetaData field1, ElementaryFieldMetaData field2) {
        if (field1.isFieldRedefine() && field2.isFieldRedefine()) {
            if (!compareFields(field1.getFieldRedefined(), field2.getFieldRedefined())) {
                return false;
            }
        } else if (field1.isFieldRedefine() || field2.isFieldRedefine()) {
            return false;
        }

        if ((field1.getSize() == field2.getSize()) && (field1.getOffset() == field2.getOffset()) && (field1.getType() == field2.getType()) && (field1.getDecimalPosition() == field2.getDecimalPosition()) && (field1.isSigned() == field2.isSigned())) {
            return true;
        }
        return false;
    }
}
