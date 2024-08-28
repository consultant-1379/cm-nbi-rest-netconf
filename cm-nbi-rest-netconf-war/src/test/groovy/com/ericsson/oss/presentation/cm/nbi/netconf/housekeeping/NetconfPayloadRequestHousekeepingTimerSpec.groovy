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
package com.ericsson.oss.presentation.cm.nbi.netconf.housekeeping

import java.time.LocalTime

import javax.ejb.*

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder
import com.ericsson.oss.presentation.cm.nbi.netconf.common.NetconfPayloadConstants

import spock.lang.Unroll

public class NetconfPayloadRequestHousekeepingTimerSpec extends CdiSpecification {

    @ObjectUnderTest
    private NetconfPayloadRequestHousekeepingTimer netconfPayloadRequestHousekeepingTimer;

    @MockedImplementation
    private TimerService timerService;

    @MockedImplementation
    private NetconfClusterMembershipListener netconfClusterMembershipListener;

    @MockedImplementation
    private Timer mockTimer;

    @MockedImplementation
    private NetconfPayloadRequestHousekeepingService netconfPayloadRequestHousekeepingService;

    @MockedImplementation
    private SystemRecorder systemRecorder;

    @Unroll
    def "Test Case to create the NetconfPayloadRequestHousekeepingTimer"() {
        given: "Scheduler times up and housekeeping triggered"
        netconfClusterMembershipListener.isMasterInstance() >> true;
        final LocalTime scheduledTime = LocalTime.parse(NetconfPayloadConstants.DEFAULT_HOUSEKEEPING_SCHEDULE_TIME);
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo("NetconfPayloadRequestHousekeepingTimer");
        timerConfig.setPersistent(false)
        mockTimer.getInfo() >> timerConfig.getInfo()
        timerService.getTimers() >> Arrays.asList(mockTimer)
        timerService.createCalendarTimer(new ScheduleExpression().hour(scheduledTime.getHour()).minute(scheduledTime.getMinute()), timerConfig);
        when: " Starting the NetconfPayloadRequestHousekeepingTimer "
        netconfPayloadRequestHousekeepingTimer.initTimer()
        netconfPayloadRequestHousekeepingTimer.housekeepingSoftwarePackages()
        then: "Created the NetconfPayloadRequestHousekeepingTimer and started housekeeping"
        1*netconfPayloadRequestHousekeepingService.housekeepingCmNbiNetconfRequestPOs()
    }

    @Unroll
    def "Test Case to check housekeeping not triggered if not a Master instance"() {
        given: "Scheduler times up and housekeeping triggered"
        netconfClusterMembershipListener.isMasterInstance() >> false;
        final LocalTime scheduledTime = LocalTime.parse(NetconfPayloadConstants.DEFAULT_HOUSEKEEPING_SCHEDULE_TIME);
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo("NetconfPayloadRequestHousekeepingTimer");
        timerConfig.setPersistent(false)
        mockTimer.getInfo() >> timerConfig.getInfo()
        timerService.getTimers() >> Arrays.asList(mockTimer)
        timerService.createCalendarTimer(new ScheduleExpression().hour(scheduledTime.getHour()).minute(scheduledTime.getMinute()), timerConfig);
        when: " Starting the NetconfPayloadRequestHousekeepingTimer "
        netconfPayloadRequestHousekeepingTimer.initTimer()
        netconfPayloadRequestHousekeepingTimer.housekeepingSoftwarePackages()
        then: "Created the NetconfPayloadRequestHousekeepingTimer but housekeeping not started"
        0*netconfPayloadRequestHousekeepingService.housekeepingCmNbiNetconfRequestPOs()
    }

    @Unroll
    def "Test Case to recordError when exception thrown"() {
        given: "Scheduler times up and housekeeping triggered"
        netconfClusterMembershipListener.isMasterInstance() >> true;
        final LocalTime scheduledTime = LocalTime.parse(NetconfPayloadConstants.DEFAULT_HOUSEKEEPING_SCHEDULE_TIME);
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo("NetconfPayloadRequestHousekeepingTimer");
        timerConfig.setPersistent(false)
        mockTimer.getInfo() >> timerConfig.getInfo()
        timerService.getTimers() >> Arrays.asList(mockTimer)
        netconfPayloadRequestHousekeepingService.housekeepingCmNbiNetconfRequestPOs() >> { throw new Exception("Dummy Exception") }
        timerService.createCalendarTimer(new ScheduleExpression().hour(scheduledTime.getHour()).minute(scheduledTime.getMinute()), timerConfig);
        when: " Starting the NetconfPayloadRequestHousekeepingTimer "
        netconfPayloadRequestHousekeepingTimer.initTimer()
        netconfPayloadRequestHousekeepingTimer.housekeepingSoftwarePackages()
        then: "Created the NetconfPayloadRequestHousekeepingTimer and error recorded"
        1*systemRecorder.recordError(_, _, _, _, _)
    }
}
