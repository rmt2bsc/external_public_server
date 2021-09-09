package org.rmt2.soap.media;

import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AudioBatchImportCriteriaType;
import org.rmt2.jaxb.BatchImportType;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.MimeCriteriaGroup;
import org.rmt2.jaxb.MimeDetailGroup;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.media.BatchImportTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.util.RMT2Date;
import com.api.xml.jaxb.JaxbUtil;

public class AudioBatchImportSoapRequestBuilderTest {

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
        MultimediaRequest req = fact.createMultimediaRequest();
        
        HeaderType head =  HeaderTypeBuilder.Builder.create()
                .withApplication("media")
                .withModule("batch")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())
                
                // Set these header elements with dummy values in order to be properly assigned later.
                .withTransaction(ApiTransactionCodes.MEDIA_AUDIO_METADATA_IMPORT_BATCH)
                .withRouting(ApiTransactionCodes.ROUTE_MULTIMEDIA)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();
        
        MimeCriteriaGroup mcg = fact.createMimeCriteriaGroup();

        AudioBatchImportCriteriaType batchCriteria = fact.createAudioBatchImportCriteriaType();
        batchCriteria.setServerName("rmtdalmedia01");
        batchCriteria.setShareName("MyBook1");
        batchCriteria.setRootPath("path1/path2");
        batchCriteria.setLocation("multimedia/audio");
        batchCriteria.setImportFilePath("C:/AppServer/data/video_batch_import.txt");
        mcg.setAudioBatchImportCriteria(batchCriteria);
        req.setCriteria(mcg);
        req.setHeader(head);
        
        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_AUDIO_METADATA_IMPORT_BATCH));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.MEDIA_AUDIO_METADATA_IMPORT_BATCH));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_AUDIO_METADATA_IMPORT_BATCH));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }
 
    @Test
    public void testBuildResponse() {
        ObjectFactory fact = new ObjectFactory();
        MultimediaResponse req = fact.createMultimediaResponse();

        HeaderType head = HeaderTypeBuilder.Builder.create()
                .withApplication("mime")
                .withModule("batch")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.MEDIA_AUDIO_METADATA_IMPORT_BATCH)
                .withRouting(ApiTransactionCodes.ROUTE_MULTIMEDIA)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        MimeDetailGroup cgt = fact.createMimeDetailGroup();
        BatchImportType g2t = BatchImportTypeBuilder.Builder.create()
                .withStartTime(RMT2Date.stringToDate("2020-01-01 01:20:45"))
                .withEndTime(RMT2Date.stringToDate("2020-01-01 02:48:02"))
                .withProcessTotal(85000)
                .withSuccessTotal(70000)
                .withFailureTotal(1000)
                .withNonAudioFilesEncountered(10000)
                .build();
        
        cgt.setBatchImportResults(g2t);

        req.setProfile(cgt);
        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_AUDIO_METADATA_IMPORT_BATCH));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.MEDIA_AUDIO_METADATA_IMPORT_BATCH));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_AUDIO_METADATA_IMPORT_BATCH));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }
}
