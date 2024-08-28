/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.presentation.cm.nbi.netconf.housekeeping;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.presentation.cm.nbi.netconf.common.NetconfPayloadConstants;

/**
 * This class responsibility is to run CmNbiNetconfRequest PO Housekeeping. It runs once daily and deletes the CmNbiNetconfRequest POs, whose status is COMPLETED and are 24hours old.
 *
 * @author zjaimee
 *
 */
@Startup
@Singleton
public class NetconfPayloadRequestHousekeepingTimer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetconfPayloadRequestHousekeepingTimer.class);

    @Resource
    private TimerService timerService;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private NetconfPayloadRequestHousekeepingService netconfPayloadRequestHousekeepingService;

    @Inject
    private NetconfClusterMembershipListener netconfClusterMembershipListener;

    /**
     * The default method that will be invoked by EJB context.
     *
     */
    @PostConstruct
    public void initTimer() {
        final LocalTime scheduledTime = LocalTime.parse(NetconfPayloadConstants.DEFAULT_HOUSEKEEPING_SCHEDULE_TIME);
        final long timerStartTime = System.currentTimeMillis();
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        timerConfig.setInfo(NetconfPayloadConstants.NETCONF_PAYLOAD_REQUEST_PO_HOUSEKEEPING_TIMER);
        timerService.createCalendarTimer(new ScheduleExpression().hour(scheduledTime.getHour()).minute(scheduledTime.getMinute()), timerConfig);
	LOGGER.info("Netconf Payload Request PO Housekeeping timer has started");
        recordEvent(NetconfPayloadConstants.TIME_TAKEN, String.valueOf(System.currentTimeMillis() - timerStartTime));
    }

    /**
     * Upon Timer expiry this method would trigger CmNbiNetconfRequest PO Housekeeping.
     */
    @Timeout
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void housekeepingSoftwarePackages() {
	LOGGER.info("Netconf Payload Request PO Housekeeping timer has timed-out. isMasterInstance : {}", netconfClusterMembershipListener.isMasterInstance());
        if (netconfClusterMembershipListener.isMasterInstance()) {
            try {
                netconfPayloadRequestHousekeepingService.housekeepingCmNbiNetconfRequestPOs();
            } catch (final Exception ex) {
                LOGGER.error("Caught Exception while initiating the Software Package Housekeeping. Exception occured : ", ex);
                recordError(NetconfPayloadConstants.NETCONF_PAYLOAD_REQUEST_PO_HOUSEKEEPING, String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()), ex.getMessage());
            }
        }
    }

    private void recordEvent(final String key, final String value) {
        final Map<String, Object> eventData = new HashMap<>();
        final String CLASS_NAME = "ClassName";
        eventData.put(CLASS_NAME, getClass().getSimpleName());
        eventData.put(key, value);
        systemRecorder.recordEventData(NetconfPayloadConstants.NETCONF_PAYLOAD_REQUEST_PO_HOUSEKEEPING, eventData);
    }

    private void recordError(final String eventKey, final String errorCode, final String errorMsg) {
	systemRecorder.recordError(eventKey, ErrorSeverity.NOTICE, NetconfPayloadConstants.NETCONF_PAYLOAD_REQUEST_PO_HOUSEKEEPING, errorCode, errorMsg);
    }

}
