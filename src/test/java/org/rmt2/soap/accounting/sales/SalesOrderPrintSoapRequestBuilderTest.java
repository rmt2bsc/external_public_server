package org.rmt2.soap.accounting.sales;

import java.math.BigInteger;
import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.AddressType;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.CustomerCriteriaType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.InventoryItemType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.ReportAttachmentType;
import org.rmt2.jaxb.SalesInvoiceType;
import org.rmt2.jaxb.SalesOrderCriteria;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.jaxb.TransactionCriteriaGroup;
import org.rmt2.jaxb.TransactionDetailGroup;
import org.rmt2.jaxb.XactBasicCriteriaType;
import org.rmt2.jaxb.XactCriteriaType;
import org.rmt2.jaxb.XactType;
import org.rmt2.jaxb.ZipcodeType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.ReplyStatusTypeBuilder;
import org.rmt2.util.ReportAttachmentTypeBuilder;
import org.rmt2.util.accounting.inventory.InventoryItemTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerTypeBuilder;
import org.rmt2.util.accounting.transaction.XactTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesInvoiceTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesOrderItemTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesOrderTypeBuilder;
import org.rmt2.util.addressbook.AddressTypeBuilder;
import org.rmt2.util.addressbook.BusinessTypeBuilder;
import org.rmt2.util.addressbook.ZipcodeTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.util.RMT2Date;
import com.api.util.RMT2File;
import com.api.xml.jaxb.JaxbUtil;

public class SalesOrderPrintSoapRequestBuilderTest {

    private JaxbUtil jaxb;
    private static final String CUSTOMER_NAME = "ABC Company";
    private static final int CUSTOMER_ID = 111111;
    private static final int SALES_ORDER_ID = 222222;
    private static final int XACT_ID = 333333;
    private static final int INVOICE_ID = 444444;

    @Before
    public void setUp() throws Exception {
        try {
            jaxb = SystemConfigurator.getJaxb(ConfigConstants.JAXB_CONTEXNAME_DEFAULT);
        } catch (Exception e) {
            jaxb = new JaxbUtil(MessagingConstants.JAXB_RMT2_PKG);
        }
    }

    @Test
    public void testBuildRequest() {
        ObjectFactory fact = new ObjectFactory();
        AccountingTransactionRequest req = fact.createAccountingTransactionRequest();

        HeaderType head = HeaderTypeBuilder.Builder.create().withApplication("accounting").withModule("transaction")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT)
                .withRouting(ApiTransactionCodes.ROUTE_ACCOUNTING)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        // Build transaction criteria group
        TransactionCriteriaGroup criteria = fact.createTransactionCriteriaGroup();
        SalesOrderCriteria soCriteria = fact.createSalesOrderCriteria();
        CustomerCriteriaType custCriteria = fact.createCustomerCriteriaType();
        XactCriteriaType xactCriteria = fact.createXactCriteriaType();

        CustomerType custType = fact.createCustomerType();
        custCriteria.setCustomer(custType);
        XactBasicCriteriaType xbct = fact.createXactBasicCriteriaType();
        xactCriteria.setBasicCriteria(xbct);

        criteria.setCustomerCriteria(custCriteria);
        criteria.setSalesCriteria(soCriteria);
        criteria.setXactCriteria(xactCriteria);

        soCriteria.setSalesOrderId(BigInteger.valueOf(SALES_ORDER_ID));
        custType.setCustomerId(BigInteger.valueOf(CUSTOMER_ID));
        xbct.setXactId(BigInteger.valueOf(XACT_ID));

