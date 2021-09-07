package org.rmt2.soap.accounting.disbursements;

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
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.TransactionCriteriaGroup;
import org.rmt2.jaxb.XactBasicCriteriaType;
import org.rmt2.jaxb.XactCriteriaType;
import org.rmt2.jaxb.XactCustomCriteriaTargetType;
import org.rmt2.jaxb.XactCustomRelationalCriteriaType;
import org.rmt2.util.HeaderTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class CashDisbursementQueryBasicSoapRequestBuilderTest {

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
                .withModule("transaction")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())
                
                // Set these header elements with dummy values in order to be properly assigned later.
                .withTransaction(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET)
                .withRouting(ApiTransactionCodes.ROUTE_ACCOUNTING)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();
        
        XactCriteriaType criteria = fact.createXactCriteriaType();
        criteria.setTargetLevel(XactCustomCriteriaTargetType.FULL);
        XactBasicCriteriaType xb = fact.createXactBasicCriteriaType();
        xb.setXactId(BigInteger.valueOf(34567));
        xb.setAccountNo("123-345-678");
        xb.setBusinessName("ABC Complay");
        xb.setXactDate("2018-12-01");
        xb.setInvoiceNo("1234566");
        xb.setConfirmNo("ADB-49384343");
        xb.setTenderId(BigInteger.valueOf(100));
        
        XactCustomRelationalCriteriaType customCriteria = fact.createXactCustomRelationalCriteriaType();
        
        criteria.setBasicCriteria(xb);
        criteria.setCustomCriteria(customCriteria);
        
        TransactionCriteriaGroup criteriaGroup = fact.createTransactionCriteriaGroup();
        criteriaGroup.setXactCriteria(criteria);
        req.setCriteria(criteriaGroup);
        req.setHeader(head);
        
        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);

        soapXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(soapXml);
        System.out.println(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET));
    }
  }
