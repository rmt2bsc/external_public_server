package org.rmt2.soap.media;

import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ArtistType;
import org.rmt2.jaxb.AudioVideoCriteriaType;
import org.rmt2.jaxb.AvProjectType;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.MimeCriteriaGroup;
import org.rmt2.jaxb.MimeCustomCriteriaTargetType;
import org.rmt2.jaxb.MimeDetailGroup;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.TrackType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.media.AVProjectTypeBuilder;
import org.rmt2.util.media.ArtistTypeBuilder;
import org.rmt2.util.media.TrackTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class ConsolidatedMediaQuerySoapRequestBuilderTest {

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
                .withModule("maint")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())
                
                // Set these header elements with dummy values in order to be properly assigned later.
                .withTransaction(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH)
                .withRouting(ApiTransactionCodes.ROUTE_MULTIMEDIA)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();
        
        MimeCriteriaGroup mcg = fact.createMimeCriteriaGroup();
        AudioVideoCriteriaType avct = fact.createAudioVideoCriteriaType();
        avct.setFetchFull(MimeCustomCriteriaTargetType.FULL);
        avct.setArtistId(10);
        avct.setArtistName("Artist Name Test");
        avct.setProjectId(200);
        avct.setProjectTitle("Project Title Test");
        avct.setYear(1999);
        avct.setTrackId(300);
        avct.setTrackTitle("Track Title Test");
        avct.setGenreName("Jazz");
        mcg.setAudioVideoCriteria(avct);
        req.setCriteria(mcg);
        req.setHeader(head);
        
        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }
 
    @Test
    public void testBuildsingleSearchTermRequest() {
        ObjectFactory fact = new ObjectFactory();
        MultimediaRequest req = fact.createMultimediaRequest();

        HeaderType head = HeaderTypeBuilder.Builder.create()
                .withApplication("media")
                .withModule("maint")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH)
                .withRouting(ApiTransactionCodes.ROUTE_MULTIMEDIA)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        MimeCriteriaGroup mcg = fact.createMimeCriteriaGroup();
        AudioVideoCriteriaType avct = fact.createAudioVideoCriteriaType();
        avct.setSearchTerm("Dennis Chambers");
        mcg.setAudioVideoCriteria(avct);
        req.setCriteria(mcg);
        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

    @Test
    public void testBuildResponse() {
        ObjectFactory fact = new ObjectFactory();
        MultimediaResponse req = fact.createMultimediaResponse();

        HeaderType head = HeaderTypeBuilder.Builder.create()
                .withApplication("media")
                .withModule("maint")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH)
                .withRouting(ApiTransactionCodes.ROUTE_MULTIMEDIA)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        MimeDetailGroup cgt = fact.createMimeDetailGroup();
        cgt.setAudioVideoDetails(fact.createAudioVideoType());

        AvProjectType pt = AVProjectTypeBuilder.Builder.create()
                .withProjectId(300)
                .withArtistId(200)
                .withCost(12.99)
                .withComments("Comments #1")
                .withYear(1984)
                .withTitle("Title #1")
                .withRipped(1)
                .withTotalTime(16)
                .withProducer("Producer #1")
                .build();

        TrackType tt1 = TrackTypeBuilder.Builder.create()
                .withTrackId(1000)
                .withGenreId(124)
                .withTrackName("Track #1")
                .withArtist(null)
                .withDiscNumber(1)
                .withHours(0)
                .withMinutes(3)
                .withSeconds(23)
                .build();
        pt.getTracks().getTrack().add(tt1);

        tt1 = TrackTypeBuilder.Builder.create()
                .withTrackId(1001)
                .withGenreId(15)
                .withTrackName("Track #2")
                .withArtist("Various Artist - Artist Name")
                .withDiscNumber(1)
                .withHours(0)
                .withMinutes(6)
                .withSeconds(33)
                .build();
        pt.getTracks().getTrack().add(tt1);

        tt1 = TrackTypeBuilder.Builder.create()
                .withTrackId(1002)
                .withGenreId(5)
                .withTrackName("Track #3")
                .withDiscNumber(1)
                .withHours(0)
                .withMinutes(5)
                .withSeconds(55)
                .build();
        pt.getTracks().getTrack().add(tt1);

        ArtistType at = ArtistTypeBuilder.Builder.create()
                .withArtistId(222)
                .withArtistName("Artist Name")
                .withProject(pt)
                .build();

        cgt.getAudioVideoDetails().getArtist().add(at);
        req.setProfile(cgt);
        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }
}
