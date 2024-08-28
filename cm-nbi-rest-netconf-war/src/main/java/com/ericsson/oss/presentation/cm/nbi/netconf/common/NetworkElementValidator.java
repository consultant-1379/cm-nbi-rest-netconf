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

package com.ericsson.oss.presentation.cm.nbi.netconf.common;

import java.util.Arrays;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.modeling.modelservice.ModelService;
import com.ericsson.oss.itpf.modeling.modelservice.typed.TypedModelAccess;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.presentation.cm.nbi.netconf.enums.NetconfPayloadSupportedPlatforms;
import com.ericsson.oss.presentation.cm.nbi.netconf.exceptions.NetconfNotSupportedException;
import com.ericsson.oss.presentation.cm.nbi.netconf.exceptions.NetworkElementNotFoundException;
import com.ericsson.oss.presentation.cm.nbi.netconf.persistence.NetconfPayloadPersistenceService;

@Stateless
public class NetworkElementValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkElementValidator.class);

    @Inject
    private ModelService modelService;

    @Inject
    private NetconfPayloadPersistenceService netconfPayloadPersistenceService;

    @Inject
    private TypedModelAccess modelAccess;

    public static final String CATEGORY = "category";
    public static final String TYPE = "type";
    public static final String PLATFORM_TYPE = "platformType";
    public static final String COM_CONNECTIVITY_INFORMATION = "ComConnectivityInformation";

    public boolean validateNetworkElement(final String networkElementName) throws NetconfNotSupportedException, NetworkElementNotFoundException {

        ManagedObject networkElementMo = netconfPayloadPersistenceService.getNetworkElementMo(networkElementName);

        if (networkElementMo != null) {
            LOGGER.debug("Attributes received for Network Element : {} are :  {}, Target : {}", networkElementName, networkElementMo.getAllAttributes(), networkElementMo.getTarget());
            Map<String, Object> moAttributes = networkElementMo.getAllAttributes();
            String platformType = (String) moAttributes.get(PLATFORM_TYPE);
            if (isNetworkElementNetconfCapable(networkElementMo, platformType, networkElementName)) {
                return true;
            } else {
                throw new NetconfNotSupportedException("Network Element provided doesn’t support Netconf protocol.");
            }
        } else {
            throw new NetworkElementNotFoundException("Network Element does not exist.");
        }
    }

    private boolean isNetworkElementNetconfCapable(final ManagedObject networkElementMo, final String platform, final String networkElementName) {

        if (platform != null) {
            return isExistInSupportedPlatforms(platform);
        } else {

            final PersistenceObject target = networkElementMo.getTarget();
            modelAccess = modelService.getTypedAccess();
            TargetTypeInformation targetTypeInformation = modelAccess.getModelInformation(TargetTypeInformation.class);
            final String connectivityInformation = targetTypeInformation.getConnectivityInfoMoType(target.getAttribute(CATEGORY), target.getAttribute(TYPE));
            LOGGER.debug("Connectivity Information received for Network Element : {} is : {}, Target Attributes : {}  ", networkElementName, connectivityInformation, target.getAllAttributes());

            if (connectivityInformation.contains(COM_CONNECTIVITY_INFORMATION)) {
                return true;
            } else {
                String nePlatform = targetTypeInformation.getPlatform(target.getAttribute(CATEGORY), target.getAttribute(TYPE));
                LOGGER.debug("Platform received for Network Element : {} is : {}, Target Attributes : {}  ", networkElementName, nePlatform, target.getAllAttributes());
                return isExistInSupportedPlatforms(nePlatform);
            }
        }
    }

    private boolean isExistInSupportedPlatforms(final String platformType) {
        return Arrays.stream(NetconfPayloadSupportedPlatforms.values()).anyMatch(supportedPlatformTypes -> supportedPlatformTypes.getPlatformType().equalsIgnoreCase(platformType));
    }
}
