package com.techhybris.facades.impl;

import com.techhybris.facades.UserAssociationFacade;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * The type Default user association facade.
 */
public class DefaultUserAssociationFacade implements UserAssociationFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserAssociationFacade.class);

    @Resource(name = "userService")
    private UserService userService;

    public DefaultUserAssociationFacade() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateUserExistence(final String userId) {
        try {
            userService.getUserForUID(userId);
            return true;
        } catch (UnknownIdentifierException e) {
            LOGGER.error("User with UserID " + userId + " not exists", e);
            return false;
        }
    }
}
