/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - initial
//      Dmitry Kornilov - Moved to another package, upgrade to Jersey 2.x
package org.eclipse.persistence.jpars.test.server.noversion;

import org.eclipse.persistence.jpars.test.BaseJparsTest;
import org.eclipse.persistence.jpars.test.model.employee.Certification;
import org.eclipse.persistence.jpars.test.model.employee.Employee;
import org.eclipse.persistence.jpars.test.model.employee.EmployeeAddress;
import org.eclipse.persistence.jpars.test.model.employee.EmploymentPeriod;
import org.eclipse.persistence.jpars.test.model.employee.Gender;
import org.eclipse.persistence.jpars.test.model.employee.LargeProject;
import org.eclipse.persistence.jpars.test.model.employee.Office;
import org.eclipse.persistence.jpars.test.model.employee.PhoneNumber;
import org.eclipse.persistence.jpars.test.model.employee.SmallProject;
import org.eclipse.persistence.jpars.test.server.RestCallFailedException;
import org.eclipse.persistence.jpars.test.util.DBUtils;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Employee model tests.
 */
public class ServerEmployeeTest extends BaseJparsTest {
    private static final long THREE_YEARS = 94608000000L;

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_employee-static", null);
    }

    @After
    public void cleanup() throws Exception {
        RestUtils.restUpdateQuery(context, "Employee.deleteAll", null, null, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test read employee json.
     */
    @Test
    public void testReadEmployeeJSON() throws Exception {
        readEmployee(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test read employee xml.
     */
    @Test
    public void testReadEmployeeXML() throws Exception {
        readEmployee(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test update employee with employment period json.
     */
    @Test
    public void testUpdateEmployeeWithEmploymentPeriodJSON() throws Exception {
        updateEmployeeWithEmploymentPeriod(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test update employee with employment period xml.
     */
    @Test
    public void testUpdateEmployeeWithEmploymentPeriodXML() throws Exception {
        updateEmployeeWithEmploymentPeriod(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test create employee with phone numbers json.
     */
    @Test
    public void testCreateEmployeeWithPhoneNumbersJSON() throws Exception {
        createEmployeeWithPhoneNumbers("employee-with-phoneNumber.json");
    }

    /**
     * Test create employee with address json.
     */
    @Test
    public void testCreateEmployeeWithAddressJSON() throws Exception {
        String msg = RestUtils.getJSONMessage("employee-with-address.json");
        String employee = RestUtils.restUpdateStr(context, msg, Employee.class, null, MediaType.APPLICATION_JSON_TYPE);
        assertNotNull(employee);
        String addressLink = "\"address\":{\"_link\":{\"href\":\"" + getServerURI() + "/entity/EmployeeAddress/201404";
        assertTrue(employee.contains(addressLink));

        RestUtils.restDelete(context, 20130, Employee.class);
    }

    /**
     * Test update employee with manager json.
     */
    @Test
    public void testUpdateEmployeeWithManagerJSON() throws Exception {
        updateEmployeeWithManager(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test update employee with manager xml.
     */
    @Test
    public void testUpdateEmployeeWithManagerXML() throws Exception {
        updateEmployeeWithManager(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test update employee with project json.
     */
    @Test
    public void testUpdateEmployeeWithProjectJSON() throws Exception {
        updateEmployeeWithProject(MediaType.APPLICATION_JSON_TYPE, false);
    }

    /**
     * Test update employee with project xml.
     */
    @Test
    public void testUpdateEmployeeWithProjectXML() throws Exception {
        updateEmployeeWithProject(MediaType.APPLICATION_XML_TYPE, false);
    }

    /**
     * Test update employee with project remove all projects json.
     */
    @Test
    public void testUpdateEmployeeWithProjectRemoveAllProjectsJSON() throws Exception {
        updateEmployeeWithProject(MediaType.APPLICATION_JSON_TYPE, true);
    }

    /**
     * Test update employee with project remove all projects xml.
     */
    @Test
    public void testUpdateEmployeeWithProjectRemoveAllProjectsXML() throws Exception {
        updateEmployeeWithProject(MediaType.APPLICATION_XML_TYPE, true);
    }

    /**
     * Test create employee address with binary data json.
     */
    @Test
    public void testCreateEmployeeAddressWithBinaryDataJSON() throws Exception {
        createEmployeeAddressWithBinaryData(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test create employee address with binary data xml.
     */
    @Test
    public void testCreateEmployeeAddressWithBinaryDataXML() throws Exception {
        createEmployeeAddressWithBinaryData(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test execute query get all xml.
     */
    @Test
    public void testExecuteQueryGetAllXML() throws Exception {
        executeQueryGetAll(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test execute query get all json.
     */
    @Test
    public void testExecuteQueryGetAllJSON() throws Exception {
        executeQueryGetAll(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test execute single result query xml.
     */
    @Test
    public void testExecuteSingleResultQueryXML() throws Exception {
        executeSingleResultQuery(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test execute single result query json.
     */
    @Test
    public void testExecuteSingleResultQueryJSON() throws Exception {
        executeSingleResultQuery(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test read employee with responsibilities xml.
     */
    @Test
    public void testReadEmployeeWithResponsibilitiesXML() throws Exception {
        readEmployeeWithResponsibilities(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test read employee with responsibilities json.
     */
    @Test
    public void testReadEmployeeWithResponsibilitiesJSON() throws Exception {
        readEmployeeWithResponsibilities(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test multi result query get employee address with simple fields xml.
     */
    @Test
    public void testMultiResultQueryGetEmployeeAddressWithSimpleFieldsXML() throws Exception {
        executeMultiResultQueryGetEmployeeAddressWithSimpleFields(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test multi result query get employee address with simple fields json.
     */
    @Test
    public void testMultiResultQueryGetEmployeeAddressWithSimpleFieldsJSON() throws Exception {
        executeMultiResultQueryGetEmployeeAddressWithSimpleFields(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test multi result query get employee with domain object json.
     */
    @Test
    public void testMultiResultQueryGetEmployeeWithDomainObjectJSON() throws Exception {
        executeMultiResultQueryGetEmployeeWithDomainObject(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test multi result query get employee with domain object xml.
     */
    @Test
    public void testMultiResultQueryGetEmployeeWithDomainObjectXML() throws Exception {
        executeMultiResultQueryGetEmployeeWithDomainObject(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test employee address multi result named query with binary data xml.
     */
    @Test
    public void testEmployeeAddressMultiResultNamedQueryWithBinaryDataXML() throws Exception {
        getEmployeeAddressMultiResultNamedQueryWithBinaryData(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test employee address multi result named query with binary data json.
     */
    @Test
    public void testEmployeeAddressMultiResultNamedQueryWithBinaryDataJSON() throws Exception {
        getEmployeeAddressMultiResultNamedQueryWithBinaryData(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test read employee responsibilities xml.
     */
    @Test
    public void testReadEmployeeResponsibilitiesXML() throws Exception {
        readEmployeeResponsibilities(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test read employee responsibilities json.
     */
    @Test
    public void testReadEmployeeResponsibilitiesJSON() throws Exception {
        readEmployeeResponsibilities(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test single result query get employee with domain object json.
     */
    @Test
    public void testSingleResultQueryGetEmployeeWithDomainObjectJSON() throws Exception {
        executeSingleResultQueryGetEmployeeWithDomainObject(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test single result query get employee with domain object xml.
     */
    @Test
    public void testSingleResultQueryGetEmployeeWithDomainObjectXML() throws Exception {
        executeSingleResultQueryGetEmployeeWithDomainObject(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test multi result query max xml.
     */
    @Test
    public void testMultiResultQueryMaxXML() throws Exception {
        executeMultiResultQueryMax(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test multi result query max json.
     */
    @Test
    public void testMultiResultQueryMaxJSON() throws Exception {
        executeMultiResultQueryMax(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test single result query max xml.
     */
    @Test
    public void testSingleResultQueryMaxXML() throws Exception {
        executeSingleResultQueryMax(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test single result query max json.
     */
    @Test
    public void testSingleResultQueryMaxJSON() throws Exception {
        executeSingleResultQueryMax(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test multi result query count xml.
     */
    @Test
    public void testMultiResultQueryCountXML() throws Exception {
        executeMultiResultQueryCount(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test multi result query count json.
     */
    @Test
    public void testMultiResultQueryCountJSON() throws Exception {
        executeMultiResultQueryCount(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test single result query count xml.
     */
    @Test
    public void testSingleResultQueryCountXML() throws Exception {
        executeSingleResultQueryCount(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test single result query count json.
     */
    @Test
    public void testSingleResultQueryCountJSON() throws Exception {
        executeSingleResultQueryCount(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test employee address single result named query with binary data xml.
     */
    @Test
    public void testEmployeeAddressSingleResultNamedQueryWithBinaryDataXML() throws Exception {
        getEmployeeAddressSingleResultNamedQueryWithBinaryData(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test employee address single result named query with binary data json.
     */
    @Test
    public void testEmployeeAddressSingleResultNamedQueryWithBinaryDataJSON() throws Exception {
        getEmployeeAddressSingleResultNamedQueryWithBinaryData(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test employee address single result named query with binary data octet stream.
     */
    @Test
    public void testEmployeeAddressSingleResultNamedQueryWithBinaryDataOctetStream() throws Exception {
        getEmployeeAddressSingleResultNamedQueryWithBinaryData(MediaType.APPLICATION_OCTET_STREAM_TYPE);
    }

    /**
     * Test read employee with address lazy fetch one2 one xml.
     */
    @Test
    public void testReadEmployeeWithAddressLazyFetchOne2OneXML() throws Exception {
        readEmployeeWithAddressLazyFetchOne2One(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test read employee with address lazy fetch one2 one json.
     */
    @Test
    public void testReadEmployeeWithAddressLazyFetchOne2OneJSON() throws Exception {
        readEmployeeWithAddressLazyFetchOne2One(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test create employee with expertise non idempotent.
     */
    @Test(expected = RestCallFailedException.class)
    public void testCreateEmployeeWithExpertiseNonIdempotent() throws Exception {
        String employee = RestUtils.getJSONMessage("employee-expertiseByValueNoId.json");
        // The expertise object contained by the employee object has generated id field, and create is idempotent.
        // So, create operation on employee with expertise list should fail.
        RestUtils.restCreateWithSequence(context, employee, Employee.class, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test create office with employee non idempotent.
     */
    @Test(expected = RestCallFailedException.class)
    public void testCreateOfficeWithEmployeeNonIdempotent() throws Exception {
        String office = RestUtils.getJSONMessage("office-employeeByValueNoId.json");
        RestUtils.restCreateWithSequence(context, office, Office.class, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test create employee with office many to one inverse mapping.
     */
    @Test
    public void testCreateEmployeeWithOfficeManyToOneInverseMapping() throws Exception {
        final String msg = RestUtils.getJSONMessage("employee-officeByValueNoId.json");

        // office should be ignored (inverse mapping) and employee should be created successfully
        final Employee employee = RestUtils.restCreateStr(context, msg, Employee.class, MediaType.APPLICATION_JSON_TYPE);
        assertNotNull(employee);
        assertTrue(employee.getId() == 121098);
        assertNull(employee.getOffice());

        RestUtils.restDelete(context, employee.getId(), Employee.class, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test query employee find all json.
     */
    @Test
    public void testQueryEmployeeFindAllJSON() throws Exception {
        queryEmployeeFindAll(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test query employee find all xml.
     */
    @Test
    public void testQueryEmployeeFindAllXML() throws Exception {
        queryEmployeeFindAll(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test read employee with certifications xml.
     */
    @Test
    public void testReadEmployeeWithCertificationsXML() throws Exception {
        readEmployeeWithCertifications(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test read employee with certifications json.
     */
    @Test
    public void testReadEmployeeWithCertificationsJSON() throws Exception {
        readEmployeeWithCertifications(MediaType.APPLICATION_JSON_TYPE);
    }

    protected void queryEmployeeFindAll(MediaType mediaType) throws Exception {
        // create an employee
        Employee employee1 = new Employee();
        employee1.setFirstName("Billie");
        employee1.setLastName("Holiday");
        employee1 = RestUtils.restUpdate(context, employee1, Employee.class, null, mediaType, true);
        assertNotNull("Employee create failed.", employee1);

        // create another employee
        Employee employee2 = new Employee();
        employee2.setFirstName("Ella");
        employee2.setLastName("Fitzgerald");
        employee2.setGender(Gender.Female);
        employee2 = RestUtils.restUpdate(context, employee2, Employee.class, null, mediaType, true);
        assertNotNull("Employee create failed.", employee2);

        // query employee
        String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "Employee.findAll", null, null, mediaType);
        assertNotNull("Query all employees failed.", queryResult);

        assertTrue(queryResult.contains(employee1.getFirstName()));
        assertTrue(queryResult.contains(employee1.getLastName()));

        assertTrue(queryResult.contains(employee2.getFirstName()));
        assertTrue(queryResult.contains(employee1.getLastName()));

        // delete employees
        RestUtils.restDelete(context, employee1.getId(), Employee.class, mediaType);
        RestUtils.restDelete(context, employee2.getId(), Employee.class, mediaType);
    }

    protected void readEmployeeWithAddressLazyFetchOne2One(MediaType mediaType) throws Exception {
        // address on employee object is annotated to be LAZY fetch
        // Create an employee without any address, and make sure that read employee response doesn't contain address,
        // then add an address to the employee and re-read the employee to make sure that the response contains a link to the address
        // even if it is fetched lazily.

        // create an employee
        Employee employee = new Employee();
        employee.setFirstName("Diana");
        employee.setLastName("Krall");

        employee = RestUtils.restUpdate(context, employee, Employee.class, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);
        assertTrue(employee.getAddress() == null);

        // create an address for this employee
        EmployeeAddress address = new EmployeeAddress();
        address.setCity("Toronto");
        address.setStreet("Queen street");
        address.setPostalCode("ON");
        address.setCountry("CA");
        address.setPostalCode("H0H 0H0");

        address = RestUtils.restUpdate(context, address, EmployeeAddress.class, null, mediaType, true);
        assertNotNull("Employee address create failed.", address);
        assertTrue("Queen street".equals(address.getStreet()));
        assertTrue("H0H 0H0".equals(address.getPostalCode()));

        // update employee with address
        String result = RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class,
                "address", address, mediaType, null, true);
        assertNotNull(result);

        // make sure that response from restUpdateBidirectionalRelationship contains newly added address in employee object
        String addressLinkHref = getServerURI() + "/entity/EmployeeAddress/" + address.getId();
        assertTrue(result.contains(addressLinkHref));

        // read employee with an address
        String employeeWithAddress = RestUtils.restRead(context, employee.getId(), Employee.class, mediaType);
        assertNotNull("Employee read failed.", employeeWithAddress);
        // make sure employee has address
        assertTrue(employeeWithAddress.contains(addressLinkHref));

        // delete employee (cascade deletes address)
        RestUtils.restDelete(context, employee.getId(), Employee.class, mediaType);
    }

    protected void getEmployeeAddressSingleResultNamedQueryWithBinaryData(MediaType mediaType) throws Exception {
        EmployeeAddress address = new EmployeeAddress("New York City", "USA", "NY", "10005", "Wall Street");
        byte[] manhattan = RestUtils.convertImageToByteArray("manhattan.png");
        address.setAreaPicture(manhattan);

        address = RestUtils.restUpdate(context, address, EmployeeAddress.class, null, MediaType.APPLICATION_JSON_TYPE, true);
        assertNotNull("EmployeeAddress create failed.", address);
        assertNotNull("EmployeeAddress area picture is null", address.getAreaPicture());
        assertTrue("New York City".equals(address.getCity()));
        assertTrue("USA".equals(address.getCountry()));
        assertTrue("NY".equals(address.getProvince()));
        assertTrue("10005".equals(address.getPostalCode()));
        assertTrue("Wall Street".equals(address.getStreet()));

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", address.getId());

        // query
        if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            String expected = "<item><areaPicture>89504E470D0A1A0A0000000D49484452000000960000009608030000000BDF81D000000300504C5445CFDEE2EFEFEFE6E6E6F7F7F7FFFFFFDEDEDEF7FDFF00C4F163DBF608C6F153D7F6D6F6FDEBFAFD99E7F9F0FCFE30CFF4B6EEFB21CCF310C8F258D8F68BE4F9CFF4FCDFF8FD7DE1F8E6F9FEBCEFFB3BD2F4C3F1FC84E3F84AD5F53AD1F46BDDF717C9F295E6F919CAF272DEF7A8EBFAA6EAFAACECFA29CDF345D0F143D4F586E1F688DAEE6ED9F1E9F4F6A3DDEAD5DDDFA2E0EFD7F1F7CFE6EA0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003A4D766F00000A084944415478DAE55CDB72DC36124537E05896E424BEC8962B4925D9AD4DEDD3FEFF67EC5BDED74E7993527CD9B5654983466F0304392087177088E1A86AC7374933260F4F779FBE002418751F5FA8FEFF60B1E3FB08CB6904E27B070BE48F41473579749F7C4B9B489933D9468503462227D74C00E15BAB8FCE964B2943E0DAAE4786D581A0EF89CBF7E1D07C6C58FD71E703731A9A59CD860987129908C78AC4F113133F70C730228F9B571B3E0A2C65F7F4BEC3C202FD77FDD7713E0D0C51AA0F84EB5C3DFF7CE5F04BBFCE62C5152A8675D97A4C576797784523C1C066D8010F15880F94BAD1833E4DA212167CA65CD7882F7FF7A458EE4F38080247DEA341F53A9011FD612FBF0CDA886D90D5610D390C5B17574A3DFBB722335C5B546EEF59EBD3DD83C042DC28B521D6C31F50105C0B7C65BD96117FB81685B81D3B3685F4132B325E87ADF377DE4E44389A2F9D10A645B798711DB6EEBCBFDF8E6A8F1064A0FA57AF1589AFE4CFAD9A6A731CC44A91578225D1FFF22A47A881068B583C80666DD4C6D2E881EB776120291E80AD679FD5D9ED4411181902BB5EAA7EAFD4899BE87378A21B2A0FEB04FCD938BF855C05163E735E9660499D7F0097D79FBC8DEE264ECC13C88AB3F5689C0CAAD9E0558DF8E87A41CB7D305868265A1CEE68E93AB0BEBEEEAD093451858E72BAB1E2B04ECF07E25F8749A00EB53B9163695D59AF06EBD1DBC6713A641804499500723EF0153C00385A09D6F3FFC42F3EFFDC04A3B5D5D9D957C95220DBD8ED38E933DC3A139B0F8D4F6DD0F9A64B52B250435A7E49996CDB1108C0ABB0F5707B9AB717EAC92FFEAA597E868C5571A52326E629AD2FA9F2E9A48FEFE8C3DFFED002C70FB3424351FDDC77161A08E287B91F5B41542F13177EF149BD70AF31A447636AE726E79082B6196408C2C5FD725F92ACA4E47AAB2E7E3FE1FA0CDAB838C4D102D326F99A078A8972B0427F1F5F1FD55F3EE0C3EBC088AB144BA94AA9F4B62F1413A3EE0FBA72B0126348717AE5CEDEF9830B4FE8ACB5D2B0C6C64B77F8ED9DCE159B9D9E27A32C9182F36F7E0BFD72DD5AB37C237E3ED802756AFA52917802C9555F5CF3DD7FAB09047A6D90FE59831FD4381C8045DAC1218C7879D3AE8999EBB988288131E2EA3CD012065D0171342C0F0BFFECCCDCDCB6C489EB7548235EE9BB6BE6E2B01EA7D5DFE9E6A4C54604686840D6757C23015E06D6451A4E68DFDD0DB4AC0349D0C49F1B5714D6C969EA59CFF0D6F5A991246D6DED6866D6F53A68115897290B2FBF48ABEC867A4343A319A26E3D4AC8D6933709ACEFF0F6B67FC2A789335412C3FA6C09582625EBDDDDDDD0CC544F773CFE681E5701239E7E4CBEF9FEC68475802557E98AA83C2555E64FD7FC59F1C2634A622800EB6C93B8F5A7EBE1B1777ED68702464C62EBFBF73765DCB500ACAD139FBE16C1A412475C0E0BB7527AEE7CCE655B00D8625C8983DFC4A4AC1C2E0CA2E56C9D5E345FBEF2C51370F6DE90E1A1DC7258275B67BAAD0B19B0B488AC02B0DCD6B7CEAAC249F29E46B784AC02B03E5CF4E8342BD8CFF1C93A5FD62F6F319215547DD7F8BFD5FB683DD515D762B64E7E4C6AD4B4A0813DDCAB99E12C86F5EA5FA9369B4463F7D81458AE3A4DDAC3EFFE6C08B26ADE7E2DD519E12C8695E46976EDC32E08C6C5B09E069EE0DBC75F7DFD9A931E8BF7F1AE866053C2883FBDB1EF157D4AE65BD2AE4AA383A48FC59678D1F99BC7FF6046D35ECAD7928590D687F530FCFDC7573F6E6E9F86D31BD889AB7D370F1799D83CBDFC550DCC62EA5295F202135C41973FFB75C0145CCDDB94659C67CFBD613D7D52F5141F5FA8E7DF8C0F3D2A9FCBB2272DF6AD709A27BF79A1FFE79859AC8824BB99BEBF7775FA25D4333F7C78F596AE06DD46AA54E7A7B8AC1920A736AC87820B5DFE21DCB01B3E4425FB1CB66A65356A0E0A45E2F8C9C200D2449B433EACC591387E5948359979CE05A574CBE5E61770D3566CB69A2CEFC772636C1335AC800DB2948FB26BABFC5454606023B19F83CACC91AEC5B0B4562E776B3EAE6844073AF373C18854AD63F7FF9FA6625BCC16E5D6EC90ACE56B7DB09C5837E65A1DE0B5749E988F0AEADA7ED0F5A5A1D67BA5EA70AF5A9A436C866FD61AE9D7817D8264D74F864BFA73332030B003C85FB0BF8874E58673AA02DDD4D0E1E31E92EBDB320213453319D216EB6DE5612D3EB515D5254AF69441A24F7EFB797B731D3B89885A14F61851722BFA8187DF282024697109D35AEBABAD923DF393B6086D386DD2DF76FE779BBF5D58CD75208AB511C3C6E86E71372F5ED0F31AFC2A54AAD57501B78089DBE28811A72D53933563524450A1F24BBF8C8DBD124B52FBBE0C9C8D6A77BC32D52FC8E9A1DAC06C21161C959AEBEDBE0D6CBB3CCE3DD736B226B5216E268B61EC9DC1A745E7977FE3FA1CC08E34EC2FA7D9F589AE2A2C096314C163E79C84631009EB8392FCDD0F429F83C9CE27C788D1B972340B6D5C180F6E2EFD0FFA88F3DEED34A1381BF8250B2E927CE6544E467B0351E51F1476EFDB6A13BFDFB1E1D9DCF40DCBCDFE30674C89206829806A26D00216037FA28B3DF7C3A29AEFF3F15A325CCCFBBCFF98F1A6D3E08DE765407BA120174F67332A0876D94EAFA70542FB9CE25BEB2A8B493E433431AB61542E368698A93D0ED8AFB0C1ACD968BCD1A2BE52082E061B0EFCA0645EB248E0ACE436015B628F4D88AFA9850AC1414CB6A9842D40A83F5C147E2D1509EE16CC7DB07261BAEA486E68CD29746936AC93402500DEB98DC35839128894016D9233A25A94133BFE8EFD3796136FA33C7C42842A8ABA50CD4652A3F8BD968F3545132F8CC456F438B4FD296A3BFF4E37F6B0F024D613C9107FF241DA9BC3BA6C6592D5CE173DC3055FF641B321A259CBF194A970A346F76E0DBFF56C584E394B293B76D3BD25BDD9B2B8D10D3E5D157C694DB35B057661719672E74C69E4CAB6F3BFE40BE7CB058E3C0A450F7A6BDC8E112B1B4E90B5EBE33D56B4A18946A82EA165F4785563B7A29BF9E9AEE74D487B6BAA3A270643D849512218E4F7C40648635EDC61CB8BDC44FBB7731796AF0BA03589A0AAC0F3E042CF14575D9AA5836957E9C002624894A47756EA46FABB5D4313D4B68AB6B39851A8756039EF8B63D38E9E3BD65A0140D141776E5EE68AA2CC877AB46171251EC3B87AEFA30B67ACF0C078F0DA07994B9F6D58CD850FF8FCAE05B38698AE728B6C505D5830118BFBACC341B69F0F550CBCDB75ED912FB73481A32858D208CE9A83B5D81A1F9C13CCD8D1932EB94C3EC263444E451626F6C5CC4145CD304530ED516AD66CCD5861C876A83D58EAB0358D6AD64E319F64F8C1820168608BA7A3C4CEF2585EBCB1CC4C5025D948CF3360899799182610CEA66A0D585CDDDDB1F60BA7DFE7233C960E47DB50EB3400A8E3C01AA48CB4B42CF668B006D4DD6276D3711058FDF5E571008DB51873EB90C3C0E21D4C78E487427A58DD6D1FEEF80FAAC4DD25C1A3845E77B082367DE8A0EB5F533B8211536E8C53F780AA60449D8ABABA272F4C968E49DF17547E71C59BCDAAFE87021D8F2D3F4BF40B4400F707D5411F2CBAE0F53F4891BE36E04AC8330000000049454E44AE426082</areaPicture></item>";
            String result = RestUtils.restNamedSingleResultQueryResult(context, "EmployeeAddress.getPicture", parameters, null, mediaType);
            assertNotNull(result);
            assertTrue(result.contains(expected));
        } else if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            String expected = "{\"areaPicture\":\"89504E470D0A1A0A0000000D49484452000000960000009608030000000BDF81D000000300504C5445CFDEE2EFEFEFE6E6E6F7F7F7FFFFFFDEDEDEF7FDFF00C4F163DBF608C6F153D7F6D6F6FDEBFAFD99E7F9F0FCFE30CFF4B6EEFB21CCF310C8F258D8F68BE4F9CFF4FCDFF8FD7DE1F8E6F9FEBCEFFB3BD2F4C3F1FC84E3F84AD5F53AD1F46BDDF717C9F295E6F919CAF272DEF7A8EBFAA6EAFAACECFA29CDF345D0F143D4F586E1F688DAEE6ED9F1E9F4F6A3DDEAD5DDDFA2E0EFD7F1F7CFE6EA0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003A4D766F00000A084944415478DAE55CDB72DC36124537E05896E424BEC8962B4925D9AD4DEDD3FEFF67EC5BDED74E7993527CD9B5654983466F0304392087177088E1A86AC7374933260F4F779FBE002418751F5FA8FEFF60B1E3FB08CB6904E27B070BE48F41473579749F7C4B9B489933D9468503462227D74C00E15BAB8FCE964B2943E0DAAE4786D581A0EF89CBF7E1D07C6C58FD71E703731A9A59CD860987129908C78AC4F113133F70C730228F9B571B3E0A2C65F7F4BEC3C202FD77FDD7713E0D0C51AA0F84EB5C3DFF7CE5F04BBFCE62C5152A8675D97A4C576797784523C1C066D8010F15880F94BAD1833E4DA212167CA65CD7882F7FF7A458EE4F38080247DEA341F53A9011FD612FBF0CDA886D90D5610D390C5B17574A3DFBB722335C5B546EEF59EBD3DD83C042DC28B521D6C31F50105C0B7C65BD96117FB81685B81D3B3685F4132B325E87ADF377DE4E44389A2F9D10A645B798711DB6EEBCBFDF8E6A8F1064A0FA57AF1589AFE4CFAD9A6A731CC44A91578225D1FFF22A47A881068B583C80666DD4C6D2E881EB776120291E80AD679FD5D9ED4411181902BB5EAA7EAFD4899BE87378A21B2A0FEB04FCD938BF855C05163E735E9660499D7F0097D79FBC8DEE264ECC13C88AB3F5689C0CAAD9E0558DF8E87A41CB7D305868265A1CEE68E93AB0BEBEEEAD093451858E72BAB1E2B04ECF07E25F8749A00EB53B9163695D59AF06EBD1DBC6713A641804499500723EF0153C00385A09D6F3FFC42F3EFFDC04A3B5D5D9D957C95220DBD8ED38E933DC3A139B0F8D4F6DD0F9A64B52B250435A7E49996CDB1108C0ABB0F5707B9AB717EAC92FFEAA597E868C5571A52326E629AD2FA9F2E9A48FEFE8C3DFFED002C70FB3424351FDDC77161A08E287B91F5B41542F13177EF149BD70AF31A447636AE726E79082B6196408C2C5FD725F92ACA4E47AAB2E7E3FE1FA0CDAB838C4D102D326F99A078A8972B0427F1F5F1FD55F3EE0C3EBC088AB144BA94AA9F4B62F1413A3EE0FBA72B0126348717AE5CEDEF9830B4FE8ACB5D2B0C6C64B77F8ED9DCE159B9D9E27A32C9182F36F7E0BFD72DD5AB37C237E3ED802756AFA52917802C9555F5CF3DD7FAB09047A6D90FE59831FD4381C8045DAC1218C7879D3AE8999EBB988288131E2EA3CD012065D0171342C0F0BFFECCCDCDCB6C489EB7548235EE9BB6BE6E2B01EA7D5DFE9E6A4C54604686840D6757C23015E06D6451A4E68DFDD0DB4AC0349D0C49F1B5714D6C969EA59CFF0D6F5A991246D6DED6866D6F53A68115897290B2FBF48ABEC867A4343A319A26E3D4AC8D6933709ACEFF0F6B67FC2A789335412C3FA6C09582625EBDDDDDDD0CC544F773CFE681E5701239E7E4CBEF9FEC68475802557E98AA83C2555E64FD7FC59F1C2634A622800EB6C93B8F5A7EBE1B1777ED68702464C62EBFBF73765DCB500ACAD139FBE16C1A412475C0E0BB7527AEE7CCE655B00D8625C8983DFC4A4AC1C2E0CA2E56C9D5E345FBEF2C51370F6DE90E1A1DC7258275B67BAAD0B19B0B488AC02B0DCD6B7CEAAC249F29E46B784AC02B03E5CF4E8342BD8CFF1C93A5FD62F6F319215547DD7F8BFD5FB683DD515D762B64E7E4C6AD4B4A0813DDCAB99E12C86F5EA5FA9369B4463F7D81458AE3A4DDAC3EFFE6C08B26ADE7E2DD519E12C8695E46976EDC32E08C6C5B09E069EE0DBC75F7DFD9A931E8BF7F1AE866053C2883FBDB1EF157D4AE65BD2AE4AA383A48FC59678D1F99BC7FF6046D35ECAD7928590D687F530FCFDC7573F6E6E9F86D31BD889AB7D370F1799D83CBDFC550DCC62EA5295F202135C41973FFB75C0145CCDDB94659C67CFBD613D7D52F5141F5FA8E7DF8C0F3D2A9FCBB2272DF6AD709A27BF79A1FFE79859AC8824BB99BEBF7775FA25D4333F7C78F596AE06DD46AA54E7A7B8AC1920A736AC87820B5DFE21DCB01B3E4425FB1CB66A65356A0E0A45E2F8C9C200D2449B433EACC591387E5948359979CE05A574CBE5E61770D3566CB69A2CEFC772636C1335AC800DB2948FB26BABFC5454606023B19F83CACC91AEC5B0B4562E776B3EAE6844073AF373C18854AD63F7FF9FA6625BCC16E5D6EC90ACE56B7DB09C5837E65A1DE0B5749E988F0AEADA7ED0F5A5A1D67BA5EA70AF5A9A436C866FD61AE9D7817D8264D74F864BFA73332030B003C85FB0BF8874E58673AA02DDD4D0E1E31E92EBDB320213453319D216EB6DE5612D3EB515D5254AF69441A24F7EFB797B731D3B89885A14F61851722BFA8187DF282024697109D35AEBABAD923DF393B6086D386DD2DF76FE779BBF5D58CD75208AB511C3C6E86E71372F5ED0F31AFC2A54AAD57501B78089DBE28811A72D53933563524450A1F24BBF8C8DBD124B52FBBE0C9C8D6A77BC32D52FC8E9A1DAC06C21161C959AEBEDBE0D6CBB3CCE3DD736B226B5216E268B61EC9DC1A745E7977FE3FA1CC08E34EC2FA7D9F589AE2A2C096314C163E79C84631009EB8392FCDD0F429F83C9CE27C788D1B972340B6D5C180F6E2EFD0FFA88F3DEED34A1381BF8250B2E927CE6544E467B0351E51F1476EFDB6A13BFDFB1E1D9DCF40DCBCDFE30674C89206829806A26D00216037FA28B3DF7C3A29AEFF3F15A325CCCFBBCFF98F1A6D3E08DE765407BA120174F67332A0876D94EAFA70542FB9CE25BEB2A8B493E433431AB61542E368698A93D0ED8AFB0C1ACD968BCD1A2BE52082E061B0EFCA0645EB248E0ACE436015B628F4D88AFA9850AC1414CB6A9842D40A83F5C147E2D1509EE16CC7DB07261BAEA486E68CD29746936AC93402500DEB98DC35839128894016D9233A25A94133BFE8EFD3796136FA33C7C42842A8ABA50CD4652A3F8BD968F3545132F8CC456F438B4FD296A3BFF4E37F6B0F024D613C9107FF241DA9BC3BA6C6592D5CE173DC3055FF641B321A259CBF194A970A346F76E0DBFF56C584E394B293B76D3BD25BDD9B2B8D10D3E5D157C694DB35B057661719672E74C69E4CAB6F3BFE40BE7CB058E3C0A450F7A6BDC8E112B1B4E90B5EBE33D56B4A18946A82EA165F4785563B7A29BF9E9AEE74D487B6BAA3A270643D849512218E4F7C40648635EDC61CB8BDC44FBB7731796AF0BA03589A0AAC0F3E042CF14575D9AA5836957E9C002624894A47756EA46FABB5D4313D4B68AB6B39851A8756039EF8B63D38E9E3BD65A0140D141776E5EE68AA2CC877AB46171251EC3B87AEFA30B67ACF0C078F0DA07994B9F6D58CD850FF8FCAE05B38698AE728B6C505D5830118BFBACC341B69F0F550CBCDB75ED912FB73481A32858D208CE9A83B5D81A1F9C13CCD8D1932EB94C3EC263444E451626F6C5CC4145CD304530ED516AD66CCD5861C876A83D58EAB0358D6AD64E319F64F8C1820168608BA7A3C4CEF2585EBCB1CC4C5025D948CF3360899799182610CEA66A0D585CDDDDB1F60BA7DFE7233C960E47DB50EB3400A8E3C01AA48CB4B42CF668B006D4DD6276D3711058FDF5E571008DB51873EB90C3C0E21D4C78E487427A58DD6D1FEEF80FAAC4DD25C1A3845E77B082367DE8A0EB5F533B8211536E8C53F780AA60449D8ABABA272F4C968E49DF17547E71C59BCDAAFE87021D8F2D3F4BF40B4400F707D5411F2CBAE0F53F4891BE36E04AC8330000000049454E44AE426082\"}";
            String result = RestUtils.restNamedSingleResultQueryResult(context, "EmployeeAddress.getPicture", parameters, null, mediaType);
            assertNotNull(result);
            assertTrue(result.equals(expected));
        } else if (mediaType == MediaType.APPLICATION_OCTET_STREAM_TYPE) {
            byte[] expectedResult = RestUtils.restNamedSingleResultQueryInByteArray(context, "EmployeeAddress.getPicture", parameters, null, mediaType);
            assertTrue(Arrays.equals(manhattan, expectedResult));
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }

        // delete employee address
        RestUtils.restDelete(context, address.getId(), EmployeeAddress.class);
    }

    protected void executeMultiResultQueryMax(MediaType mediaType) throws Exception {
        // create an employee
        Employee employee1 = new Employee();
        employee1.setFirstName("Miles");
        employee1.setLastName("Davis");
        employee1.setGender(Gender.Male);
        employee1.setSalary((double) 20000);

        employee1 = RestUtils.restUpdate(context, employee1, Employee.class, null, mediaType, true);
        assertNotNull("Employee1 create failed.", employee1);

        // create another employee
        Employee employee2 = new Employee();
        employee2.setFirstName("Charlie");
        employee2.setLastName("Parker");
        employee2.setGender(Gender.Male);
        employee2.setSalary((double) 30000);

        employee2 = RestUtils.restUpdate(context, employee2, Employee.class, null, mediaType, true);
        assertNotNull("Employee2 create failed.", employee2);

        // query
        String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "Employee.salaryMax", null, null, mediaType);
        assertNotNull(queryResult);

        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            assertTrue(queryResult.contains("{\"id\":" + employee2.getId() + ",\"max_salary\":30000.0}"));
            assertTrue(queryResult.contains("{\"id\":" + employee1.getId() + ",\"max_salary\":20000.0}"));
        } else if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            assertTrue(queryResult.contains("<List>"));
            assertTrue(queryResult.contains("<item><id>" + employee1.getId()));
            assertTrue(queryResult.contains("<max_salary>20000.0"));
            assertTrue(queryResult.contains("<item><id>" + employee2.getId()));
            assertTrue(queryResult.contains("<max_salary>30000.0<"));
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }

        // delete employees
        RestUtils.restDelete(context, employee1.getId(), Employee.class, mediaType);
        RestUtils.restDelete(context, employee2.getId(), Employee.class, mediaType);
    }

    protected void executeSingleResultQueryMax(MediaType mediaType) throws Exception {
        // create an employee
        Employee employee = new Employee();
        employee.setFirstName("Miles");
        employee.setLastName("Davis");
        employee.setGender(Gender.Male);
        employee.setSalary((double) 20000);

        employee = RestUtils.restUpdate(context, employee, Employee.class, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        // query
        String queryResult = RestUtils.restNamedSingleResultQueryResult(context, "Employee.salaryMax", null, null, mediaType);
        assertNotNull(queryResult);

        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            assertTrue(queryResult.equals("{\"id\":" + employee.getId() + ",\"max_salary\":20000.0}"));
        } else if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            assertTrue(!queryResult.contains("<List>"));
            assertTrue(queryResult.contains("<item><id>" + employee.getId()));
            assertTrue(queryResult.contains("<max_salary>20000.0<"));
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }

        // delete employee
        RestUtils.restDelete(context, employee.getId(), Employee.class, mediaType);
    }

    protected void executeSingleResultQueryCount(MediaType mediaType) throws Exception {
        // create an employee
        Employee employee1 = new Employee();
        employee1.setFirstName("Miles");
        employee1.setLastName("Davis");
        employee1.setGender(Gender.Male);

        employee1 = RestUtils.restUpdate(context, employee1, Employee.class, null, mediaType, true);
        assertNotNull("Employee1 create failed.", employee1);

        // create another employee
        Employee employee2 = new Employee();
        employee2.setFirstName("Charlie");
        employee2.setLastName("Parker");
        employee2.setGender(Gender.Male);

        employee2 = RestUtils.restUpdate(context, employee2, Employee.class, null, mediaType, true);
        assertNotNull("Employee2 create failed.", employee2);

        // query
        String queryResult = RestUtils.restNamedSingleResultQueryResult(context, "Employee.count", null, null, mediaType);
        assertNotNull(queryResult);

        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            assertTrue(queryResult.equals("{\"COUNT\":2}"));
        } else if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            assertTrue(!queryResult.contains("<List>"));
            assertTrue(queryResult.contains("<item><COUNT>2<"));
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }

        // delete employees
        RestUtils.restDelete(context, employee1.getId(), Employee.class, mediaType);
        RestUtils.restDelete(context, employee2.getId(), Employee.class, mediaType);
    }

    protected void executeMultiResultQueryCount(MediaType mediaType) throws Exception {
        // create an employee
        Employee employee1 = new Employee();
        employee1.setFirstName("Miles");
        employee1.setLastName("Davis");
        employee1.setGender(Gender.Male);

        employee1 = RestUtils.restUpdate(context, employee1, Employee.class, null, mediaType, true);
        assertNotNull("Employee1 create failed.", employee1);

        // create another employee
        Employee employee2 = new Employee();
        employee2.setFirstName("Charlie");
        employee2.setLastName("Parker");
        employee2.setGender(Gender.Male);

        employee2 = RestUtils.restUpdate(context, employee2, Employee.class, null, mediaType, true);
        assertNotNull("Employee2 create failed.", employee2);

        // query
        String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "Employee.count", null, null, mediaType);
        assertNotNull(queryResult);

        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            assertTrue(queryResult.equals("[{\"COUNT\":2}]"));
        } else if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            assertTrue(queryResult.contains("<List>"));
            assertTrue(queryResult.contains("<item><COUNT>2<"));
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }

        // delete employees
        RestUtils.restDelete(context, employee1.getId(), Employee.class, mediaType);
        RestUtils.restDelete(context, employee2.getId(), Employee.class, mediaType);
    }

    protected void executeMultiResultQueryGetEmployeeWithDomainObject(MediaType mediaType) throws Exception {
        // create an employee
        Employee employee = new Employee();
        employee.setFirstName("Miles");
        employee.setLastName("Davis");
        employee.setGender(Gender.Male);

        employee = RestUtils.restUpdate(context, employee, Employee.class, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        // create a manager
        Employee manager = new Employee();
        manager.setFirstName("Charlie");
        manager.setLastName("Parker");
        manager.setGender(Gender.Male);

        manager = RestUtils.restUpdate(context, manager, Employee.class, null, mediaType, true);
        assertNotNull("Employee manager create failed.", manager);

        // update employee with manager
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class,
                "manager", manager, mediaType, "managedEmployees", true);

        // read manager and verify that the relationship is set correctly
        manager = RestUtils.restReadObject(context, manager.getId(), Employee.class, mediaType);
        assertNotNull("Manager read failed.", manager);
        assertNotNull("Manager's managed employee list is null", manager.getManagedEmployees());
        assertTrue("Manager's managed employee list is empty", manager.getManagedEmployees().size() > 0);

        employee = RestUtils.restReadObject(context, employee.getId(), Employee.class, mediaType);
        assertNotNull("Manager read failed.", employee);

        for (Employee emp : manager.getManagedEmployees()) {
            assertNotNull("Managed employee's first name is null", emp.getFirstName());
            assertNotNull("Managed employee's last name is null", emp.getLastName());
        }

        // query
        String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "Employee.getManager", null, null, mediaType);
        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            assertTrue(queryResult.contains("[{\"firstName\":\"Miles\",\"lastName\":\"Davis\",\"manager\""));
            assertTrue(queryResult.contains("managedEmployees\":[{"));
            assertTrue(queryResult.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId()));
            assertTrue(queryResult.contains("{\"firstName\":\"Charlie\",\"lastName\":\"Parker\"}"));
        } else if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            assertTrue(queryResult.contains("<item><firstName>Miles</firstName><lastName>Davis</lastName><manager"));
            assertTrue(queryResult.contains("<managedEmployees><_link"));
            assertTrue(queryResult.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId()));
            assertTrue(queryResult.contains("</managedEmployees>"));
            assertTrue(queryResult.contains("<item><firstName>Charlie</firstName><lastName>Parker</lastName></item>"));
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }

        // delete employee
        RestUtils.restDelete(context, employee.getId(), Employee.class, mediaType);

        // delete manager
        RestUtils.restDelete(context, manager.getId(), Employee.class, mediaType);
    }

    protected void readEmployeeWithResponsibilities(MediaType mediaType) throws Exception {
        // create an employee
        Employee employee = new Employee();
        employee.setId(11025);
        employee.setFirstName("Miles");
        employee.setLastName("Davis");

        employee.addResponsibility("team lead");
        employee.addResponsibility("standard lead");
        employee.addResponsibility("er team member");

        employee = RestUtils.restCreate(context, employee, Employee.class, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        employee = RestUtils.restReadObject(context, 11025, Employee.class, mediaType);
        assertNotNull(employee.getResponsibilities());
        assertTrue(employee.getResponsibilities().size() == 3);

        // delete employee
        RestUtils.restDelete(context, 11025, Employee.class, mediaType);
    }

    protected void executeQueryGetAll(MediaType mediaType) throws Exception {
        // create address1
        EmployeeAddress address1 = new EmployeeAddress("New York City", "USA", "NY", "10005", "Wall Street");
        address1 = RestUtils.restUpdate(context, address1, EmployeeAddress.class, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address1);
        assertTrue("New York City".equals(address1.getCity()));
        assertTrue("USA".equals(address1.getCountry()));
        assertTrue("NY".equals(address1.getProvince()));
        assertTrue("10005".equals(address1.getPostalCode()));
        assertTrue("Wall Street".equals(address1.getStreet()));

        // create address2
        EmployeeAddress address2 = new EmployeeAddress("Ottawa", "Canada", "Ontario", "K1Y 6F7", "Main Street");
        address2 = RestUtils.restUpdate(context, address2, EmployeeAddress.class, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address2);
        assertTrue("Ottawa".equals(address2.getCity()));
        assertTrue("Canada".equals(address2.getCountry()));
        assertTrue("Ontario".equals(address2.getProvince()));
        assertTrue("K1Y 6F7".equals(address2.getPostalCode()));
        assertTrue("Main Street".equals(address2.getStreet()));

        // query
        String result = RestUtils.restNamedMultiResultQueryResult(context, "EmployeeAddress.getAll", null, null, mediaType);
        String expected1;
        String expected2;
        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            expected1 = "{\"city\":\"New York City\",\"country\":\"USA\",\"id\":" + address1.getId() + ",\"postalCode\":\"10005\",\"province\":\"NY\",\"street\":\"Wall Street\",\"_relationships\":[]}";
            expected2 = "{\"city\":\"Ottawa\",\"country\":\"Canada\",\"id\":" + address2.getId() + ",\"postalCode\":\"K1Y 6F7\",\"province\":\"Ontario\",\"street\":\"Main Street\",\"_relationships\":[]}";
        } else if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            expected1 = "<city>New York City</city><country>USA</country><id>" + address1.getId() + "</id><postalCode>10005</postalCode><province>NY</province><street>Wall Street</street>";
            expected2 = "<city>Ottawa</city><country>Canada</country><id>" + address2.getId() + "</id><postalCode>K1Y 6F7</postalCode><province>Ontario</province><street>Main Street</street>";
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }

        assertTrue(result.contains(expected1));
        assertTrue(result.contains(expected2));

        // delete employee address
        RestUtils.restDelete(context, address1.getId(), EmployeeAddress.class, mediaType);
        RestUtils.restDelete(context, address2.getId(), EmployeeAddress.class, mediaType);
    }

    protected void executeMultiResultQueryGetEmployeeAddressWithSimpleFields(MediaType mediaType) throws Exception {
        // create address1
        EmployeeAddress address1 = new EmployeeAddress("Washington", "USA", "WA", "99999", "Main");
        address1 = RestUtils.restUpdate(context, address1, EmployeeAddress.class, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address1);
        assertTrue("Washington".equals(address1.getCity()));
        assertTrue("USA".equals(address1.getCountry()));
        assertTrue("WA".equals(address1.getProvince()));
        assertTrue("99999".equals(address1.getPostalCode()));
        assertTrue("Main".equals(address1.getStreet()));

        // create address2
        EmployeeAddress address2 = new EmployeeAddress("Halifax", "Canada", "NS", "K1A5A7", "Queen");
        address2 = RestUtils.restUpdate(context, address2, EmployeeAddress.class, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address2);
        assertTrue("Halifax".equals(address2.getCity()));
        assertTrue("Canada".equals(address2.getCountry()));
        assertTrue("NS".equals(address2.getProvince()));
        assertTrue("K1A5A7".equals(address2.getPostalCode()));
        assertTrue("Queen".equals(address2.getStreet()));

        // query
        String result = RestUtils.restNamedMultiResultQueryResult(context, "EmployeeAddress.getRegion", null, null, mediaType);
        assertNotNull(result);
        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            assertTrue(result.contains("[{\"postalCode\":\"99999\",\"province\":\"WA\",\"street\":\"Main\"},{\"postalCode\":\"K1A5A7\",\"province\":\"NS\",\"street\":\"Queen\"}]"));
        } else if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            assertTrue(result.contains("<List><item><postalCode>99999</postalCode><province>WA</province><street>Main</street></item><item><postalCode>K1A5A7</postalCode><province>NS</province><street>Queen</street></item></List>"));
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }

        // delete employee address
        RestUtils.restDelete(context, address1.getId(), EmployeeAddress.class, mediaType);
        RestUtils.restDelete(context, address2.getId(), EmployeeAddress.class, mediaType);
    }

    protected void executeSingleResultQuery(MediaType mediaType) throws Exception {
        EmployeeAddress address = new EmployeeAddress("Newyork City", "USA", "NY", "10005", "Wall Street");
        address.setId(9112);
        address.setAreaPicture(RestUtils.convertImageToByteArray("manhattan.png"));

        address = RestUtils.restCreate(context, address, EmployeeAddress.class, null, mediaType, true);

        assertNotNull("EmployeeAddress create failed.", address);
        assertNotNull("EmployeeAddress area picture is null", address.getAreaPicture());
        assertTrue("Newyork City".equals(address.getCity()));
        assertTrue("USA".equals(address.getCountry()));
        assertTrue("NY".equals(address.getProvince()));
        assertTrue("10005".equals(address.getPostalCode()));
        assertTrue("Wall Street".equals(address.getStreet()));
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", address.getId());

        String result = RestUtils.restNamedSingleResultQueryResult(context, "EmployeeAddress.getById", parameters, null, mediaType);
        assertNotNull(result);

        String expected;
        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            expected = "{\"areaPicture\":\"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\",\"city\":\"Newyork City\",\"country\":\"USA\",\"id\":9112,\"postalCode\":\"10005\",\"province\":\"NY\",\"street\":\"Wall Street\",\"_relationships\":[]}";
        } else if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            expected = "<areaPicture>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</areaPicture><city>Newyork City</city><country>USA</country><id>9112</id><postalCode>10005</postalCode><province>NY</province><street>Wall Street</street>";
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }

        assertTrue(result.contains(expected));

        // delete employee address
        RestUtils.restDelete(context, address.getId(), EmployeeAddress.class, mediaType);
    }

    protected void createEmployeeAddressWithBinaryData(MediaType mediaType) throws Exception {
        EmployeeAddress address = new EmployeeAddress("Newyork City", "USA", "NY", "10005", "Wall Street");
        address.setAreaPicture(RestUtils.convertImageToByteArray("manhattan.png"));

        address = RestUtils.restUpdate(context, address, EmployeeAddress.class, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address);
        assertNotNull("EmployeeAddress area picture is null", address.getAreaPicture());
        assertTrue("Newyork City".equals(address.getCity()));
        assertTrue("USA".equals(address.getCountry()));
        assertTrue("NY".equals(address.getProvince()));
        assertTrue("10005".equals(address.getPostalCode()));
        assertTrue("Wall Street".equals(address.getStreet()));

        // delete employee address
        RestUtils.restDelete(context, address.getId(), EmployeeAddress.class, mediaType);
    }

    protected void updateEmployeeWithProject(MediaType mediaType, boolean removeAllProjects) throws Exception {
        // create an employee
        Employee employee = new Employee();
        employee.setFirstName("Charles");
        employee.setLastName("Mingus");
        employee.setGender(Gender.Male);

        employee = RestUtils.restUpdate(context, employee, Employee.class, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        // create a small project
        SmallProject smallProject = new SmallProject("SmallProject", "This is a small project.");

        smallProject = RestUtils.restUpdate(context, smallProject, SmallProject.class, null, mediaType, true);
        assertNotNull("SmallProject create failed.", smallProject);

        // update employee with small project
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class,
                "projects", smallProject, mediaType, "teamLeader", true);

        // create a large project
        LargeProject largeProject = new LargeProject();
        largeProject.setName("LargeProject");
        largeProject.setBudget(100000);

        largeProject = RestUtils.restUpdate(context, largeProject, LargeProject.class, null, mediaType, true);
        assertNotNull("LargeProject create failed.", largeProject);

        // update employee with large project
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class,
                "projects", largeProject, mediaType, "teamLeader", true);

        // read employee and verify that the relationship is set correctly for the projects
        String employeeUpdated = RestUtils.restRead(context, employee.getId(), Employee.class, mediaType);
        assertNotNull("Employee read failed.", employeeUpdated);
        assertTrue(employeeUpdated.contains("SmallProject/" + smallProject.getId()));
        assertTrue(employeeUpdated.contains("LargeProject/" + largeProject.getId()));

        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            if (removeAllProjects) {
                // remove all projects
                String allProjectsRemoved = RestUtils.restRemoveBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class, "projects", mediaType, "teamLeader", null);
                // verify relationships
                assertTrue(allProjectsRemoved.contains("\"firstName\":\"Charles\",\"gender\":\"Male\",\"id\":" + employee.getId() + ",\"lastName\":\"Mingus\",\"responsibilities\":[],\"salary\":0.0"));
                assertTrue(allProjectsRemoved.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/manager"));
                assertTrue(allProjectsRemoved.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/address"));
                assertTrue(allProjectsRemoved.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/responsibilities"));
                assertTrue(allProjectsRemoved.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/projects"));
                assertTrue(allProjectsRemoved.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/managedEmployees"));
                assertTrue(allProjectsRemoved.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/phoneNumbers"));
                // there must be no project associated with this employee
                assertTrue(allProjectsRemoved.contains("projects\":[]"));
            } else {
                // remove projects one by one
                // Disassociate large project from the employee first
                String largeProjectDisassociated = RestUtils.restRemoveBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class, "projects", mediaType, "teamLeader", String.valueOf(largeProject.getId()));
                // verify relationships
                assertTrue(largeProjectDisassociated.contains("\"firstName\":\"Charles\",\"gender\":\"Male\",\"id\":" + employee.getId() + ",\"lastName\":\"Mingus\",\"responsibilities\":[],\"salary\":0.0"));
                assertTrue(largeProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/manager"));
                assertTrue(largeProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/address"));
                assertTrue(largeProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/responsibilities"));
                assertTrue(largeProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/projects"));
                assertTrue(largeProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/managedEmployees"));
                assertTrue(largeProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/phoneNumbers"));
                //the employee should have only small project after this
                assertTrue(largeProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/SmallProject/" + smallProject.getId()));

                // Disassociate small project from the employe
                String smallProjectDisassociated = RestUtils.restRemoveBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class, "projects", mediaType, "teamLeader", String.valueOf(smallProject.getId()));
                // verify relationships
                assertTrue(smallProjectDisassociated.contains("\"firstName\":\"Charles\",\"gender\":\"Male\",\"id\":" + employee.getId() + ",\"lastName\":\"Mingus\",\"responsibilities\":[],\"salary\":0.0"));
                assertTrue(smallProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/manager"));
                assertTrue(smallProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/address"));
                assertTrue(smallProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/responsibilities"));
                assertTrue(smallProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/projects"));
                assertTrue(smallProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/managedEmployees"));
                assertTrue(smallProjectDisassociated.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/phoneNumbers"));
                // there must be no project associated with this employee
                assertTrue(smallProjectDisassociated.contains("projects\":[]"));
            }
        } else if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            if (removeAllProjects) {
                // remove all projects
                String allProjectsRemoved = RestUtils.restRemoveBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class, "projects", mediaType, "teamLeader", null);
                // verify relationships
                assertTrue(allProjectsRemoved.contains("<employee><firstName>Charles</firstName><gender>Male</gender><id>" + employee.getId() + "</id><lastName>Mingus</lastName><salary>0.0</salary>"));
                assertTrue(allProjectsRemoved.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/manager"));
                assertTrue(allProjectsRemoved.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/address"));
                assertTrue(allProjectsRemoved.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/responsibilities"));
                assertTrue(allProjectsRemoved.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/projects"));
                assertTrue(allProjectsRemoved.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/managedEmployees"));
                assertTrue(allProjectsRemoved.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/phoneNumbers"));
                // there must be no project associated with this employee
                assertTrue(!allProjectsRemoved.contains("href=\"" + getServerURI() + "/entity/SmallProject/" + smallProject.getId() + "\""));
                assertTrue(!allProjectsRemoved.contains("href=\"" + getServerURI() + "/entity/LargeProject/" + largeProject.getId() + "\""));
            } else {
                // Disassociate large project from the employee
                String largeProjectDisassociated = RestUtils.restRemoveBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class, "projects", mediaType, "teamLeader", String.valueOf(largeProject.getId()));
                assertTrue(largeProjectDisassociated.contains("<employee><firstName>Charles</firstName><gender>Male</gender><id>" + employee.getId() + "</id><lastName>Mingus</lastName><salary>0.0</salary>"));
                assertTrue(largeProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/manager"));
                assertTrue(largeProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/address"));
                assertTrue(largeProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/responsibilities"));
                assertTrue(largeProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/projects"));
                assertTrue(largeProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/managedEmployees"));
                assertTrue(largeProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/phoneNumbers"));
                // the employee should have only small project after this
                assertTrue(largeProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/SmallProject/" + smallProject.getId() + "\""));
                assertTrue(!largeProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/LargeProject/" + largeProject.getId() + "\""));

                // Disassociate small project from the employee
                String smallProjectDisassociated = RestUtils.restRemoveBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class, "projects",
                        mediaType, "teamLeader", String.valueOf(smallProject.getId()));
                assertTrue(smallProjectDisassociated.contains("<employee><firstName>Charles</firstName><gender>Male</gender><id>" + employee.getId() + "</id><lastName>Mingus</lastName><salary>0.0</salary>"));
                assertTrue(smallProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/manager"));
                assertTrue(smallProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/address"));
                assertTrue(smallProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/responsibilities"));
                assertTrue(smallProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/projects"));
                assertTrue(smallProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/managedEmployees"));
                assertTrue(smallProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId() + "/phoneNumbers"));
                // there must be no project associated with this employee
                assertTrue(!smallProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/SmallProject/" + smallProject.getId() + "\""));
                assertTrue(!smallProjectDisassociated.contains("href=\"" + getServerURI() + "/entity/LargeProject/" + largeProject.getId() + "\""));
            }
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }
        // delete projects
        RestUtils.restDelete(context, largeProject.getId(), LargeProject.class, mediaType);
        RestUtils.restDelete(context, smallProject.getId(), SmallProject.class, mediaType);

        // delete employee
        RestUtils.restDelete(context, employee.getId(), Employee.class, mediaType);
    }

    protected void updateEmployeeWithManager(MediaType mediaType) throws Exception {
        // create an employee
        Employee employee = new Employee();
        employee.setId(90888);
        employee.setFirstName("Miles");
        employee.setLastName("Davis");
        employee.setGender(Gender.Male);

        employee = RestUtils.restCreate(context, employee, Employee.class, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        // create a manager
        Employee manager = new Employee();
        manager.setId(11910);
        manager.setFirstName("Charlie");
        manager.setLastName("Parker");
        manager.setGender(Gender.Male);

        manager = RestUtils.restCreate(context, manager, Employee.class, null, mediaType, true);
        assertNotNull("Employee manager create failed.", manager);

        // update employee with manager
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class,
                "manager", manager, mediaType, "managedEmployees", true);

        // read manager and verify that the relationship is set correctly
        manager = RestUtils.restReadObject(context, manager.getId(), Employee.class, mediaType);
        assertNotNull("Manager read failed.", manager);
        assertNotNull("Manager's managed employee list is null", manager.getManagedEmployees());
        assertTrue("Manager's managed employee list is empty", manager.getManagedEmployees().size() > 0);

        employee = RestUtils.restReadObject(context, employee.getId(), Employee.class, mediaType);
        assertNotNull("Manager read failed.", employee);

        for (Employee emp : manager.getManagedEmployees()) {
            assertNotNull("Managed employee's first name is null", emp.getFirstName());
            assertNotNull("Managed employee's last name is null", emp.getLastName());
        }

        // delete employee
        RestUtils.restDelete(context, employee.getId(), Employee.class, mediaType);

        // delete manager
        RestUtils.restDelete(context, manager.getId(), Employee.class, mediaType);
    }

    protected void createEmployeeWithPhoneNumbers(String filename) throws Exception {
        String msg = RestUtils.getJSONMessage(filename);
        Employee employee = RestUtils.restCreateStr(context, msg, Employee.class, MediaType.APPLICATION_JSON_TYPE);
        assertNotNull(employee);
        assertTrue(employee.getId() == 743627);
        List<PhoneNumber> phoneNumbers = employee.getPhoneNumbers();
        assertNotNull(phoneNumbers);
        assertTrue(phoneNumbers.size() == 1);
        assertTrue("613".equals(phoneNumbers.get(0).getAreaCode()));

        // update employee
        employee.setSalary(20000);
        employee = RestUtils.restUpdate(context, employee, Employee.class);
        assertNotNull(employee);
        assertTrue(employee.getId() == 743627);
        assertTrue(employee.getSalary() == 20000);

        // delete
        RestUtils.restDelete(context, employee.getId(), Employee.class);
    }

    protected void updateEmployeeWithEmploymentPeriod(MediaType mediaType) throws Exception {
        Employee employee = new Employee();
        employee.setId(10234);
        employee.setFirstName("John");
        employee.setLastName("Travolta");
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        Calendar now = GregorianCalendar.getInstance();
        employmentPeriod.setStartDate(now);
        employee.setPeriod(employmentPeriod);

        employee = RestUtils.restCreate(context, employee, Employee.class, mediaType);
        assertNotNull("Employee create failed.", employee);

        Calendar threeYearsLater = GregorianCalendar.getInstance();
        long end = (now.getTimeInMillis() + THREE_YEARS);
        threeYearsLater.setTimeInMillis(end);

        employmentPeriod.setEndDate(threeYearsLater);
        employee.setPeriod(employmentPeriod);

        employee = RestUtils.restUpdate(context, employee, Employee.class, null, mediaType, true);

        assertNotNull("Employee update failed.", employee);
        assertNotNull("Employee's employment period update failed", employee.getPeriod());
        assertNotNull("Employee's employment period end date is null", employee.getPeriod().getEndDate());
        assertTrue("Incorrect end date for employee", employee.getPeriod().getEndDate().getTimeInMillis() == threeYearsLater.getTimeInMillis());

        RestUtils.restDelete(context, 10234, Employee.class, mediaType);
    }

    protected void readEmployee(MediaType mediaType) throws Exception {
        Employee employee = new Employee();
        employee.setId(18234);
        employee.setFirstName("Pat");
        employee.setLastName("Metheny");
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        employmentPeriod.setStartDate(GregorianCalendar.getInstance());
        employee.setPeriod(employmentPeriod);

        Employee employeeCreated = RestUtils.restCreate(context, employee, Employee.class, mediaType);
        Employee employeeRead = RestUtils.restReadObject(context, 18234, Employee.class, mediaType);
        assertNotNull("Employee create failed.", employeeCreated);
        assertNotNull("Employee read failed.", employeeRead);
        assertTrue("Employee created and employee read is different", employeeCreated.getLastName().equals(employeeRead.getLastName()));
        RestUtils.restDelete(context, 18234, Employee.class, mediaType);
        Employee emp = DBUtils.dbRead(18234, Employee.class, context.getEmf().createEntityManager());
        assertNull("Employee could not be deleted", emp);
    }

    protected void getEmployeeAddressMultiResultNamedQueryWithBinaryData(MediaType mediaType) throws Exception {
        EmployeeAddress address = new EmployeeAddress("Newyork City", "USA", "NY", "10005", "Wall Street");
        address.setAreaPicture(RestUtils.convertImageToByteArray("manhattan.png"));

        address = RestUtils.restUpdate(context, address, EmployeeAddress.class, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address);
        assertNotNull("EmployeeAddress area picture is null", address.getAreaPicture());
        assertTrue("Newyork City".equals(address.getCity()));
        assertTrue("USA".equals(address.getCountry()));
        assertTrue("NY".equals(address.getProvince()));
        assertTrue("10005".equals(address.getPostalCode()));
        assertTrue("Wall Street".equals(address.getStreet()));

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", address.getId());

        String expected;
        if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            expected = "<List><item><areaPicture>89504E470D0A1A0A0000000D49484452000000960000009608030000000BDF81D000000300504C5445CFDEE2EFEFEFE6E6E6F7F7F7FFFFFFDEDEDEF7FDFF00C4F163DBF608C6F153D7F6D6F6FDEBFAFD99E7F9F0FCFE30CFF4B6EEFB21CCF310C8F258D8F68BE4F9CFF4FCDFF8FD7DE1F8E6F9FEBCEFFB3BD2F4C3F1FC84E3F84AD5F53AD1F46BDDF717C9F295E6F919CAF272DEF7A8EBFAA6EAFAACECFA29CDF345D0F143D4F586E1F688DAEE6ED9F1E9F4F6A3DDEAD5DDDFA2E0EFD7F1F7CFE6EA0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003A4D766F00000A084944415478DAE55CDB72DC36124537E05896E424BEC8962B4925D9AD4DEDD3FEFF67EC5BDED74E7993527CD9B5654983466F0304392087177088E1A86AC7374933260F4F779FBE002418751F5FA8FEFF60B1E3FB08CB6904E27B070BE48F41473579749F7C4B9B489933D9468503462227D74C00E15BAB8FCE964B2943E0DAAE4786D581A0EF89CBF7E1D07C6C58FD71E703731A9A59CD860987129908C78AC4F113133F70C730228F9B571B3E0A2C65F7F4BEC3C202FD77FDD7713E0D0C51AA0F84EB5C3DFF7CE5F04BBFCE62C5152A8675D97A4C576797784523C1C066D8010F15880F94BAD1833E4DA212167CA65CD7882F7FF7A458EE4F38080247DEA341F53A9011FD612FBF0CDA886D90D5610D390C5B17574A3DFBB722335C5B546EEF59EBD3DD83C042DC28B521D6C31F50105C0B7C65BD96117FB81685B81D3B3685F4132B325E87ADF377DE4E44389A2F9D10A645B798711DB6EEBCBFDF8E6A8F1064A0FA57AF1589AFE4CFAD9A6A731CC44A91578225D1FFF22A47A881068B583C80666DD4C6D2E881EB776120291E80AD679FD5D9ED4411181902BB5EAA7EAFD4899BE87378A21B2A0FEB04FCD938BF855C05163E735E9660499D7F0097D79FBC8DEE264ECC13C88AB3F5689C0CAAD9E0558DF8E87A41CB7D305868265A1CEE68E93AB0BEBEEEAD093451858E72BAB1E2B04ECF07E25F8749A00EB53B9163695D59AF06EBD1DBC6713A641804499500723EF0153C00385A09D6F3FFC42F3EFFDC04A3B5D5D9D957C95220DBD8ED38E933DC3A139B0F8D4F6DD0F9A64B52B250435A7E49996CDB1108C0ABB0F5707B9AB717EAC92FFEAA597E868C5571A52326E629AD2FA9F2E9A48FEFE8C3DFFED002C70FB3424351FDDC77161A08E287B91F5B41542F13177EF149BD70AF31A447636AE726E79082B6196408C2C5FD725F92ACA4E47AAB2E7E3FE1FA0CDAB838C4D102D326F99A078A8972B0427F1F5F1FD55F3EE0C3EBC088AB144BA94AA9F4B62F1413A3EE0FBA72B0126348717AE5CEDEF9830B4FE8ACB5D2B0C6C64B77F8ED9DCE159B9D9E27A32C9182F36F7E0BFD72DD5AB37C237E3ED802756AFA52917802C9555F5CF3DD7FAB09047A6D90FE59831FD4381C8045DAC1218C7879D3AE8999EBB988288131E2EA3CD012065D0171342C0F0BFFECCCDCDCB6C489EB7548235EE9BB6BE6E2B01EA7D5DFE9E6A4C54604686840D6757C23015E06D6451A4E68DFDD0DB4AC0349D0C49F1B5714D6C969EA59CFF0D6F5A991246D6DED6866D6F53A68115897290B2FBF48ABEC867A4343A319A26E3D4AC8D6933709ACEFF0F6B67FC2A789335412C3FA6C09582625EBDDDDDDD0CC544F773CFE681E5701239E7E4CBEF9FEC68475802557E98AA83C2555E64FD7FC59F1C2634A622800EB6C93B8F5A7EBE1B1777ED68702464C62EBFBF73765DCB500ACAD139FBE16C1A412475C0E0BB7527AEE7CCE655B00D8625C8983DFC4A4AC1C2E0CA2E56C9D5E345FBEF2C51370F6DE90E1A1DC7258275B67BAAD0B19B0B488AC02B0DCD6B7CEAAC249F29E46B784AC02B03E5CF4E8342BD8CFF1C93A5FD62F6F319215547DD7F8BFD5FB683DD515D762B64E7E4C6AD4B4A0813DDCAB99E12C86F5EA5FA9369B4463F7D81458AE3A4DDAC3EFFE6C08B26ADE7E2DD519E12C8695E46976EDC32E08C6C5B09E069EE0DBC75F7DFD9A931E8BF7F1AE866053C2883FBDB1EF157D4AE65BD2AE4AA383A48FC59678D1F99BC7FF6046D35ECAD7928590D687F530FCFDC7573F6E6E9F86D31BD889AB7D370F1799D83CBDFC550DCC62EA5295F202135C41973FFB75C0145CCDDB94659C67CFBD613D7D52F5141F5FA8E7DF8C0F3D2A9FCBB2272DF6AD709A27BF79A1FFE79859AC8824BB99BEBF7775FA25D4333F7C78F596AE06DD46AA54E7A7B8AC1920A736AC87820B5DFE21DCB01B3E4425FB1CB66A65356A0E0A45E2F8C9C200D2449B433EACC591387E5948359979CE05A574CBE5E61770D3566CB69A2CEFC772636C1335AC800DB2948FB26BABFC5454606023B19F83CACC91AEC5B0B4562E776B3EAE6844073AF373C18854AD63F7FF9FA6625BCC16E5D6EC90ACE56B7DB09C5837E65A1DE0B5749E988F0AEADA7ED0F5A5A1D67BA5EA70AF5A9A436C866FD61AE9D7817D8264D74F864BFA73332030B003C85FB0BF8874E58673AA02DDD4D0E1E31E92EBDB320213453319D216EB6DE5612D3EB515D5254AF69441A24F7EFB797B731D3B89885A14F61851722BFA8187DF282024697109D35AEBABAD923DF393B6086D386DD2DF76FE779BBF5D58CD75208AB511C3C6E86E71372F5ED0F31AFC2A54AAD57501B78089DBE28811A72D53933563524450A1F24BBF8C8DBD124B52FBBE0C9C8D6A77BC32D52FC8E9A1DAC06C21161C959AEBEDBE0D6CBB3CCE3DD736B226B5216E268B61EC9DC1A745E7977FE3FA1CC08E34EC2FA7D9F589AE2A2C096314C163E79C84631009EB8392FCDD0F429F83C9CE27C788D1B972340B6D5C180F6E2EFD0FFA88F3DEED34A1381BF8250B2E927CE6544E467B0351E51F1476EFDB6A13BFDFB1E1D9DCF40DCBCDFE30674C89206829806A26D00216037FA28B3DF7C3A29AEFF3F15A325CCCFBBCFF98F1A6D3E08DE765407BA120174F67332A0876D94EAFA70542FB9CE25BEB2A8B493E433431AB61542E368698A93D0ED8AFB0C1ACD968BCD1A2BE52082E061B0EFCA0645EB248E0ACE436015B628F4D88AFA9850AC1414CB6A9842D40A83F5C147E2D1509EE16CC7DB07261BAEA486E68CD29746936AC93402500DEB98DC35839128894016D9233A25A94133BFE8EFD3796136FA33C7C42842A8ABA50CD4652A3F8BD968F3545132F8CC456F438B4FD296A3BFF4E37F6B0F024D613C9107FF241DA9BC3BA6C6592D5CE173DC3055FF641B321A259CBF194A970A346F76E0DBFF56C584E394B293B76D3BD25BDD9B2B8D10D3E5D157C694DB35B057661719672E74C69E4CAB6F3BFE40BE7CB058E3C0A450F7A6BDC8E112B1B4E90B5EBE33D56B4A18946A82EA165F4785563B7A29BF9E9AEE74D487B6BAA3A270643D849512218E4F7C40648635EDC61CB8BDC44FBB7731796AF0BA03589A0AAC0F3E042CF14575D9AA5836957E9C002624894A47756EA46FABB5D4313D4B68AB6B39851A8756039EF8B63D38E9E3BD65A0140D141776E5EE68AA2CC877AB46171251EC3B87AEFA30B67ACF0C078F0DA07994B9F6D58CD850FF8FCAE05B38698AE728B6C505D5830118BFBACC341B69F0F550CBCDB75ED912FB73481A32858D208CE9A83B5D81A1F9C13CCD8D1932EB94C3EC263444E451626F6C5CC4145CD304530ED516AD66CCD5861C876A83D58EAB0358D6AD64E319F64F8C1820168608BA7A3C4CEF2585EBCB1CC4C5025D948CF3360899799182610CEA66A0D585CDDDDB1F60BA7DFE7233C960E47DB50EB3400A8E3C01AA48CB4B42CF668B006D4DD6276D3711058FDF5E571008DB51873EB90C3C0E21D4C78E487427A58DD6D1FEEF80FAAC4DD25C1A3845E77B082367DE8A0EB5F533B8211536E8C53F780AA60449D8ABABA272F4C968E49DF17547E71C59BCDAAFE87021D8F2D3F4BF40B4400F707D5411F2CBAE0F53F4891BE36E04AC8330000000049454E44AE426082</areaPicture></item></List>";
        } else if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            expected = "[{\"areaPicture\":\"89504E470D0A1A0A0000000D49484452000000960000009608030000000BDF81D000000300504C5445CFDEE2EFEFEFE6E6E6F7F7F7FFFFFFDEDEDEF7FDFF00C4F163DBF608C6F153D7F6D6F6FDEBFAFD99E7F9F0FCFE30CFF4B6EEFB21CCF310C8F258D8F68BE4F9CFF4FCDFF8FD7DE1F8E6F9FEBCEFFB3BD2F4C3F1FC84E3F84AD5F53AD1F46BDDF717C9F295E6F919CAF272DEF7A8EBFAA6EAFAACECFA29CDF345D0F143D4F586E1F688DAEE6ED9F1E9F4F6A3DDEAD5DDDFA2E0EFD7F1F7CFE6EA0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003A4D766F00000A084944415478DAE55CDB72DC36124537E05896E424BEC8962B4925D9AD4DEDD3FEFF67EC5BDED74E7993527CD9B5654983466F0304392087177088E1A86AC7374933260F4F779FBE002418751F5FA8FEFF60B1E3FB08CB6904E27B070BE48F41473579749F7C4B9B489933D9468503462227D74C00E15BAB8FCE964B2943E0DAAE4786D581A0EF89CBF7E1D07C6C58FD71E703731A9A59CD860987129908C78AC4F113133F70C730228F9B571B3E0A2C65F7F4BEC3C202FD77FDD7713E0D0C51AA0F84EB5C3DFF7CE5F04BBFCE62C5152A8675D97A4C576797784523C1C066D8010F15880F94BAD1833E4DA212167CA65CD7882F7FF7A458EE4F38080247DEA341F53A9011FD612FBF0CDA886D90D5610D390C5B17574A3DFBB722335C5B546EEF59EBD3DD83C042DC28B521D6C31F50105C0B7C65BD96117FB81685B81D3B3685F4132B325E87ADF377DE4E44389A2F9D10A645B798711DB6EEBCBFDF8E6A8F1064A0FA57AF1589AFE4CFAD9A6A731CC44A91578225D1FFF22A47A881068B583C80666DD4C6D2E881EB776120291E80AD679FD5D9ED4411181902BB5EAA7EAFD4899BE87378A21B2A0FEB04FCD938BF855C05163E735E9660499D7F0097D79FBC8DEE264ECC13C88AB3F5689C0CAAD9E0558DF8E87A41CB7D305868265A1CEE68E93AB0BEBEEEAD093451858E72BAB1E2B04ECF07E25F8749A00EB53B9163695D59AF06EBD1DBC6713A641804499500723EF0153C00385A09D6F3FFC42F3EFFDC04A3B5D5D9D957C95220DBD8ED38E933DC3A139B0F8D4F6DD0F9A64B52B250435A7E49996CDB1108C0ABB0F5707B9AB717EAC92FFEAA597E868C5571A52326E629AD2FA9F2E9A48FEFE8C3DFFED002C70FB3424351FDDC77161A08E287B91F5B41542F13177EF149BD70AF31A447636AE726E79082B6196408C2C5FD725F92ACA4E47AAB2E7E3FE1FA0CDAB838C4D102D326F99A078A8972B0427F1F5F1FD55F3EE0C3EBC088AB144BA94AA9F4B62F1413A3EE0FBA72B0126348717AE5CEDEF9830B4FE8ACB5D2B0C6C64B77F8ED9DCE159B9D9E27A32C9182F36F7E0BFD72DD5AB37C237E3ED802756AFA52917802C9555F5CF3DD7FAB09047A6D90FE59831FD4381C8045DAC1218C7879D3AE8999EBB988288131E2EA3CD012065D0171342C0F0BFFECCCDCDCB6C489EB7548235EE9BB6BE6E2B01EA7D5DFE9E6A4C54604686840D6757C23015E06D6451A4E68DFDD0DB4AC0349D0C49F1B5714D6C969EA59CFF0D6F5A991246D6DED6866D6F53A68115897290B2FBF48ABEC867A4343A319A26E3D4AC8D6933709ACEFF0F6B67FC2A789335412C3FA6C09582625EBDDDDDDD0CC544F773CFE681E5701239E7E4CBEF9FEC68475802557E98AA83C2555E64FD7FC59F1C2634A622800EB6C93B8F5A7EBE1B1777ED68702464C62EBFBF73765DCB500ACAD139FBE16C1A412475C0E0BB7527AEE7CCE655B00D8625C8983DFC4A4AC1C2E0CA2E56C9D5E345FBEF2C51370F6DE90E1A1DC7258275B67BAAD0B19B0B488AC02B0DCD6B7CEAAC249F29E46B784AC02B03E5CF4E8342BD8CFF1C93A5FD62F6F319215547DD7F8BFD5FB683DD515D762B64E7E4C6AD4B4A0813DDCAB99E12C86F5EA5FA9369B4463F7D81458AE3A4DDAC3EFFE6C08B26ADE7E2DD519E12C8695E46976EDC32E08C6C5B09E069EE0DBC75F7DFD9A931E8BF7F1AE866053C2883FBDB1EF157D4AE65BD2AE4AA383A48FC59678D1F99BC7FF6046D35ECAD7928590D687F530FCFDC7573F6E6E9F86D31BD889AB7D370F1799D83CBDFC550DCC62EA5295F202135C41973FFB75C0145CCDDB94659C67CFBD613D7D52F5141F5FA8E7DF8C0F3D2A9FCBB2272DF6AD709A27BF79A1FFE79859AC8824BB99BEBF7775FA25D4333F7C78F596AE06DD46AA54E7A7B8AC1920A736AC87820B5DFE21DCB01B3E4425FB1CB66A65356A0E0A45E2F8C9C200D2449B433EACC591387E5948359979CE05A574CBE5E61770D3566CB69A2CEFC772636C1335AC800DB2948FB26BABFC5454606023B19F83CACC91AEC5B0B4562E776B3EAE6844073AF373C18854AD63F7FF9FA6625BCC16E5D6EC90ACE56B7DB09C5837E65A1DE0B5749E988F0AEADA7ED0F5A5A1D67BA5EA70AF5A9A436C866FD61AE9D7817D8264D74F864BFA73332030B003C85FB0BF8874E58673AA02DDD4D0E1E31E92EBDB320213453319D216EB6DE5612D3EB515D5254AF69441A24F7EFB797B731D3B89885A14F61851722BFA8187DF282024697109D35AEBABAD923DF393B6086D386DD2DF76FE779BBF5D58CD75208AB511C3C6E86E71372F5ED0F31AFC2A54AAD57501B78089DBE28811A72D53933563524450A1F24BBF8C8DBD124B52FBBE0C9C8D6A77BC32D52FC8E9A1DAC06C21161C959AEBEDBE0D6CBB3CCE3DD736B226B5216E268B61EC9DC1A745E7977FE3FA1CC08E34EC2FA7D9F589AE2A2C096314C163E79C84631009EB8392FCDD0F429F83C9CE27C788D1B972340B6D5C180F6E2EFD0FFA88F3DEED34A1381BF8250B2E927CE6544E467B0351E51F1476EFDB6A13BFDFB1E1D9DCF40DCBCDFE30674C89206829806A26D00216037FA28B3DF7C3A29AEFF3F15A325CCCFBBCFF98F1A6D3E08DE765407BA120174F67332A0876D94EAFA70542FB9CE25BEB2A8B493E433431AB61542E368698A93D0ED8AFB0C1ACD968BCD1A2BE52082E061B0EFCA0645EB248E0ACE436015B628F4D88AFA9850AC1414CB6A9842D40A83F5C147E2D1509EE16CC7DB07261BAEA486E68CD29746936AC93402500DEB98DC35839128894016D9233A25A94133BFE8EFD3796136FA33C7C42842A8ABA50CD4652A3F8BD968F3545132F8CC456F438B4FD296A3BFF4E37F6B0F024D613C9107FF241DA9BC3BA6C6592D5CE173DC3055FF641B321A259CBF194A970A346F76E0DBFF56C584E394B293B76D3BD25BDD9B2B8D10D3E5D157C694DB35B057661719672E74C69E4CAB6F3BFE40BE7CB058E3C0A450F7A6BDC8E112B1B4E90B5EBE33D56B4A18946A82EA165F4785563B7A29BF9E9AEE74D487B6BAA3A270643D849512218E4F7C40648635EDC61CB8BDC44FBB7731796AF0BA03589A0AAC0F3E042CF14575D9AA5836957E9C002624894A47756EA46FABB5D4313D4B68AB6B39851A8756039EF8B63D38E9E3BD65A0140D141776E5EE68AA2CC877AB46171251EC3B87AEFA30B67ACF0C078F0DA07994B9F6D58CD850FF8FCAE05B38698AE728B6C505D5830118BFBACC341B69F0F550CBCDB75ED912FB73481A32858D208CE9A83B5D81A1F9C13CCD8D1932EB94C3EC263444E451626F6C5CC4145CD304530ED516AD66CCD5861C876A83D58EAB0358D6AD64E319F64F8C1820168608BA7A3C4CEF2585EBCB1CC4C5025D948CF3360899799182610CEA66A0D585CDDDDB1F60BA7DFE7233C960E47DB50EB3400A8E3C01AA48CB4B42CF668B006D4DD6276D3711058FDF5E571008DB51873EB90C3C0E21D4C78E487427A58DD6D1FEEF80FAAC4DD25C1A3845E77B082367DE8A0EB5F533B8211536E8C53F780AA60449D8ABABA272F4C968E49DF17547E71C59BCDAAFE87021D8F2D3F4BF40B4400F707D5411F2CBAE0F53F4891BE36E04AC8330000000049454E44AE426082\"}]";
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }
        // query
        String result = RestUtils.restNamedMultiResultQueryResult(context, "EmployeeAddress.getPicture", parameters, null, mediaType);
        assertNotNull(result);
        assertTrue(result.contains(expected));

        // delete employee address
        RestUtils.restDelete(context, address.getId(), EmployeeAddress.class, mediaType);
    }

    protected void readEmployeeResponsibilities(MediaType mediaType) throws Exception {
        // create an employee
        Employee employee = new Employee();
        employee.setId(921025);
        employee.setFirstName("Johannes");
        employee.setLastName("Brahms");

        employee.addResponsibility("musician");
        employee.addResponsibility("architect");
        employee.addResponsibility("conductor");

        employee = RestUtils.restCreate(context, employee, Employee.class, mediaType);
        assertNotNull("Employee create failed.", employee);

        employee = RestUtils.restReadObject(context, employee.getId(), Employee.class, mediaType);
        assertNotNull(employee.getResponsibilities());
        assertTrue(employee.getResponsibilities().size() == 3);
        List<String> responsibilities = employee.getResponsibilities();
        assertTrue(responsibilities.contains("musician"));
        assertTrue(responsibilities.contains("architect"));
        assertTrue(responsibilities.contains("conductor"));

        // read responsibilities
        String result = RestUtils.restFindAttribute(context, employee.getId(), Employee.class, "responsibilities", mediaType);
        assertNotNull(result);

        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            String expected = "{\"responsibilities\":[\"musician\",\"architect\",\"conductor\"]}";
            assertTrue(result.contains(expected));
        } else if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            String expected = "<List><responsibilities>musician</responsibilities><responsibilities>architect</responsibilities><responsibilities>conductor</responsibilities></List>";
            assertTrue(result.contains(expected));
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }

        // delete employee
        RestUtils.restDelete(context, employee.getId(), Employee.class, mediaType);
    }

    protected void executeSingleResultQueryGetEmployeeWithDomainObject(MediaType mediaType) throws Exception {
        // create an employee
        Employee employee = new Employee();
        employee.setFirstName("Miles");
        employee.setLastName("Davis");
        employee.setGender(Gender.Male);

        employee = RestUtils.restUpdate(context, employee, Employee.class, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        // create a manager
        Employee manager = new Employee();
        manager.setFirstName("Charlie");
        manager.setLastName("Parker");
        manager.setGender(Gender.Male);

        manager = RestUtils.restUpdate(context, manager, Employee.class, null, mediaType, true);
        assertNotNull("Employee manager create failed.", manager);

        // update employee with manager
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class,
                "manager", manager, mediaType, "managedEmployees", true);

        // read manager and verify that the relationship is set correctly
        manager = RestUtils.restReadObject(context, manager.getId(), Employee.class, mediaType);
        assertNotNull("Manager read failed.", manager);
        assertNotNull("Manager's managed employee list is null", manager.getManagedEmployees());
        assertTrue("Manager's managed employee list is empty", manager.getManagedEmployees().size() > 0);

        employee = RestUtils.restReadObject(context, employee.getId(), Employee.class, mediaType);
        assertNotNull("Manager read failed.", employee);

        for (Employee emp : manager.getManagedEmployees()) {
            assertNotNull("Managed employee's first name is null", emp.getFirstName());
            assertNotNull("Managed employee's last name is null", emp.getLastName());
        }

        // single result query
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", employee.getId());

        String queryResult = RestUtils.restNamedSingleResultQueryResult(context, "Employee.getManagerById", parameters, null, mediaType);
        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            assertTrue(queryResult.contains("{\"firstName\":\"Miles\",\"lastName\":\"Davis\",\"manager\":"));
            assertTrue(queryResult.contains("managedEmployees\":[{"));
            assertTrue(queryResult.contains("href\":\"" + getServerURI() + "/entity/Employee/" + employee.getId()));
        } else if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            assertTrue(queryResult.contains("<item><firstName>Miles</firstName><lastName>Davis</lastName><manager"));
            assertTrue(queryResult.contains("<managedEmployees><_link"));
            assertTrue(queryResult.contains("href=\"" + getServerURI() + "/entity/Employee/" + employee.getId()));
            assertTrue(queryResult.contains("</managedEmployees>"));
            assertTrue(queryResult.contains("</item>"));
        } else {
            // unsupported media type
            throw new RestCallFailedException(Response.Status.BAD_REQUEST);
        }

        // delete employee
        RestUtils.restDelete(context, employee.getId(), Employee.class, mediaType);

        // delete manager
        RestUtils.restDelete(context, manager.getId(), Employee.class, mediaType);
    }

    protected void readEmployeeWithCertifications(MediaType mediaType) throws Exception {
        // create an employee
        Employee employee = new Employee();
        employee.setId(201204);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        List<Certification> certifications = new ArrayList<>();

        Certification certification = new Certification();
        certification.setName("Java");
        certification.setIssueDate(GregorianCalendar.getInstance());
        certifications.add(certification);

        employee.setCertifications(certifications);

        employee = RestUtils.restCreate(context, employee, Employee.class, mediaType);
        assertNotNull("Employee create failed.", employee);

        employee = RestUtils.restReadObject(context, 201204, Employee.class, mediaType);
        assertNotNull(employee.getCertifications());
        assertTrue(employee.getCertifications().size() == 1);
        assertTrue("Java".equals(employee.getCertifications().get(0).getName()));

        // delete employee
        RestUtils.restDelete(context, 201204, Employee.class, mediaType);
    }
}
