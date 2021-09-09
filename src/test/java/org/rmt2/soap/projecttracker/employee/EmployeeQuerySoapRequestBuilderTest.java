package org.rmt2.soap.projecttracker.employee;

import java.math.BigInteger;
import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AddressType;
import org.rmt2.jaxb.CodeDetailType;
import org.rmt2.jaxb.EmployeeCriteriaType;
import org.rmt2.jaxb.EmployeeTitleType;
import org.rmt2.jaxb.EmployeeType;
import org.rmt2.jaxb.EmployeetypeType;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.PersonType;
import org.rmt2.jaxb.ProjectCriteriaGroup;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ZipcodeType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.addressbook.CodeDetailTypeBuilder;
import org.rmt2.util.addressbook.PersonTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeeTitleTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeeTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeetypeTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.util.RMT2Date;
import com.api.xml.jaxb.JaxbUtil;

public class EmployeeQuerySoapRequestBuilderTest {

    private JaxbUtil jaxb;
    
    @Before
    public void setUp() throws Exception {
        try {
            jaxb = SystemConfigurator.getJaxb(ConfigConstants.JAXB_CONTEXNAME_DEFAULT);
        }
        catch (Exception e) {
            jaxb = new JaxbUtil(MessagingConstants.JAXB_RMT2_PKG);
        }
    }
    
    @Test
    public void testBuildRequest() {
        ObjectFactory fact = new ObjectFactory();
        ProjectProfileRequest req = fact.createProjectProfileRequest();
        
        HeaderType head =  HeaderTypeBuilder.Builder.create()
                .withApplication("projecttracker")
                .withModule("employee")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())
                
                // Set these header elements with dummy values in order to be properly assigned later.
                .withTransaction(ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET)
                .withRouting(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();
        
        EmployeeCriteriaType criteria = fact.createEmployeeCriteriaType();
        criteria.setEmployeeId(BigInteger.valueOf(200));
        criteria.setEmployeeTypeId(BigInteger.valueOf(1));
        criteria.setManagerId(BigInteger.valueOf(2));
        criteria.setIsManager(true);
        criteria.setEmployeeTitleId(BigInteger.valueOf(3));
        criteria.setFirstName("Roy");
        criteria.setLastName("Terrell");
        criteria.setCompanyName("XYA Company");
        criteria.setProjectName("Project 1");
        criteria.setLoginName("roy.terrell");
        criteria.setSsn("555-55-5555");

        ProjectCriteriaGroup pcg = fact.createProjectCriteriaGroup();
        pcg.setEmployeeCriteria(criteria);
        req.setCriteria(pcg);
        req.setHeader(head);
        
        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

    @Test
    public void testBuildResponse() {
        ObjectFactory fact = new ObjectFactory();
        ProjectProfileResponse resp = fact.createProjectProfileResponse();

        HeaderType head = HeaderTypeBuilder.Builder.create()
                .withApplication("projecttracker")
                .withModule(ConfigConstants.API_APP_MODULE_VALUE)
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET)
                .withRouting(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        ZipcodeType zip = fact.createZipcodeType();
        zip.setCity("Dallas");
        zip.setState("Tx");
        zip.setZipcode(BigInteger.valueOf(75232));

        AddressType addr = fact.createAddressType();
        addr.setAddrId(BigInteger.valueOf(1000));
        addr.setAddr1("Address Line 1");
        addr.setAddr2("Address Line 2");
        addr.setAddr3("Address Line 3");
        addr.setAddr4("Address Line 4");
        addr.setPhoneMain("2147777777");
        addr.setZip(zip);

        CodeDetailType maritalStatus = CodeDetailTypeBuilder.Builder.create()
                .withCodeId(44)
                .withLongDescription("Married")
                .build();
        CodeDetailType gender = CodeDetailTypeBuilder.Builder.create()
                .withCodeId(55)
                .withLongDescription("Male")
                .build();
        CodeDetailType race = CodeDetailTypeBuilder.Builder.create()
                .withCodeId(66)
                .withLongDescription("African American")
                .build();

        PersonType pt = PersonTypeBuilder.Builder.create()
                .withPersonId(7777)
                .withFirstName("roy")
                .withLastName("terrell")
                .withBirthDate(RMT2Date.stringToDate("1980-05-16"))
                .withEmail("roy.terrell@gte.net")
                .withSocialSecurityNumber("555-55-5555")
                .withAddress(addr)
                .withMaritalStatus(maritalStatus)
                .withGender(gender)
                .withRace(race)
                .build();

        EmployeetypeType ett = EmployeetypeTypeBuilder.Builder.create()
                .withEmployeeTypeId(1000)
                .withDescription("Employee Type Description")
                .build();

        EmployeeTitleType ett2 = EmployeeTitleTypeBuilder.Builder.create()
                .withEmployeeTitleId(3333)
                .withDescription("Employee Title Description")
                .build();

        EmployeeType et = EmployeeTypeBuilder.Builder.create()
                .withEmployeeId(200)
                .withEmployeeType(ett)
                .withManagerFlag(false)
                .withManagerId(0)
                .withEmployeeTitleType(ett2)
                .withLoginId(3333)
                .withLoginName("roy.terrell")
                .withContactDetails(pt)
                .withStartDate(RMT2Date.stringToDate("2000-01-01"))
                .withTermDate(RMT2Date.stringToDate("2020-03-30"))
                .withProjectCount(28)
                .build();

        ProjectDetailGroup profile = fact.createProjectDetailGroup();
        profile.getEmployee().add(et);

        resp.setProfile(profile);
        resp.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(resp);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }
}
