package org.rmt2.rest;

import java.util.Date;

import org.apache.log4j.Logger;
import org.rmt2.constants.ApiHeaderNames;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.util.HeaderTypeBuilder;

import com.RMT2Base;
import com.api.messaging.webservice.router.MessageRouterHelper;

/**
 * Common REST resource processor which functions to identify and setup
 * application, module, and transaction properties and to route requests to
 * their respective destinations.
 * 
 * @author Roy Terrell
 *
 */
public class RMT2BaseRestResouce extends RMT2Base {
    private Logger logger = Logger.getLogger(RMT2BaseRestResouce.class);
    protected MessageRouterHelper msgRouterHelper;
    protected String application;
    protected String module;

    /**
     * Create a RMT2BaseRestResouce which is aware of the application and
     * module.
     */
    public RMT2BaseRestResouce(String app, String module) {
        this.msgRouterHelper = new MessageRouterHelper();
        this.application = app;
        this.module = module;

        logger.info("Contacting web service for: [Application ->" + app + ", Module->" + module + "]");
    }


    /**
     * Creates the header with application, module, and transaction values.
     * 
     * @return {@link HeaderType}
     */
    protected HeaderType getHeader() {
        return HeaderTypeBuilder.Builder.create()
                .withApplication(this.application)
                .withModule(this.module)
                .withMessageMode(ApiHeaderNames.MESSAGE_MODE_REQUEST)
                .withDeliveryDate(new Date())
                
                // Set these header elements with dummy values in order to be properly assigned later.
                .withTransaction(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withRouting(ApiHeaderNames.DUMMY_HEADER_VALUE)
                .withDeliveryMode(ApiHeaderNames.DUMMY_HEADER_VALUE).build();
        
    }
    
}
