package org.rmt2.soap.addressbook;

import java.math.BigInteger;
import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.PostalRequest;
import org.rmt2.jaxb.PostalRequest.PostalCriteria;
import org.rmt2.jaxb.ZipResultFormatType;
import org.rmt2.jaxb.ZipcodeCriteriaType;
import org.rmt2.util.HeaderTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class ZipcodeQuerySoapRequestBuilderTest {

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
        PostalRequest req = fact.createPostalRequest();
        
        HeaderType head =  HeaderTypeBuilder.Builder.create()
                .withApplication("addressbook")
                .withModule("postal")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())
                
                // Set these header elements with dummy values in order to be properly assigned later.
                .withTransaction(ApiTransactionCodes.ZIPCODE_GET)
                .withRouting(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();
        
        PostalCriteria criteria = fact.createPostalRequestPostalCriteria();
        ZipcodeCriteriaType zipCriteria = fact.createZipcodeCriteriaType();
        zipCriteria.setZipcode(BigInteger.valueOf(75232));
        zipCriteria.setCity("Dallas");
        zipCriteria.setState("TX");
        zipCriteria.setCountyName("Dallas");
        zipCriteria.setAreaCode("214");
        zipCriteria.setResultFormat(ZipResultFormatType.FULL);
        criteria.setZipcode(zipCriteria);
        req.setPostalCriteria(criteria);
        req.setHeader(head);
        
        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ZIPCODE_GET));
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
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.ZIPCODE_GET));
    }

}
