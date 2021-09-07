package org.rmt2.soap.accounting.generalledger;

import java.math.BigInteger;
import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingGeneralLedgerRequest;
import org.rmt2.jaxb.GlAccountcatgType;
import org.rmt2.jaxb.GlAccounttypeType;
import org.rmt2.jaxb.GlBalancetypeType;
import org.rmt2.jaxb.GlCriteriaGroup;
import org.rmt2.jaxb.GlCriteriaType;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.accounting.generalledger.GlAccountBalanceTypeBuilder;
import org.rmt2.util.accounting.generalledger.GlAccountCategoryTypeBuilder;
import org.rmt2.util.accounting.generalledger.GlAccounttypeTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class GLAccountQuerySoapRequestBuilderTest {

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
        AccountingGeneralLedgerRequest req = fact.createAccountingGeneralLedgerRequest();
        
        HeaderType head =  HeaderTypeBuilder.Builder.create()
                .withApplication("accounting")
                .withModule(ConfigConstants.API_APP_MODULE_VALUE)
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())
                
                // Set these header elements with dummy values in order to be properly assigned later.
                .withTransaction(ApiTransactionCodes.GL_ACCOUNT_GET)
                .withRouting(ApiTransactionCodes.ROUTE_ACCOUNTING)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();
        
        
        GlCriteriaType criteria = fact.createGlCriteriaType();
        criteria.setAcctId(BigInteger.valueOf(200));
        criteria.setAccountDescription("GL ACcount Description Test");
        criteria.setAccountName("GL Account Name Test");
        criteria.setAccountNo("GL Account Number Test");
        criteria.setAccountCode("Account Code");

        GlBalancetypeType gbtt = GlAccountBalanceTypeBuilder.Builder.create()
                .withAcctBalanceTypeId(1).build();
        GlAccounttypeType gatt = GlAccounttypeTypeBuilder.Builder.create()
                .withAcctTypeId(111)
                .withDescription("GL Account Type Description Test").build();
        GlAccountcatgType gact = GlAccountCategoryTypeBuilder.Builder.create()
                .withAcctCatgId(300).withAccountType(gatt)
                .withDescription("GL Account Category Test").build();
        criteria.setAcctType(gatt);
        criteria.setAcctCatg(gact);
        criteria.setBalanceType(gbtt);
        
        GlCriteriaGroup criteriaGroup = fact.createGlCriteriaGroup();
        criteriaGroup.setGlCriteria(criteria);
        req.setCriteria(criteriaGroup);
        req.setHeader(head);
        
        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.GL_ACCOUNT_GET));
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
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.GL_ACCOUNT_GET));
    }
 }
