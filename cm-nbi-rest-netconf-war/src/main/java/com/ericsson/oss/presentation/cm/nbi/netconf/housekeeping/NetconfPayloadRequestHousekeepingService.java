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

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.presentation.cm.nbi.netconf.common.FileResource;
import com.ericsson.oss.presentation.cm.nbi.netconf.common.NetconfPayloadConstants;
import com.ericsson.oss.presentation.cm.nbi.netconf.persistence.NetconfPayloadPersistenceService;
import com.ericsson.oss.services.cm.nbi.netconf.NetconfPayloadRequestResult;

@Stateless
public class NetconfPayloadRequestHousekeepingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetconfPayloadRequestHousekeepingService.class);

    @Inject
    NetconfPayloadPersistenceService persistenceService;

    @Inject
    private FileResource fileResource;

    /**
     * This method deletes the CmNbiNetconfRequest POs and files, whose status is COMPLETED and are 24hours old.
     */
    public void housekeepingCmNbiNetconfRequestPOs() {
        final Iterator<PersistenceObject> listOfNetConfRequestPOToDelete = persistenceService.getAllNetconfPayloadRequestPOs();

        List<PersistenceObject> poList = new ArrayList<>();
        listOfNetConfRequestPOToDelete.forEachRemaining(poList::add);

        final List<PersistenceObject> poIDsToDelete = poList.stream()
                .filter(netconfRequestPO -> netconfRequestPO.getCreatedTime().before(new DateTime().minusHours(NetconfPayloadConstants.TWENTY_FOUR_HOURS).toDate())).collect(Collectors.toList());
        LOGGER.info("Total number of POs to delete: {}", poIDsToDelete.size());

        final List<PersistenceObject> filePathsToDelete = poIDsToDelete.stream()
                .filter(netconfRequestPO -> netconfRequestPO.getAttribute(NetconfPayloadConstants.RESULT) != null
                        && netconfRequestPO.getAttribute(NetconfPayloadConstants.RESULT).equals(NetconfPayloadRequestResult.SUCCESS.toString()))
                .collect(Collectors.toList());

        deleteFiles(filePathsToDelete);
        poIDsToDelete.forEach(netconfRequestPO -> persistenceService.deletePO(netconfRequestPO));
        LOGGER.info("Housekeeping of Netconf request POs completed successfully");
    }

    private void deleteFiles(final List<PersistenceObject> filePathsToDelete) {
        for (PersistenceObject netconfRequestPO : filePathsToDelete) {
            Map<String, Object> additionalInfo = netconfRequestPO.getAttribute(NetconfPayloadConstants.ADDITIONAL_INFO);
            Object getConfigResponseFilePath = additionalInfo != null ? additionalInfo.get(NetconfPayloadConstants.FILE_PATH) : null;
            if (getConfigResponseFilePath != null) {
		LOGGER.info("getConfigResponseFilePath to be deleted: {}", getConfigResponseFilePath);
                fileResource.deleteFile(((String) getConfigResponseFilePath));
            }
        }

    }
}
