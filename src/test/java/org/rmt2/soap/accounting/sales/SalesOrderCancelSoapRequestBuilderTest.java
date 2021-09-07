package org.rmt2.soap.accounting.sales;

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
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.SalesOrderListType;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.jaxb.TransactionDetailGroup;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesOrderTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class SalesOrderCancelSoapRequestBuilderTest {

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
    public void testBuildRequest() {
        ObjectFactory fact = new ObjectFactory();
        AccountingTransactionRequest req = fact.createAccountingTransactionRequest();

        HeaderType head = HeaderTypeBuilder.Builder.create().withApplication("accounting").withModule("transaction")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.ACCOUNTING_SALESORDER_CANCEL).withRouting(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        // Build transaction detail group
        req.setProfile(setupSalesOrderData(fact));

        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_CANCEL));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_CANCEL));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_CANCEL));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }


    private TransactionDetailGroup setupSalesOrderData(ObjectFactory fact) {
        SalesOrderType sot1 = SalesOrderTypeBuilder.Builder.create().withSalesOrderId(1234567).build();
        SalesOrderType sot2 = SalesOrderTypeBuilder.Builder.create().withSalesOrderId(2234567).build();
        SalesOrderType sot3 = SalesOrderTypeBuilder.Builder.create().withSalesOrderId(3234567).build();

        TransactionDetailGroup data = fact.createTransactionDetailGroup();
        SalesOrderListType silt = fact.createSalesOrderListType();
        silt.getSalesOrder().add(sot1);
        silt.getSalesOrder().add(sot2);
        silt.getSalesOrder().add(sot3);
        data.setSalesOrders(silt);
        return data;
    }
}
