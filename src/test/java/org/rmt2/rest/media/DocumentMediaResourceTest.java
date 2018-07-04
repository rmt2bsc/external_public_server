package org.rmt2.rest.media;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.math.BigInteger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.MimeContentType;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.rest.BaseRestServiceTest;
import org.rmt2.rest.RMT2BaseRestResouce;

import com.api.messaging.webservice.router.MessageRoutingInfo;
import com.api.util.RMT2Base64Encoder;
import com.api.util.RMT2File;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RMT2BaseRestResouce.class })
public class DocumentMediaResourceTest extends BaseRestServiceTest {
    private static final long TEST_CONTENT_ID = 7777;
    private static final long TEST_NEW_CONTENT_ID = 12345;
    private static final long TEST_INVALID_CONTENT_ID = 0;
    private static final String TEST_FILENAME = "pearl-weathered-leather-1600-1200.jpg";

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
    }

    private MimeContentType createMockContentType(Long contentId, String appCode, String fileName, String filePath) {
        ObjectFactory f = new ObjectFactory();
        MimeContentType ct = f.createMimeContentType();
        ct.setAppCode(appCode);
        ct.setContentId(contentId == null ? null : BigInteger.valueOf(contentId));
        ct.setFilename(fileName);
        ct.setFilepath(filePath);
        // Set binary content
        String imgContent = this.getImageContentTypeAsBase64String(fileName);
        ct.setBinaryData("ImageData".getBytes());
        return ct;
    }

    private String getImageContentTypeAsBase64String(String fileName) {
        String imgContent = null;
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
            byte contentBytes[] = RMT2File.getStreamByteData(is);
            imgContent = RMT2Base64Encoder.encode(contentBytes);
        } catch (Exception e) {
            imgContent = null;
        }
        return imgContent;
    }

    @Test
    public void testGetContentByIdSuccess() {
        ObjectFactory f = new ObjectFactory();
        MultimediaResponse mockResponse = f.createMultimediaResponse();
        MimeContentType content = this.createMockContentType(TEST_CONTENT_ID, "ACCT", TEST_FILENAME,
                "/tmp/somefilepath/");
        mockResponse.setContent(content);

        MessageRoutingInfo mockRouteInfo = this.buildMockMessageRoutingInfo("media", "document",
                ApiTransactionCodes.MEDIA_GET_CONTENT);
        when(mockMsgRouterHelper.getRoutingInfo(ApiTransactionCodes.MEDIA_GET_CONTENT))
                .thenReturn(mockRouteInfo);
        when(mockMsgRouterHelper.routeJsonMessage(eq(ApiTransactionCodes.MEDIA_GET_CONTENT), any(MultimediaRequest.class)))
                .thenReturn(mockResponse);

        DocumentMediaResource srvc = new DocumentMediaResource();
        Response resp = srvc.fetchImageContent(TEST_CONTENT_ID);
        Assert.assertEquals(Status.OK.getStatusCode(), resp.getStatus());
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }

    @Test
    public void testGetContentWithInvalidId() {
        DocumentMediaResource srvc = new DocumentMediaResource();
        Response resp = null;
        try {
            srvc.fetchImageContent(TEST_INVALID_CONTENT_ID);
        } catch (WebApplicationException e) {
            resp = e.getResponse();
        }
        Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }

    @Test
    public void testSaveContentSuccess() {
        ObjectFactory f = new ObjectFactory();
        MultimediaResponse mockResponse = f.createMultimediaResponse();
        MimeContentType content = this.createMockContentType(TEST_NEW_CONTENT_ID, "ACCT", TEST_FILENAME,
                "/tmp/somefilepath/");
        mockResponse.setContent(content);

        MessageRoutingInfo mockRouteInfo = this.buildMockMessageRoutingInfo("media", "document",
                ApiTransactionCodes.MEDIA_SAVE_CONTENT);

        when(mockMsgRouterHelper.routeJsonMessage(eq(ApiTransactionCodes.MEDIA_SAVE_CONTENT), any(MultimediaRequest.class)))
                .thenReturn(mockResponse);

        when(mockMsgRouterHelper.getRoutingInfo(ApiTransactionCodes.MEDIA_SAVE_CONTENT))
                .thenReturn(mockRouteInfo);
        DocumentMediaResource srvc = new DocumentMediaResource();
        MimeContentType contentTypeParm = this.createMockContentType(0L, "ACCT", TEST_FILENAME, "/tmp/somefilepath/");
        Response resp = srvc.saveImageContent(contentTypeParm);
        Assert.assertEquals(Status.OK.getStatusCode(), resp.getStatus());
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }

    @Test
    public void testSaveContentWithNullContentObject() {
        Response resp = null;
        DocumentMediaResource srvc = new DocumentMediaResource();
        try {
            resp = srvc.saveImageContent(null);
        } catch (WebApplicationException e) {
            resp = e.getResponse();
        }
        Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }

    @Test
    public void testSaveContentWithNullContentId() {
        Response resp = null;
        DocumentMediaResource srvc = new DocumentMediaResource();
        MimeContentType contentTypeParm = this.createMockContentType(null, "ACCT", TEST_FILENAME, "/tmp/somefilepath/");
        try {
            resp = srvc.saveImageContent(contentTypeParm);
        } catch (WebApplicationException e) {
            resp = e.getResponse();
        }
        Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }

    @Test
    public void testSaveContentWithNonZeroContentId() {
        Response resp = null;
        DocumentMediaResource srvc = new DocumentMediaResource();
        MimeContentType contentTypeParm = this.createMockContentType(849L, "ACCT", TEST_FILENAME, "/tmp/somefilepath/");
        try {
            resp = srvc.saveImageContent(contentTypeParm);
        } catch (WebApplicationException e) {
            resp = e.getResponse();
        }
        Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }

    @Test
    public void testSaveContentWithNullBinaryContent() {
        Response resp = null;
        DocumentMediaResource srvc = new DocumentMediaResource();
        MimeContentType contentTypeParm = this.createMockContentType(0L, "ACCT", null, "/tmp/somefilepath/");
        try {
            resp = srvc.saveImageContent(contentTypeParm);
        } catch (WebApplicationException e) {
            resp = e.getResponse();
        }
        Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
        Object obj = resp.getEntity();
        Assert.assertNotNull(obj);
    }
}