        req.setCriteria(criteria);

        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }


    @Test
    public void testBuildReportDataResponse() {
        ObjectFactory fact = new ObjectFactory();
        AccountingTransactionResponse res = fact.createAccountingTransactionResponse();

        HeaderType head = HeaderTypeBuilder.Builder.create().withApplication("accounting").withModule("transaction")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT)
                .withRouting(ApiTransactionCodes.ROUTE_ACCOUNTING)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        RecordTrackingType tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(RMT2Date.stringToDate("2020-01-01"))
                .withDateUpdate(RMT2Date.stringToDate("2020-01-01"))
                .withUserId("testuser")
                .build();

        InventoryItemType inv1 = InventoryItemTypeBuilder.Builder.create()
                .withItemId(100)
                .withActive(true)
                .withItemName("Dell Computer")
                .withItemSerialNo("COMP-9329393")
                .withMarkup(3)
                .withUnitCost(150.00)
                .withQtyOnHand(10)
                .withVendorItemNo("839KJFDL9329").build();
        SalesOrderItemType item1 = SalesOrderItemTypeBuilder.Builder.create()
                .withSalesOrderItemId(4321)
                .withSalesOrderId(SALES_ORDER_ID)
                .withBackOrderQty(0)
                .withMarkup(3)
                .withUnitCost(450.00)
                .withOrderQty(3)
                .withInventoryItem(inv1)
                .withRecordTracking(tracking)
                .build();
        InventoryItemType inv2 = InventoryItemTypeBuilder.Builder.create()
                .withItemId(100)
                .withActive(true)
                .withItemName("Western Digital 6TB Hard Drive")
                .withItemSerialNo("HD-9843844")
                .withMarkup(3)
                .withUnitCost(45.00)
                .withQtyOnHand(10)
                .withVendorItemNo("8943923833").build();
        SalesOrderItemType item2 = SalesOrderItemTypeBuilder.Builder.create()
                .withSalesOrderItemId(5432)
                .withSalesOrderId(SALES_ORDER_ID)
                .withBackOrderQty(0)
                .withMarkup(3)
                .withUnitCost(135.00)
                .withOrderQty(2)
                .withInventoryItem(inv2)
                .withRecordTracking(tracking)
                .build();

        RecordTrackingType tracking2 = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(RMT2Date.stringToDate("2020-01-01"))
                .withDateUpdate(RMT2Date.stringToDate("2020-02-01"))
                .withUserId("testuser")
                .build();

        XactType xt = XactTypeBuilder.Builder.create()
                .withXactId(XACT_ID)
                .withBankTransInd("Trans_Ind")
                .withConfirmNo("12345BDC93839")
                .withDocumentId(102)
                .withItemTotal(1000.00)
                .withPostedDate("2020-01-01")
                .withReason("Drum Services for gig at Mordern Arts Museum on 1-3-2020")
                .withTenderId(100)
                .withXactAmount(1000.00)
                .withXactDate("2020-01-01")
                .withXactCodeGroupId(1)
                .withXactCodeGroupDescription("Transaction Types")
                .withXactCodeId(4)
                .withXactCodeDescription("CASH RECEIPTS")
                .withXactTypeId(2)
                .withXactTypeDescription("CASH RECEIPTS")
                .build();

        RecordTrackingType tracking3 = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(RMT2Date.stringToDate("2020-01-01"))
                .withDateUpdate(RMT2Date.stringToDate("2020-02-01"))
                .withUserId("testuser")
                .build();

        SalesInvoiceType sit = SalesInvoiceTypeBuilder.Builder.create()
                .withInvoiceId(INVOICE_ID)
                .withInvoiceNo("1297-20200104")
                .withInvoiceDate("2020-01-01")
                .withSalesOrderId(SALES_ORDER_ID)
                .withInvoiceTotal(1620.00)
                .withItemCount(2)
                .withTransaction(xt)
                .withRecordTracking(tracking3)
                .build();

        SalesOrderType so = SalesOrderTypeBuilder.Builder.create()
                .withCustomerAcctNo("U1465-20120901")
                .withInvoiced(false)
                .withSalesOrderId(SALES_ORDER_ID)
                .withCustomerId(CUSTOMER_ID)
                .withCustomerName(CUSTOMER_NAME)
                .withOrderTotal(1620.00)
                .withEffectiveDate(RMT2Date.stringToDate("2020-01-15"))
                .withStatusId(1)
                .withStatusDescription("Quote")
                .withSalesOrderItem(item1)
                .withSalesOrderItem(item2)
                .withSalesInvoiceType(sit)
                .withRecordTracking(tracking2)
                .build();

        ZipcodeType zt1 = ZipcodeTypeBuilder.Builder.create()
                .withCity("Shreveport")
                .withState("LA")
                .withZipcode(71106)
                .build();
        AddressType at1 = AddressTypeBuilder.Builder.create()
                .withAddrId(2001)
                .withAddressLine1("8432 Lucky Ave")
                .withAddressLine2("Building 500")
                .withAddressLine3("Suite 54324")
                .withAddressLine4("P.O Box 34")
                .withZipcode(zt1)
                .build();
        BusinessType custBt = BusinessTypeBuilder.Builder.create()
                .withBusinessId(1343)
                .withContactFirstname("Bob")
                .withContactLastname("Jones")
                .withContactPhone("5555555555")
                .withLongname("Outer Circles Inc")
                .withTaxId("349283921")
                .withWebsite("www.outercircles.com")
                .withContactEmail("bob.jones@gte.net")
                .withAddress(at1)
                .build();

        CustomerType cust = CustomerTypeBuilder.Builder.create()
                .withCustomerId(CUSTOMER_ID)
                .withBusinessType(custBt)
                .withAccountNo("U1465-20120901")
                .build();

        ZipcodeType zt = ZipcodeTypeBuilder.Builder.create()
                .withCity("Dallas")
                .withState("TX")
                .withZipcode(75232)
                .build();
        AddressType at = AddressTypeBuilder.Builder.create()
                .withAddrId(2001)
                .withAddressLine1("3030 Hall Ave")
                .withAddressLine2("Suite 456")
                .withAddressLine3("Room 6")
                .withAddressLine4("PMB 456")
                .withZipcode(zt)
                .build();
        BusinessType company = BusinessTypeBuilder.Builder.create()
                .withBusinessId(1343)
                .withContactFirstname("Roy")
                .withContactLastname("Terrell")
                .withContactPhone("9998887777")
                .withLongname("RMT2 Business Systems Corp.")
                .withTaxId("759987483")
                .withWebsite("www.rmt2.com")
                .withContactEmail("roy.terrell@rmt2.com")
                .withAddress(at)
                .build();

        // Build transaction criteria group
        TransactionDetailGroup profile = fact.createTransactionDetailGroup();
        profile.setCompany(company);
        profile.setSalesOrders(fact.createSalesOrderListType());
        profile.getSalesOrders().getSalesOrder().add(so);
        profile.setCustomers(fact.createCustomerListType());
        profile.getCustomers().getCustomer().add(cust);

        res.setProfile(profile);
        res.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(res);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

    @Test
    public void testBuildFinalResponse() {
        ObjectFactory fact = new ObjectFactory();
        AccountingTransactionResponse res = fact.createAccountingTransactionResponse();

        HeaderType head = HeaderTypeBuilder.Builder.create().withApplication("accounting").withModule("transaction")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT)
                .withRouting(ApiTransactionCodes.ROUTE_ACCOUNTING)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();
        
        ReplyStatusType rst = ReplyStatusTypeBuilder.Builder.create()
                .withStatus("200")
                .withMessage("Sales order(s) printed successfully")
                .withReturnCode(1)
                .withRecordCount(1)
                .build();
        
        SalesInvoiceType sit = SalesInvoiceTypeBuilder.Builder.create()
                .withInvoiceId(INVOICE_ID)
                .withInvoiceNo("1297-20200104")
                .withInvoiceDate("2020-01-01")
                .withSalesOrderId(SALES_ORDER_ID)
                .withInvoiceTotal(1620.00)
                .withItemCount(2)
                .build();
        
        SalesOrderType so = SalesOrderTypeBuilder.Builder.create()
                .withCustomerAcctNo("111-111-111")
                .withInvoiced(false)
                .withSalesOrderId(SALES_ORDER_ID)
                .withCustomerId(CUSTOMER_ID)
                .withCustomerName(CUSTOMER_NAME)
                .withOrderTotal(1000.00)
                .withEffectiveDate(RMT2Date.stringToDate("2020-02-01"))
                .withStatusId(2)
                .withStatusDescription("Invoice")
                .build();

        byte[] pdf = RMT2File.getFileContentsAsBytes("files/1581283879785-SalesOrderInvoiceReport.pdf");
        ReportAttachmentType rat = ReportAttachmentTypeBuilder.Builder.create()
                .withFilePath("/temp/1581441602634-SalesOrderInvoiceReport.pdf")
                .withFileSize(5626)
                .withMimeType("pdf")
                .withReportId("1581441602634-SalesOrderInvoiceReport")
                .withReportContent(pdf)
                .build();

        // Build transaction profile group
        TransactionDetailGroup profile = fact.createTransactionDetailGroup();
        profile.setSalesOrders(fact.createSalesOrderListType());
        profile.getSalesOrders().getSalesOrder().add(so);
        profile.setAttachment(rat);

        res.setProfile(profile);
        res.setHeader(head);
        res.setReplyStatus(rst);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(res);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }
}
