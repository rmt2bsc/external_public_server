package org.rmt2.rest.media;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.InputStream;
import java.math.BigInteger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.jaxb.MimeContentType;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.rest.RMT2BaseRestResouce;

import com.api.messaging.webservice.router.MessageRouterHelper;
import com.util.RMT2Base64Encoder;
import com.util.RMT2File;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RMT2BaseRestResouce.class })
public class DocumentMediaResourceTest {
    private static final long TEST_CONTENT_ID = 7777;
    private static final long TEST_INVALID_CONTENT_ID = 0;

    private MessageRouterHelper mockMsgRouterHelper;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private void setupMocks() {
        this.mockMsgRouterHelper = Mockito.mock(MessageRouterHelper.class);
        try {
            whenNew(MessageRouterHelper.class).withNoArguments().thenReturn(this.mockMsgRouterHelper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetContentByIdSuccess() {
        this.setupMocks();
        ObjectFactory f = new ObjectFactory();
        MultimediaResponse mockResponse = f.createMultimediaResponse();

        MimeContentType content = f.createMimeContentType();
        content.setAppCode("ACCT");
        content.setContentId(BigInteger.valueOf(TEST_CONTENT_ID));
        content.setFilename("example.jpg");
        content.setFilepath("/tmp/somefilepath/");
        InputStream is = ClassLoader.getSystemResourceAsStream("pearl-weathered-leather-1600-1200.jpg");
        byte contentBytes[] = RMT2File.getStreamByteData(is);
        String imgContent = RMT2Base64Encoder.encode(contentBytes);
        content.setBinaryData(imgContent);
        mockResponse.setContent(content);

        when(mockMsgRouterHelper.routeJsonMessage(any(String.class), any(MultimediaRequest.class)))
                .thenReturn(mockResponse);

        DocumentMediaResource srvc = new DocumentMediaResource("getContent");
        Response resp = srvc.fetchImageContent(TEST_CONTENT_ID);
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }

    @Test
    public void testGetContentWithInvalidId() {
        DocumentMediaResource srvc = new DocumentMediaResource("getContent");
        Response resp = null;
        try {
            srvc.fetchImageContent(TEST_INVALID_CONTENT_ID);
        } catch (WebApplicationException e) {
            resp = e.getResponse();
        }
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }

    @Test
    public void testGetContentBusinessServerUnavailable() {
        DocumentMediaResource srvc = new DocumentMediaResource("getContent");
        Response resp = null;
        try {
            srvc.fetchImageContent(TEST_CONTENT_ID);
        } catch (WebApplicationException e) {
            resp = e.getResponse();
        }
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }

    // @Test
    // public void testGetContentWithInvalidId() {
    // ObjectFactory f = new ObjectFactory();
    // MultimediaRequest mockRequest = f.createMultimediaRequest();
    // MultimediaResponse mockResponse = f.createMultimediaResponse();
    //
    // HeaderType mockHeaderType = f.createHeaderType();
    // mockHeaderType.setApplication("media");
    // mockHeaderType.setModule("document");
    // mockHeaderType.setTransaction("getContent");
    // mockRequest.setHeader(mockHeaderType);
    // mockRequest.setContentId(BigInteger.valueOf(TEST_CONTENT_ID));
    //
    // MimeContentType content = f.createMimeContentType();
    // content.setAppCode("ACCT");
    // content.setContentId(BigInteger.valueOf(TEST_CONTENT_ID));
    // content.setFilename("example.jpg");
    // content.setFilepath("/tmp/somefilepath/");
    // InputStream is =
    // ClassLoader.getSystemResourceAsStream("pearl-weathered-leather-1600-1200.jpg");
    // byte contentBytes[] = RMT2File.getStreamByteData(is);
    // String imgContent = RMT2Base64Encoder.encode(contentBytes);
    // content.setBinaryData(imgContent);
    // mockResponse.setContent(content);
    //
    // when(mockMsgRouterHelper.routeJsonMessage(any(String.class),
    // any(MultimediaRequest.class)))
    // .thenReturn(mockResponse);
    //
    // DocumentMediaResource srvc = new DocumentMediaResource("getContent");
    // Response resp = srvc.fetchImageContent(TEST_CONTENT_ID);
    // Object obj = resp.getEntity();
    // Assert.assertNotNull(obj);
    // }
}
