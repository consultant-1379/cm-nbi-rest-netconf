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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.cluster.MembershipChangeEvent;
import com.ericsson.oss.itpf.sdk.cluster.annotation.ServiceCluster;

@ApplicationScoped
public class NetconfClusterMembershipListener {
    private boolean isMaster = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(NetconfClusterMembershipListener.class);

    public void listenForMembershipChange(@Observes @ServiceCluster("CmNbiNetconfPayloadCluster") final MembershipChangeEvent changeEvent) {
        if (changeEvent.isMaster()) {
            LOGGER.info("Received membership change event [{}], setting current EventListener instance to master", changeEvent.isMaster());
            isMaster = true;
        } else {
            LOGGER.info("Received membership change event [{}], setting current EventListener instance to redundant", changeEvent.isMaster());
            isMaster = false;
        }
    }

    /**
     * @return boolean state of current NetconfClusterMembershipListener Service instance
     */
    public boolean isMasterInstance() {
        return isMaster;
    }

}
