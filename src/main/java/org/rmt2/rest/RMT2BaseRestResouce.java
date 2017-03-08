package org.rmt2.rest;

import org.apache.log4j.Logger;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;

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
    protected String transaction;

    /**
     * Create a RMT2BaseRestResouce which is aware of the application, module,
     * and transaction.
     */
    public RMT2BaseRestResouce(String app, String module, String transaction) {
        this.msgRouterHelper = new MessageRouterHelper();
        this.application = app;
        this.module = module;
        this.transaction = transaction;

        logger.info("Contacting web service for: [Application ->" + app + ", Module->" + module + ", Transaction->"
                + transaction + "]");
    }

    /**
     * Creates the header with application, module, and transaction values.
     * 
     * @return {@link HeaderType}
     */
    protected HeaderType getHeader() {
        ObjectFactory f = new ObjectFactory();
        HeaderType header = f.createHeaderType();
        header.setApplication(this.application);
        header.setModule(this.module);
        header.setTransaction(this.transaction);
        return header;
    }
}
