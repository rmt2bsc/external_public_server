package org.rmt2.soap.accounting.sales;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.rmt2.jaxb.XactLineitemType;
import org.rmt2.jaxb.XactListType;
import org.rmt2.jaxb.XactType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.accounting.transaction.XactItemTypeBuilder;
import org.rmt2.util.accounting.transaction.XactTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesOrderTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.util.RMT2Date;
import com.api.xml.jaxb.JaxbUtil;

public class SalesOrderCloseSoapRequestBuilderTest {

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
                .withTransaction(ApiTransactionCodes.ACCOUNTING_SALESORDER_CLOSE_WITH_PAYMENT)
                .withRouting(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        SalesOrderType sot = SalesOrderTypeBuilder.Builder.create().withSalesOrderId(0).withCustomerId(3333)
                .withCustomerName("ABC Company")
                .withCustomerAcctNo("ACCT-NO-8888").withInvoiced(false).withOrderTotal(755.94)
                .withEffectiveDate(RMT2Date.stringToDate("2020-01-01"))
                .build();

        TransactionDetailGroup data = fact.createTransactionDetailGroup();
        SalesOrderListType silt = fact.createSalesOrderListType();
        silt.getSalesOrder().add(sot);
        data.setSalesOrders(silt);

        List<XactLineitemType> items = new ArrayList<XactLineitemType>();
        items.add(XactItemTypeBuilder.Builder.create().withXactTypeItemActvId(0).withAmount(755.94)
                .withXactId(0).withItemId(601).withItemTypeId(7001)
                .withDescription("item1").build());

        XactType xt = XactTypeBuilder.Builder.create().withXactId(0).withXactTypeId(60).withXactAmount(755.94)
                .withXactDate("2018-12-01").withReason("Test creating a transaction via message handler")
                .withNegInstrNo("1111-1111-1111-1111").withTenderId(100).withXactItems(items).build();

        XactListType xlt = fact.createXactListType();
        xlt.getTransaction().add(xt);
        data.setTransactions(xlt);

        // Build transaction detail group
        req.setProfile(data);

        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_CLOSE_WITH_PAYMENT));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_CLOSE_WITH_PAYMENT));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.ACCOUNTING_SALESORDER_CLOSE_WITH_PAYMENT));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

}
