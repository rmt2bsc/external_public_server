package org.rmt2.soap.accounting.receipts;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.CustomerPaymentConfirmation;
import org.rmt2.jaxb.CustomerPaymentConfirmation.CustomerData;
import org.rmt2.jaxb.CustomerPaymentConfirmation.SalesOrderData;
import org.rmt2.jaxb.CustomerPaymentConfirmation.XactData;
import org.rmt2.util.accounting.transaction.sales.PayConfirmCustomerBuilder;
import org.rmt2.util.accounting.transaction.sales.PayConfirmSalesOrderBuilder;
import org.rmt2.util.accounting.transaction.sales.PayConfirmXactBuilder;
import org.rmt2.util.accounting.transaction.sales.PayConfirmationBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class CustomerPaymentConfirmationBuilderTest {

    private JaxbUtil jaxb;

    @Before
    public void setUp() throws Exception {
        try {
            jaxb = SystemConfigurator
                    .getJaxb(ConfigConstants.JAXB_CONTEXNAME_DEFAULT);
        } catch (Exception e) {
            jaxb = new JaxbUtil(MessagingConstants.JAXB_RMT2_PKG);
        }
    }

    @Test
    public void testBuildRequest() {
        CustomerData cd = PayConfirmCustomerBuilder.Builder.create()
                .withBalance(100.00)
                .withBusinessId(1111)
                .withContactEmail("johndoe@gte.net")
                .withCustomerId(2222)
                .withGlAccountId(10)
                .withAccoutNo("ACCT-1234-1920")
                .build();

        SalesOrderData sod = PayConfirmSalesOrderBuilder.Builder.create()
                .withBusinessId(1111)
                .withCustomerId(2222)
                .withEffectiveDate("2020-04-01")
                .withInvoiced(1)
                .withOrderTotal(100.00)
                .build();

        XactData xd = PayConfirmXactBuilder.Builder.create()
                .withTender("CASH")
                .withOrderTotal(100.00)
                .withXactDate("2020-04-10")
                .withXactId(7777)
                .withXactTypeId(20)
                .withConfirmNo("C32933-0039")
                .withXactReason("Cash Receipt: Full payment received for sales order #1292")
                .build();

        CustomerPaymentConfirmation pc = PayConfirmationBuilder.Builder.create()
                .withAppRoot("c:/tmp")
                .withPageTitle("Page Title")
                .withCustomer(cd)
                .withSalesOrder(sod)
                .withXact(xd)
                .build();

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(pc);
        Assert.assertNotNull(bodyXml);
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains("ACCT-1234-1920"));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertTrue(bodyXml.contains("ACCT-1234-1920"));
        Assert.assertNotNull(bodyXml);
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }
}
