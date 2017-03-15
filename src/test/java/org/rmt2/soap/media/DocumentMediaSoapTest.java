package org.rmt2.soap.media;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.api.messaging.webservice.router.MessageRouterHelper;
import com.api.messaging.webservice.soap.SoapMessageHelper;
import com.api.messaging.webservice.soap.engine.RMT2SoapEngine;
import com.api.web.Request;
import com.api.web.Response;
import com.api.web.controller.StatelessControllerProcessingException;
import com.api.web.controller.scope.HttpVariableScopeFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpVariableScopeFactory.class, RMT2SoapEngine.class })
public class DocumentMediaSoapTest {

    private static String REQUEST_SEARCH_SUCCESS;
    private static String REQUEST_SEARCH_PAYLOAD;

    @Before
    public void setUp() throws Exception {

        StringBuilder s = new StringBuilder();
        s.append("   <MultimediaRequest>");
        s.append("      <header>");
        s.append("         <routing>fkdkdfjdkd9dkd9d</routing>");
        s.append("         <application>media</application>");
        s.append("         <module>document</module>");
        s.append("         <transaction>getContent</transaction>");
        s.append("         <delivery_mode>SYNC</delivery_mode>");
        s.append("         <message_mode>REQUEST</message_mode>");
        s.append("         <delivery_date>2017-1-1</delivery_date>");
        s.append("         <session_id>849032894329393939</session_id>");
        s.append("         <user_id>test_user</user_id>");
        s.append("      </header>");
        s.append("      <content_id>1200</content_id>");
        s.append("   </MultimediaRequest>");
        REQUEST_SEARCH_PAYLOAD = s.toString().trim();

        s.delete(0, s.length());

        s.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        s.append("<soapenv:Header/>");
        s.append("<soapenv:Body>");
        s.append(REQUEST_SEARCH_PAYLOAD);
        s.append("</soapenv:Body>");
        s.append("</soapenv:Envelope>");
        REQUEST_SEARCH_SUCCESS = s.toString();

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSearchSuccess() {
        HttpServletRequest mockHttpRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockHttpResponse = Mockito.mock(HttpServletResponse.class);
        Request mockGenericRequest = Mockito.mock(Request.class);
        Response mockGenericResponse = Mockito.mock(Response.class);
        SOAPMessage mockSoapMessage = Mockito.mock(SOAPMessage.class);
        SOAPMessage mockSoapMessageResponse = Mockito.mock(SOAPMessage.class);
        SoapMessageHelper mockSoapMessageHelper = Mockito.mock(SoapMessageHelper.class);
        MessageRouterHelper mockMessageRouter = Mockito.mock(MessageRouterHelper.class);

        try {
            whenNew(SoapMessageHelper.class).withNoArguments().thenReturn(mockSoapMessageHelper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            whenNew(MessageRouterHelper.class).withNoArguments().thenReturn(mockMessageRouter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        when(mockHttpRequest.getRequestURL()).thenReturn(new StringBuffer("http://Test-URL"));
        when(mockSoapMessageHelper.getSoapInstance(mockGenericRequest)).thenReturn(mockSoapMessage);
        when(mockSoapMessageHelper.toString(mockSoapMessage)).thenReturn(REQUEST_SEARCH_SUCCESS);
        when(mockSoapMessageHelper.getBody(mockSoapMessage)).thenReturn(REQUEST_SEARCH_PAYLOAD);
        when(mockSoapMessageHelper.extractAttachments(mockSoapMessage)).thenReturn(null);
        when(mockMessageRouter.routeSoapMessage("getContent", REQUEST_SEARCH_PAYLOAD, null))
                .thenReturn(mockSoapMessageResponse);
        
        PowerMockito.mockStatic(HttpVariableScopeFactory.class);
        when(HttpVariableScopeFactory.createHttpRequest(mockHttpRequest)).thenReturn(mockGenericRequest);
        when(HttpVariableScopeFactory.createHttpResponse(mockHttpResponse)).thenReturn(mockGenericResponse);

        // Hashtable ht = new Hashtable();
        // Enumeration mockEnumeration = ht.keys();
        //
        // MessageFactory mockMessageFactory =
        // Mockito.mock(MessageFactory.class);
        // try {
        // whenNew(MessageFactory.class).withNoArguments().thenReturn(mockMessageFactory);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // when(mockMessageFactory.createMessage(any(MimeHeaders.class, in))
        //

        // when(mockGenericRequest.getHeaderNames()).thenReturn(mockEnumeration);
        
        // JmsConnectionManager mockJmsConnectionManager =
        // Mockito.mock(JmsConnectionManager.class);
        // Connection mockJmsConnection = Mockito.mock(Connection.class);
        // JmsClientManager mockJmsClientManager =
        // Mockito.mock(JmsClientManager.class);
        // Context mockIntialContext = Mockito.mock(Context.class);
        // Destination mockDestination = Mockito.mock(Destination.class);
        // TextMessage mockTextMsg = Mockito.mock(TextMessage.class);
        // TextMessage mockResponseTextMsg = Mockito.mock(TextMessage.class);
        // try {
        // whenNew(JmsConnectionManager.class).withNoArguments().thenReturn(mockJmsConnectionManager);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // when(mockJmsConnectionManager.getConnection(JmsConstants.DEFAUL_MSG_SYS)).thenReturn(mockJmsConnection);
        // when(mockJmsConnectionManager.getJndi()).thenReturn(mockIntialContext);
        // try {
        // whenNew(JmsClientManager.class).withArguments(any(Connection.class),
        // any(Context.class))
        // .thenReturn(mockJmsClientManager);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // try {
        // when(mockJmsClientManager.createTextMessage(any(String.class))).thenReturn(mockTextMsg);
        // when(mockTextMsg.getJMSReplyTo()).thenReturn(mockDestination);
        // when(mockJmsClientManager.createReplyToDestination(any(String.class),
        // mockTextMsg))
        // .thenReturn(mockDestination);
        // when(mockJmsClientManager.listen(mockDestination,
        // 10000)).thenReturn(mockResponseTextMsg);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        RMT2SoapEngine eng = new RMT2SoapEngine();
        try {
            eng.processRequest(mockHttpRequest, mockHttpResponse);
        } catch (StatelessControllerProcessingException e) {
            e.printStackTrace();
        }

        String soapXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"> <soapenv:Header/> <soapenv:Body> <MultimediaRequest> <header> <routing>fkdkdfjdkd9dkd9d</routing> <application>media</application> <module>document</module> <transaction>getContent</transaction> <delivery_mode>SYNC</delivery_mode> <message_mode>REQUEST</message_mode> <delivery_date>2017-1-1</delivery_date> <session_id>849032894329393939</session_id> <user_id>test_user</user_id> </header> <content_id>1200</content_id> </MultimediaRequest> </soapenv:Body> </soapenv:Envelope>";
    }

}
