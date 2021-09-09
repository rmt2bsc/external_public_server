package org.rmt2.soap.media;

import java.math.BigInteger;
import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ArtistType;
import org.rmt2.jaxb.AvProjectType;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.MimeDetailGroup;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.TrackType;
import org.rmt2.util.HeaderTypeBuilder;
import org.rmt2.util.media.AVProjectTypeBuilder;
import org.rmt2.util.media.ArtistTypeBuilder;
import org.rmt2.util.media.TrackTypeBuilder;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.xml.jaxb.JaxbUtil;

public class TracksUpdateSoapRequestBuilderTest {

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
                .withTransaction(ApiTransactionCodes.MEDIA_TRACK_UPDATE)
                .withRouting(ApiTransactionCodes.ROUTE_MULTIMEDIA)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();
        
        MimeDetailGroup mdg = fact.createMimeDetailGroup();
        mdg.setAudioVideoDetails(fact.createAudioVideoType());

        TrackType tt1 = TrackTypeBuilder.Builder.create()
                .withTrackId(1000)
                .withProjectId(300)
                .withGenreId(124)
                .withTrackName("Track #1")
                .withArtist(null)
                .withDiscNumber(1)
                .withHours(0)
                .withMinutes(3)
                .withSeconds(23)
                .withComments("Track Comments")
                .withComposer("Track Composer")
                .withLocationFilename("Location File Name")
                .withLocationPath("/artist/project/track/location_path")
                .withLocationRootPath("track_root_path")
                .withLocationServerName("track_servername")
                .withLocationShareName("track_shearename")
                .withLyrisist("Track Lyrisist")
                .withProducer("Track Producer")
                .build();

        AvProjectType pt = AVProjectTypeBuilder.Builder.create()
                .withProjectId(300)
                .withTrack(tt1)
                .build();

        ArtistType at = ArtistTypeBuilder.Builder.create()
                .withArtistId(222)
                .withProject(pt)
                .build();

        mdg.getAudioVideoDetails().getArtist().add(at);
        req.setProfile(mdg);
        req.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(req);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_TRACK_UPDATE));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.MEDIA_TRACK_UPDATE));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_TRACK_UPDATE));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }

    @Test
    public void testBuildResponse() {
        ObjectFactory fact = new ObjectFactory();
        MultimediaResponse resp = fact.createMultimediaResponse();

        HeaderType head = HeaderTypeBuilder.Builder.create()
                .withApplication("media")
                .withModule("maint")
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())

                // Set these header elements with dummy values in order to be
                // properly assigned later.
                .withTransaction(ApiTransactionCodes.MEDIA_TRACK_UPDATE)
                .withRouting(ApiTransactionCodes.ROUTE_MULTIMEDIA)
                .withSessionId(ConfigConstants.API_DUMMY_SESSION_ID)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();

        ReplyStatusType rst = fact.createReplyStatusType();
        rst.setRecordCount(BigInteger.ONE);
        rst.setReturnCode(BigInteger.ONE);
        rst.setReturnStatus("200");
        rst.setMessage("Media track, 1000, was updated successfully");
        resp.setReplyStatus(rst);
        resp.setHeader(head);

        // Create SOAP object using response XML
        String bodyXml = jaxb.marshalJsonMessage(resp);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_TRACK_UPDATE));
        SoapMessageHelper util = new SoapMessageHelper();
        String soapXml = util.createRequest(bodyXml);
        Assert.assertNotNull(soapXml);
        Assert.assertTrue(soapXml.contains(ApiTransactionCodes.MEDIA_TRACK_UPDATE));
        SOAPMessage soapObj = util.getSoapInstance(soapXml);
        Assert.assertNotNull(soapObj);

        // Extract Body from SOAP object
        bodyXml = util.getBody(soapObj);
        Assert.assertNotNull(bodyXml);
        Assert.assertTrue(bodyXml.contains(ApiTransactionCodes.MEDIA_TRACK_UPDATE));
        System.out.println("XML extracted from SOAP body instance:  ");
        System.out.println(bodyXml);
    }
}
