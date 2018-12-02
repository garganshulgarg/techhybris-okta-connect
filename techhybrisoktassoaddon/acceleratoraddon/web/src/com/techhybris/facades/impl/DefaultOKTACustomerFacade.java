package com.techhybris.facades.impl;


import com.techhybris.integration.rest.services.OKTARESTServices;
import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.UserModel;

import javax.annotation.Resource;

public class DefaultOKTACustomerFacade extends DefaultCustomerFacade {


    @Resource(name = "oktaRestServices")
    private OKTARESTServices oktaRestServices;

    //TODO If we put AOP for it then it will perform great and less dependency will be injected.
    @Override
    public void register(final RegisterData registerData) throws DuplicateUidException {
        super.register(registerData);
        oktaRestServices.createActiveCustomerInOKTA(registerData, registerData.getPassword());
    }

    /**
     * Method updates password for customer.
     * This is updating password in OKTA too.
     * @param oldPassword string value.
     * @param newPassword string value.
     * @throws PasswordMismatchException exception.
     */
    @Override
    public void changePassword(final String oldPassword, final String newPassword) throws PasswordMismatchException {

        final UserModel currentUser = getCurrentUser();
        try {
            // OKTA Change Password Call.
            if (oktaRestServices.isCustomerExistsInOKTA(currentUser.getUid())) {
                oktaRestServices.changePasswordInOKTA(currentUser.getUid(), oldPassword, newPassword);
            } else {
                getCustomerAccountService().changePassword(currentUser, oldPassword, newPassword);
            }
        } catch (final de.hybris.platform.commerceservices.customer.PasswordMismatchException e) {
            throw new PasswordMismatchException(e);
        }
    }


}