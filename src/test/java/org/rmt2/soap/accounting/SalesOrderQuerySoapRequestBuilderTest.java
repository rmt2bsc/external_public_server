package org.rmt2.soap.accounting;

import java.math.BigDecimal;
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
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.SalesOrderCriteria;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.jaxb.TransactionCriteriaGroup;
import org.rmt2.jaxb.TransactionDetailGroup;
import org.rmt2.jaxb.XactCustomCriteriaTargetType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesOrderItemTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesOrderTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.util.RMT2Date;
import com.api.xml.jaxb.JaxbUtil;

public class SalesOrderQuerySoapRequestBuilderTest {

    private JaxbUtil jaxb;

    @Before
    public void setUp() throws Exception {
        try {
            jaxb = SystemConfigurator.getJaxb(ConfigConstants.JAXB_CONTEXNAME_DEFAULT);
        } catch (Exception e) {
            jaxb = new JaxbUtil(MessagingConstants.JAXB_RMT2_PKG);
        }
    }

    @Test
    public void testBuildRequestSalesOrderFull() {
        ObjectFactory fact = new ObjectFactory();
        AccountingTransactionRequest req = fact.createAccountingTransactionRequest();

        HeaderType head = HeaderTypeBuilder.Builder.create().withApplication("accounting").withModule("transaction")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.ACCOUNTING_SALESORDER_GET)
                .withRouting(ApiTransactionCodes.ROUTE_ACCOUNTING)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        // Build transaction criteria group
        TransactionCriteriaGroup criteria = fact.createTransactionCriteriaGroup();
        SalesOrderCriteria soCriteria = fact.createSalesOrderCriteria();
        criteria.setSalesCriteria(soCriteria);
        soCriteria.setAccountNo("ACCTNO-1234");
        soCriteria.setBusinessId(BigInteger.valueOf(11111));
        soCriteria.setBusinessName("ABC Company");
        soCriteria.setCustomerId(BigInteger.valueOf(222222));
        soCriteria.setSalesOrderDate(RMT2Date.toXmlDate("2020-01-01"));
        soCriteria.setSalesOrderId(BigInteger.valueOf(33333));
        soCriteria.setTargetLevel(XactCustomCriteriaTargetType.FULL);
        soCriteria.setSalesOrderTotal(BigDecimal.valueOf(1000.00));
        req.setCriteria(criteria);

        req.setHeader(head);

        String bodyXml = jaxb.marshalJsonMessage(req);
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);

        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_GET));
        Assert.assertTrue(bodyXml.contains("<target_level>FULL</target_level>"));
    }

    @Test
    public void testBuildFullResponse() {
        ObjectFactory fact = new ObjectFactory();
        AccountingTransactionResponse resp = fact.createAccountingTransactionResponse();

        HeaderType head = HeaderTypeBuilder.Builder.create().withApplication("accounting").withModule("transaction")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.ACCOUNTING_SALESORDER_GET)
                .withRouting(ApiTransactionCodes.ROUTE_ACCOUNTING)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        RecordTrackingType tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(RMT2Date.stringToDate("2020-01-01"))
                .withDateUpdate(RMT2Date.stringToDate("2020-01-01"))
                .withUserId("testuser")
                .build();

        SalesOrderItemType item1 = SalesOrderItemTypeBuilder.Builder.create()
                .withSalesOrderItemId(4321)
                .withSalesOrderId(777777)
                .withBackOrderQty(0)
                .withMarkup(2)
                .withUnitCost(100.00)
                .withOrderQty(3)
                .withRecordTracking(tracking)
                .build();
        SalesOrderItemType item2 = SalesOrderItemTypeBuilder.Builder.create()
                .withSalesOrderItemId(5432)
                .withSalesOrderId(777777)
                .withBackOrderQty(0)
                .withMarkup(2)
                .withUnitCost(100.00)
                .withOrderQty(2)
                .withRecordTracking(tracking)
                .build();

        RecordTrackingType tracking2 = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(RMT2Date.stringToDate("2020-01-01"))
                .withDateUpdate(RMT2Date.stringToDate("2020-02-01"))
                .withIpCreated("192.168.0.11")
                .withIpUpdate("192.168.0.11")
                .withUserId("testuser")
                .build();
        SalesOrderType so = SalesOrderTypeBuilder.Builder.create()
                .withCustomerAcctNo("111-111-111")
                .withInvoiced(false)
                .withSalesOrderId(777777)
                .withCustomerId(1111111)
                .withCustomerName("ABC Company")
                .withOrderTotal(1000.00)
                .withEffectiveDate(RMT2Date.stringToDate("2020-01-15"))
                .withStatusId(1)
                .withStatusDescription("Quote")
                .withSalesOrderItem(item1)
                .withSalesOrderItem(item2)
                .withRecordTracking(tracking2)
                .build();

        // Build transaction criteria group
        TransactionDetailGroup profile = fact.createTransactionDetailGroup();
        profile.setSalesOrders(fact.createSalesOrderListType());
        profile.getSalesOrders().getSalesOrder().add(so);

        resp.setProfile(profile);
        resp.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(resp);
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_GET));

        // Unmarshal SOAP body to JAXB object
        AccountingTransactionResponse results = (AccountingTransactionResponse) jaxb.unMarshalMessage(bodyXml);
        Assert.assertNotNull(results.getHeader().getTransaction());
        Assert.assertTrue(results.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.ACCOUNTING_SALESORDER_GET));
    }
}
