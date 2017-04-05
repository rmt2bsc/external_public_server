package org.rmt2.rest;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;
import org.rmt2.jaxb.HeaderType;
import org.rmt2.jaxb.ObjectFactory;

import com.RMT2Base;
import com.api.messaging.webservice.router.MessageRouterHelper;
import com.api.messaging.webservice.router.MessageRoutingException;
import com.api.messaging.webservice.router.MessageRoutingInfo;
import com.util.RMT2Date;
import com.util.RMT2String2;

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
    private MessageRouterHelper msgRouterHelper;
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
     * Returns the message routing information for a given transaction id.
     * 
     * @param transactionId
     *            the transaction id
     * @return an instance of {@link MessageRoutingInfo}
     * @throws MessageRoutingException
     *             <i>transactionId</i> is invalid.<br>
     *             The message router helper object has not been initialized.
     */
    protected MessageRoutingInfo getRouting(String transactionId) {
        if (RMT2String2.isEmpty(transactionId)) {
            throw new MessageRoutingException("Invalid transaction id");
        }
        if (this.msgRouterHelper == null) {
            throw new MessageRoutingException("Message router helper is invalid");
        }
        return this.msgRouterHelper.getRoutingInfo(transactionId);
    }

    /**
     * Routes a payload message to destination based on the details contained in
     * the message routing info object.
     * 
     * @param routeInfo
     *            an instance of {@link MessageRoutingInfo} containing the
     *            routing data for a transaction.
     * @param payload
     *            the actual message
     * @return a generic response object
     * @throws MessageRoutingException
     *             The message router helper is not properly initialized.<br>
     *             <i>routeInfo</i> is invalid. <br>
     *             <i>routeInfo</i> does not contain a invalid transaction id. <Br>
     *             <i>payload</i> is invalid.
     */
    protected Object routeMessage(MessageRoutingInfo routeInfo, Serializable payload) {
        if (this.msgRouterHelper == null) {
            throw new MessageRoutingException("The message router helper is invalid");
        }
        if (routeInfo == null) {
            throw new MessageRoutingException("Message routing information is invalid");
        }
        if (RMT2String2.isEmpty(routeInfo.getMessageId())) {
            throw new MessageRoutingException("The message routing info oject does contain a valid transaction id");
        }
        if (payload == null) {
            throw new MessageRoutingException("The payload message is null or invalid");
        }
        return this.msgRouterHelper.routeJsonMessage(routeInfo, payload);
    }

    /**
     * Creates the header with application, module, and transaction values.
     * 
     * @param routeInfo
     *            instance of {@link MessageRoutingInfo}
     * @return {@link HeaderType}
     */
    protected HeaderType getHeader(MessageRoutingInfo routeInfo) {
        ObjectFactory f = new ObjectFactory();
        HeaderType header = f.createHeaderType();
        header.setApplication(this.application);
        header.setModule(this.module);
        header.setMessageMode("REQUEST");
        header.setDeliveryDate(RMT2Date.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        if (routeInfo != null) {
            header.setTransaction(routeInfo.getMessageId());
            header.setRouting(routeInfo.getRouterType() + ": " + routeInfo.getDestination());
            header.setDeliveryMode(routeInfo.getDeliveryMode());

        }
        return header;
    }
}
