package com.techhybris.integration.rest.services.impl;

import de.hybris.platform.commercefacades.user.data.RegisterData;
import com.techhybris.rest.integration.*;
import de.hybris.platform.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the OKTA specific REST Service Class.
 */
@SuppressWarnings("squid:S2221")
public class DefaultOKTARESTServices extends AbstractOKTARestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOKTARESTServices.class);

    private static final String OKTAFINDUSERURI = Config.getParameter("okta.techhybris.baseUrl") + "/api/v1/users/{userID}";
    private static final String OKTACHANGECREDENTIALS = OKTAFINDUSERURI + "/credentials/change_password";
    private static final String OKTACREATEACTIVEUSER = Config.getParameter("okta.techhybris.baseUrl") + "/api/v1/users?activate=true";
    private static final String USERIDCONSTANT = "userID";

    /**
     * Instantiates a new Default oktarest services.
     */
    public DefaultOKTARESTServices() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean setPasswordInOKTA(final String userID, final String password) {
        OKTASetPasswordRequestBody oktaSetPasswordRequestBody = createOKTASetPasswordRequestBodyObject(password);
        try {
            Map<String, String> params = new HashMap<>();
            params.put(USERIDCONSTANT, userID);
            HttpEntity<OKTASetPasswordRequestBody> requestEntity =
                    new HttpEntity<>(oktaSetPasswordRequestBody, getOKTASpecificAuthorizations());
            ResponseEntity<OKTASetPasswordRequestBody> responseEntity = createRestTemplateObject()
                    .exchange(OKTAFINDUSERURI, HttpMethod.PUT, requestEntity, OKTASetPasswordRequestBody.class, params);
            if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            LOGGER.error("setPasswordInOKTA is having some exception ", e);
        }
        return Boolean.FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean changePasswordInOKTA(final String userID, final String oldPassword, final String newPassword) {
        OKTAChangePasswordRequestBody oktaChangePasswordRequestBody =
                createOKTAChangePasswordRequestBodyObject(oldPassword, newPassword);
        try {
            Map<String, String> params = new HashMap<>();
            params.put(USERIDCONSTANT, userID);
            HttpEntity<OKTAChangePasswordRequestBody> requestEntity =
                    new HttpEntity<>(oktaChangePasswordRequestBody, getOKTASpecificAuthorizations());
            ResponseEntity<OKTAChangePasswordRequestBody> responseEntity = createRestTemplateObject()
                    .exchange(OKTACHANGECREDENTIALS, HttpMethod.POST, requestEntity, OKTAChangePasswordRequestBody.class,
                            params);
            if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            LOGGER.error("changePasswordInOKTA is having some exception ", e);
        }
        return Boolean.FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isCustomerExistsInOKTA(final String userID) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put(USERIDCONSTANT, userID);
            HttpEntity<?> requestEntity = new HttpEntity<>(getOKTASpecificAuthorizations());
            ResponseEntity<String> responseEntity =
                    createRestTemplateObject().exchange(OKTAFINDUSERURI, HttpMethod.GET, requestEntity, String.class, params);
            if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            LOGGER.error("isCustomerExistsInOKTA is having some exception ", e);
        }
        return Boolean.FALSE;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean createActiveCustomerInOKTA(RegisterData registerData, String password) {
        OKTACreateActivatedUserWithPasswordRequestBody oktaCreateActivatedUserWithPasswordRequestBody =
                createOktaActivatedUserWithPasswordRequestBody(registerData, password);
        try {
            Map<String, String> params = new HashMap<>();
            HttpEntity<OKTACreateActivatedUserWithPasswordRequestBody> requestEntity =
                    new HttpEntity<>(oktaCreateActivatedUserWithPasswordRequestBody, getOKTASpecificAuthorizations());
            ResponseEntity<OKTACreateActivatedUserWithPasswordRequestBody> responseEntity = createRestTemplateObject()
                    .exchange(OKTACREATEACTIVEUSER, HttpMethod.POST, requestEntity, OKTACreateActivatedUserWithPasswordRequestBody.class,
                            params);
            if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            LOGGER.error("createActiveCustomerInOKTA is having some exception ", e);
        }
        return Boolean.FALSE;
    }

    // TODO Move this call to respective converters and populators.
    private OKTASetPasswordRequestBody createOKTASetPasswordRequestBodyObject(final String pwd) {

        OKTASetPasswordRequestBody oktaSetPasswordRequestBody = new OKTASetPasswordRequestBody();
        Credentials credentials = new Credentials();
        Password password = new Password();
        password.setValue(pwd);
        credentials.setPassword(password);
        oktaSetPasswordRequestBody.setCredentials(credentials);
        return oktaSetPasswordRequestBody;

    }

    // TODO Move this call to respective converters and populators.
    private OKTAChangePasswordRequestBody createOKTAChangePasswordRequestBodyObject(String oldPwd, String newPwd) {
        OKTAChangePasswordRequestBody oktaChangePasswordRequestBody = new OKTAChangePasswordRequestBody();
        Password oldPassword = new Password();
        oldPassword.setValue(oldPwd);
        Password newPassword = new Password();
        newPassword.setValue(newPwd);
        oktaChangePasswordRequestBody.setOldPassword(oldPassword);
        oktaChangePasswordRequestBody.setNewPassword(newPassword);
        return oktaChangePasswordRequestBody;
    }

    // TODO Move this call to respective converters and populators.
    private OKTACreateActivatedUserWithPasswordRequestBody createOktaActivatedUserWithPasswordRequestBody(RegisterData registerData, String pwd) {
        OKTACreateActivatedUserWithPasswordRequestBody oktaCreateActivatedUserWithPasswordRequestBody = new OKTACreateActivatedUserWithPasswordRequestBody();
        // Setting up Credentials
        Credentials credentials = new Credentials();
        Password password = new Password();
        password.setValue(pwd);
        credentials.setPassword(password);

        // Setting up user Profile information.
        OKTAUserProfileRequestBody oktaUserProfileRequestBody = new OKTAUserProfileRequestBody();
        oktaUserProfileRequestBody.setFirstName(registerData.getFirstName());
        oktaUserProfileRequestBody.setLastName(registerData.getLastName());
        oktaUserProfileRequestBody.setEmail(registerData.getLogin());
        oktaUserProfileRequestBody.setLogin(registerData.getLogin());


        oktaCreateActivatedUserWithPasswordRequestBody.setCredentials(credentials);
        oktaCreateActivatedUserWithPasswordRequestBody.setProfile(oktaUserProfileRequestBody);

        return oktaCreateActivatedUserWithPasswordRequestBody;
    }

}