package com.techhybris.service.impl;

import com.techhybris.integration.rest.services.OKTARESTServices;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.core.model.user.CustomerModel;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.util.Date;

public class DefaultOKTACustomerAccountService extends DefaultCustomerAccountService {

    private OKTARESTServices oktaRestServices;

    /**
     * This method is overriden to set password forcefully in OKTA at the time of Password Reset via Email.
     * @param token
     * @param newPassword
     * @throws TokenInvalidatedException
     */
    @Override
    public void updatePassword(final String token, final String newPassword) throws TokenInvalidatedException {
        Assert.hasText(token, "The field [token] cannot be empty");
        Assert.hasText(newPassword, "The field [newPassword] cannot be empty");

        final SecureToken data = getSecureTokenService().decryptData(token);
        if (getTokenValiditySeconds() > 0L) {
            final long delta = new Date().getTime() - data.getTimeStamp();
            if (delta / 1000 > getTokenValiditySeconds()) {
                throw new IllegalArgumentException("token expired");
            }
        }

        final CustomerModel customer = getUserService().getUserForUID(data.getData(), CustomerModel.class);
        if (customer == null) {
            throw new IllegalArgumentException("user for token not found");
        }
        if (!token.equals(customer.getToken())) {
            throw new TokenInvalidatedException();
        }
        customer.setToken(null);
        customer.setLoginDisabled(false);
        getModelService().save(customer);

        if (oktaRestServices.isCustomerExistsInOKTA(customer.getUid())) {
            oktaRestServices.setPasswordInOKTA(customer.getUid(), newPassword);
        } else {
            getUserService().setPassword(data.getData(), newPassword, getPasswordEncoding());
        }
    }


    @Required
    public void setOktaRestServices(OKTARESTServices oktaRestServices) {
        this.oktaRestServices = oktaRestServices;
    }
}
