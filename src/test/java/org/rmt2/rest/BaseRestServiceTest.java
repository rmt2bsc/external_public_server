package org.rmt2.rest;

import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.MockRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.api.messaging.webservice.router.MessageRouterHelper;
import com.api.messaging.webservice.router.MessageRoutingInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RMT2BaseRestResouce.class })
public class BaseRestServiceTest {

    protected MessageRouterHelper mockMsgRouterHelper;
    protected MessageRoutingInfo mockMessageRoutingInfo;
    // protected MessageRouterHelper msgRouterHelper;

    @Before
    public void setUp() throws Exception {
        // /Users/royterrell/work/External_WebServices_Server
        // String curDir = RMT2File.getCurrentDirectory();
        // String configFile = curDir +
        // "/src/test/resources/config/TestAppServerConfig.xml";
        // SystemConfigurator sysConfig = new SystemConfigurator();
        // try {
        // sysConfig.start(configFile);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        //
        // // This will force the Service Registry to be setup.
        // msgRouterHelper = new MessageRouterHelper();

        // Setup common mocks
        this.setupMocks();
    }

    @After
    public void tearDown() throws Exception {
    }

    private void setupMocks() {
        this.mockMsgRouterHelper = Mockito.mock(MessageRouterHelper.class);
        this.mockMessageRoutingInfo = Mockito.mock(MessageRoutingInfo.class);
        try {
            whenNew(MessageRouterHelper.class).withNoArguments().thenReturn(this.mockMsgRouterHelper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes the MessageRouterHelper class from mock repository.
     * <p>
     * This is typically called when you want to exercise the "Server
     * Unavailable" test scenario
     */
    protected void cancelMessageRouterHelperMock() {
        MockRepository.remove(MessageRouterHelper.class);
    }

    /**
     * 
     * @param appId
     * @param moduleId
     * @param transactionId
     * @return
     */
    protected MessageRoutingInfo buildMockMessageRoutingInfo(String appId, String moduleId, String transactionId) {
        MessageRoutingInfo mockMessageRoutingInfo = new MessageRoutingInfo();
        mockMessageRoutingInfo.setApplicatoinId(appId);
        mockMessageRoutingInfo.setModuleId(moduleId);
        mockMessageRoutingInfo.setMessageId(transactionId);
        mockMessageRoutingInfo.setDeliveryMode("SYNC");
        mockMessageRoutingInfo.setDestination("TEST_QUEUE");
        mockMessageRoutingInfo.setRouterType("JMS");
        return mockMessageRoutingInfo;
    }

    @Test
    public void dummyTest() {

    }
}
