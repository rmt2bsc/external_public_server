package org.rmt2.soap.accounting.subsidiary;

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
import org.rmt2.jaxb.CreditortypeCriteriaType;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.TransactionCriteriaGroup;
import org.rmt2.util.HeaderTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class CreditorTypeQuerySoapRequestBuilderTest {

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
        AccountingTransactionRequest req = fact.createAccountingTransactionRequest();
        
        HeaderType head =  HeaderTypeBuilder.Builder.create()
                .withApplication("accounting")
                .withModule("subsidiary")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())
                
                // Set these header elements with dummy values in order to be properly assigned later.
                .withTransaction(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TYPE_GET)
                .withRouting(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();
        
        CreditortypeCriteriaType criteria = fact.createCreditortypeCriteriaType();
        criteria.setCreditorTypeId(BigInteger.valueOf(3333));
        criteria.setDecription("Creditor Type Description");

        TransactionCriteriaGroup criteriaGroup = fact.createTransactionCriteriaGroup();
        criteriaGroup.setCreditortypeCriteria(criteria);
        req.setCriteria(criteriaGroup);
        req.setHeader(head);
        
        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TYPE_GET));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TYPE_GET));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TYPE_GET));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

}
