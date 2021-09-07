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
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.InventoryItemType;
import org.rmt2.jaxb.InventoryItemtypeType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.SalesOrderCriteria;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderListType;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.jaxb.TransactionCriteriaGroup;
import org.rmt2.jaxb.TransactionDetailGroup;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.accounting.inventory.InventoryItemTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesOrderItemTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesOrderTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.util.RMT2Date;
import com.api.xml.jaxb.JaxbUtil;

public class SalesOrderCreateSoapRequestBuilderTest {

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
    public void testBuildRequestSalesOrder() {
        ObjectFactory fact = new ObjectFactory();
        AccountingTransactionRequest req = fact.createAccountingTransactionRequest();

        HeaderType head = HeaderTypeBuilder.Builder.create().withApplication("accounting").withModule("transaction")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.ACCOUNTING_SALESORDER_CREATE)
                .withRouting(ApiTransactionCodes.ROUTE_ACCOUNTING)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        // Build transaction criteria group
        req.setCriteria(setupTransCriteriaGroup(fact, false, false));

        // Build transaction detail group
        req.setProfile(setupSalesOrderData(fact));

        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_CREATE));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_CREATE));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_CREATE));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

    @Test
    public void testBuildRequestSalesOrderWithInvoicing() {
        ObjectFactory fact = new ObjectFactory();
        AccountingTransactionRequest req = fact.createAccountingTransactionRequest();

        HeaderType head = HeaderTypeBuilder.Builder.create().withApplication("accounting").withModule("transaction")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_CREATE)
                .withRouting(ApiTransactionCodes.ROUTE_ACCOUNTING)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        // Build transaction criteria group
        req.setCriteria(setupTransCriteriaGroup(fact, true, false));

        // Build transaction detail group
        req.setProfile(setupSalesOrderData(fact));

        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_CREATE));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_CREATE));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_CREATE));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

    @Test
    public void testBuildRequestSalesOrderWithInvoicingAndPayment() {
        ObjectFactory fact = new ObjectFactory();
        AccountingTransactionRequest req = fact.createAccountingTransactionRequest();

        HeaderType head = HeaderTypeBuilder.Builder.create().withApplication("accounting").withModule("transaction")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE)
                .withRouting(ApiTransactionCodes.ROUTE_ACCOUNTING)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        // Build transaction criteria group
        req.setCriteria(setupTransCriteriaGroup(fact, true, true));

        // Build transaction detail group
        req.setProfile(setupSalesOrderData(fact));

        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

    private TransactionCriteriaGroup setupTransCriteriaGroup(ObjectFactory fact, boolean invoice, boolean payment) {
        TransactionCriteriaGroup criteria = fact.createTransactionCriteriaGroup();
        SalesOrderCriteria soCriteria = fact.createSalesOrderCriteria();
        if (invoice) {
            soCriteria.setInvoiceOnSalesOrderCreation(invoice);
        }
        if (payment) {
            soCriteria.setApplyPaymentOnSalesOrderCreation(payment);
        }
        criteria.setSalesCriteria(soCriteria);
        return criteria;
    }

    private TransactionDetailGroup setupSalesOrderData(ObjectFactory fact) {
        InventoryItemType item1 = InventoryItemTypeBuilder.Builder.create().withItemId(100).withActive(true).withItemName("Dell Computer")
                .withItemSerialNo("11111111").withMarkup(3).withUnitCost(150.99).withQtyOnHand(10).withVendorItemNo("1234-4839").build();

        InventoryItemtypeType iit = fact.createInventoryItemtypeType();
        iit.setItemTypeId(BigInteger.valueOf(222));
        item1.setItemType(iit);

        InventoryItemType item2 = InventoryItemTypeBuilder.Builder.create().withItemId(200).withActive(true).withItemName("HP Computer")
                .withItemSerialNo("22222222").withMarkup(3).withUnitCost(100.99).withQtyOnHand(20).withVendorItemNo("4444-4839").build();

        InventoryItemtypeType iit2 = fact.createInventoryItemtypeType();
        iit2.setItemTypeId(BigInteger.valueOf(222));
        item2.setItemType(iit2);

        SalesOrderItemType soit1 = SalesOrderItemTypeBuilder.Builder.create().withSalesOrderItemId(100).withSalesOrderId(0).withInventoryItem(item1)
                .withUnitCost(150.99).withMarkup(3).withOrderQty(1).build();
        SalesOrderItemType soit2 = SalesOrderItemTypeBuilder.Builder.create().withSalesOrderItemId(200).withSalesOrderId(0).withInventoryItem(item2)
                .withUnitCost(100.99).withMarkup(3).withOrderQty(1).build();

        SalesOrderType sot = SalesOrderTypeBuilder.Builder.create().withSalesOrderId(0).withCustomerId(3333).withCustomerName("ABC Company")
                .withCustomerAcctNo("ACCT-NO-8888").withInvoiced(false).withOrderTotal(755.94).withEffectiveDate(RMT2Date.stringToDate("2020-01-01"))
                .withSalesOrderItem(soit1).withSalesOrderItem(soit2).build();

        TransactionDetailGroup data = fact.createTransactionDetailGroup();
        SalesOrderListType silt = fact.createSalesOrderListType();
        silt.getSalesOrder().add(sot);
        data.setSalesOrders(silt);
        return data;
    }
}
