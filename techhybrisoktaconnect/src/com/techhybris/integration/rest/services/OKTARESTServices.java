package com.techhybris.integration.rest.services;

import de.hybris.platform.commercefacades.user.data.RegisterData;

/**
 * The interface Oktarest services.
 */
public interface OKTARESTServices {

    /**
     * Sets password in okta.
     *
     * @param userID   the user id
     * @param password the password
     * @return the password in okta
     */
    Boolean setPasswordInOKTA(final String userID, final String password);

    /**
     * Change password in okta boolean.
     *
     * @param userID      the user id
     * @param oldPassword the old password
     * @param newPassword the new password
     * @return the boolean
     */
    Boolean changePasswordInOKTA(final String userID, final String oldPassword, final String newPassword);

    /**
     * Is customer exists in okta boolean.
     *
     * @param userID the user id
     * @return the boolean
     */
    Boolean isCustomerExistsInOKTA(final String userID);

    /**
     * Following method registers the user with Password in Active State in OKTA.
     *
     * @param registerData
     * @param password
     * @return
     */
    Boolean createActiveCustomerInOKTA(final RegisterData registerData, final String password);

}