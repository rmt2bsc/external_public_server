package org.rmt2.soap.projecttracker.timesheet;

import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ClientType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.EmployeeType;
import org.rmt2.jaxb.EmployeetypeType;
import org.rmt2.jaxb.EventType;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.PersonType;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ProjectTaskType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.TimesheetStatusType;
import org.rmt2.jaxb.TimesheetType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.ReplyStatusTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerTypeBuilder;
import org.rmt2.util.addressbook.PersonTypeBuilder;
import org.rmt2.util.projecttracker.admin.ClientTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeeTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeetypeTypeBuilder;
import org.rmt2.util.projecttracker.timesheet.EventTypeBuilder;
import org.rmt2.util.projecttracker.timesheet.ProjectTaskTypeBuilder;
import org.rmt2.util.projecttracker.timesheet.TimesheetStatusTypeBuilder;
import org.rmt2.util.projecttracker.timesheet.TimesheetTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.util.RMT2Date;
import com.api.xml.jaxb.JaxbUtil;

public class TimesheetUpdateSoapRequestBuilderTest {

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
                .withModule("timesheet")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())
                
                // Set these header elements with dummy values in order to be properly assigned later.
                .withTransaction(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE)
                .withRouting(ApiTransactionCodes.ROUTE_PROJECTTRACKER)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();
        
        CustomerType customer = CustomerTypeBuilder.Builder.create()
                .withAccountNo("111-222-333")
                .build();

        ClientType client = ClientTypeBuilder.Builder.create()
                .withClientId(200)
                .withClientName("Client Name")
                .withCustomerData(customer)
                .build();

        PersonType pt = PersonTypeBuilder.Builder.create()
                .withFirstName("roy")
                .withLastName("terrell")
                .withShortName("roy terrell")
                .build();

        EmployeetypeType ett = EmployeetypeTypeBuilder.Builder.create()
                .withEmployeeTypeId(3)
                .build();

        EmployeeType et = EmployeeTypeBuilder.Builder.create()
                .withEmployeeId(200)
                .withEmployeeType(ett)
                .withManagerId(0)
                .withContactDetails(pt)
                .build();

        TimesheetStatusType tsst = TimesheetStatusTypeBuilder.Builder.create()
                .withStatusId(6)
                .withName("Invoice")
                .withDescription("A sales order has been created and invoiced for the timesheet.")
                .build();

        TimesheetType main = TimesheetTypeBuilder.Builder.create()
                .withTimesheetId(169)
                .withProjectId(30)
                .withDisplayTimesheetId("0000000169")
                .withBeginPeriod("2020-01-01")
                .withEndPeriod("2020-02-01")
                .withInvoiceRefNo("943294393")
                .withExternalRefNo("432-3923-43022")
                .withComments("Monday: Test comment")
                .withDocumentId(65948)
                .withHourlyOvertimeRate(120.00)
                .withHourlyRate(100.00)
                .withStatusHistoryId(333)
                .withStatusEffectiveDate("2020-01-01")
                .withStatusEndDate(new Date())
                .withEmployeeBillableHours(40)
                .withEmployeeNonBillableHours(0)
                .withClient(client)
                .withEmployee(et)
                .withStatus(tsst)
                .addTimeEntry(this.createProjectTaskType(1, 2, "About Objects", 10, "Analysis and Design"))
                .addTimeEntry(this.createProjectTaskType(2, 2, "About Objects", 20, "Development"))
                .addTimeEntry(this.createProjectTaskType(3, 2, "About Objects", 30, "Testing"))
                .addTimeEntry(this.createProjectTaskType(4, 2, "About Objects", 40, "Meetings"))
                .build();

        ProjectDetailGroup profile = fact.createProjectDetailGroup();
        profile.getTimesheet().add(main);

        req.setProfile(profile);
        req.setHeader(head);
        
        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

    @Test
    public void testBuildResponse() {
        ObjectFactory fact = new ObjectFactory();
        ProjectProfileResponse resp = fact.createProjectProfileResponse();

        HeaderType head = HeaderTypeBuilder.Builder.create()
                .withApplication("projecttracker")
                .withModule("timesheet")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE)
                .withRouting(ApiTransactionCodes.ROUTE_PROJECTTRACKER)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        ReplyStatusType rst = ReplyStatusTypeBuilder.Builder.create()
                .withStatus(MessagingConstants.RETURN_STATUS_SUCCESS)
                .withReturnCode(MessagingConstants.RETURN_CODE_SUCCESS)
                .withMessage("timesheet updated successfully")
                .withRecordCount(1)
                .build();

        RecordTrackingType tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(RMT2Date.stringToDate("2020-05-01"))
                .withDateUpdate(RMT2Date.stringToDate("2020-05-01"))
                .withUserId("testuser")
                .build();

        CustomerType customer = CustomerTypeBuilder.Builder.create()
                .withAccountNo("111-222-333")
                .build();

        ClientType client = ClientTypeBuilder.Builder.create()
                .withClientId(200)
                .withClientName("Client Name")
                .withCustomerData(customer)
                .build();

        PersonType pt = PersonTypeBuilder.Builder.create()
                .withFirstName("roy")
                .withLastName("terrell")
                .withShortName("roy terrell")
                .build();

        EmployeetypeType ett = EmployeetypeTypeBuilder.Builder.create()
                .withEmployeeTypeId(3)
                .build();

        EmployeeType et = EmployeeTypeBuilder.Builder.create()
                .withEmployeeId(200)
                .withEmployeeType(ett)
                .withManagerId(2001)
                .withContactDetails(pt)
                .build();

        TimesheetStatusType tsst = TimesheetStatusTypeBuilder.Builder.create()
                .withName("Draft")
                .build();

        TimesheetType main = TimesheetTypeBuilder.Builder.create()
                .withTimesheetId(169)
                .withProjectId(30)
                .withDisplayTimesheetId("0000000169")
                .withBeginPeriod("2020-01-01")
                .withEndPeriod("2020-02-01")
                .withEmployeeBillableHours(40)
                .withEmployeeNonBillableHours(0)
                .withClient(client)
                .withEmployee(et)
                .withStatus(tsst)

                // Don't forget that timesheet status history can be loaded in
                // this process...
                .withRecordTracking(tracking)
                .build();

        ProjectDetailGroup profile = fact.createProjectDetailGroup();
        profile.getTimesheet().add(main);

        resp.setProfile(profile);
        resp.setHeader(head);
        resp.setReplyStatus(rst);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(resp);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

    private ProjectTaskType createProjectTaskType(int projTaskId, int projId, String projName, int taskId, String taskName) {

        ProjectTaskType ptt = ProjectTaskTypeBuilder.Builder.create()
                .withProjectTaskId(projTaskId)
                .withTaskId(taskId)
                .withTaskName(taskName)
                .withProjectId(projId)
                .withProjectName(projName)
                .addHours(this.createWorkEvent(1, RMT2Date.stringToDate("2020-01-01"), 2))
                .addHours(this.createWorkEvent(2, RMT2Date.stringToDate("2020-01-02"), 2))
                .addHours(this.createWorkEvent(3, RMT2Date.stringToDate("2020-01-03"), 2))
                .addHours(this.createWorkEvent(4, RMT2Date.stringToDate("2020-01-04"), 2))
                .addHours(this.createWorkEvent(5, RMT2Date.stringToDate("2020-01-05"), 2))
                .build();

        return ptt;
    }

    private EventType createWorkEvent(int eventId, Date workDate, int hours) {
        EventType evt = EventTypeBuilder.Builder.create()
                .withEventId(eventId)
                .withEventDate(workDate)
                .withHours(hours)
                .build();

        return evt;
    }

}
