package com.techhybris.security;

import de.hybris.platform.acceleratorstorefrontcommons.security.AbstractAcceleratorAuthenticationProvider;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloConnection;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.spring.security.CoreUserDetails;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

/**
 * The role of this provider is to login to Hybris System with out Password. But Prerequisite for same is user should
 * get successfully authenticated using OKTA.
 *
 * @author anshul.
 */
public class OktaLoginAuthenticationProvider extends AbstractAcceleratorAuthenticationProvider {
    private static final Logger LOGGER = Logger.getLogger(OktaLoginAuthenticationProvider.class);

    /**
     * Instantiates a new Okta login authentication provider.
     */
    public OktaLoginAuthenticationProvider() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        // For OKTA Login we are setting isAuthenticated Flag to true. If it is Hybris Specific Login then calling Hybris
        // Default Provider Authenticated method.
        if (!authentication.isAuthenticated()) {
            return super.authenticate(authentication);
        }
        final String username = getUserNameFromAuthentication(authentication);
        if (getBruteForceAttackCounter().isAttack(username)) {
            try {
                final UserModel userModel = getUserService().getUserForUID(StringUtils.lowerCase(username));
                userModel.setLoginDisabled(true);
                getModelService().save(userModel);
                getBruteForceAttackCounter().resetUserCounter(userModel.getUid());
            } catch (final UnknownIdentifierException e) {
                LOGGER.warn("Brute force attack attempt for non existing user name " + username, e);
            }

            throw new BadCredentialsException(
                    messages.getMessage("CoreAuthenticationProvider.badCredentials", "Bad credentials"));

        }

        return coreHybrisAuthenticationProvider(authentication);

    }

    /**
     * Login the User into Hybris System.
     *
     * @param authentication Provides the authentication parameter.
     * @return Returns the Authentication
     */
    private Authentication coreHybrisAuthenticationProvider(final Authentication authentication) {
        if (Registry.hasCurrentTenant() && JaloConnection.getInstance().isSystemInitialized()) {
            String username = getUserNameFromAuthentication(authentication);
            UserDetails userDetails = null;

            try {
                userDetails = this.retrieveUser(username);
            } catch (final UsernameNotFoundException arg5) {
                throw new BadCredentialsException(
                        this.messages.getMessage("CoreAuthenticationProvider.badCredentials", "Bad credentials"), arg5);
            }

            this.getPreAuthenticationChecks().check(userDetails);
            final User user = UserManager.getInstance().getUserByLogin(userDetails.getUsername());
            JaloSession.getCurrentSession().setUser(user);
            return this.createSuccessAuthentication(authentication, userDetails);
        } else {
            return this.createSuccessAuthentication(authentication,
                    new CoreUserDetails("systemNotInitialized", "systemNotInitialized", true, false, true, true,
                            Collections.emptyList(), (String) null));
        }
    }

    private String getUserNameFromAuthentication(final Authentication authentication) {
        if (null != authentication.getPrincipal()) {
            return authentication.getName();
        }
        return "NONE_PROVIDED";
    }
}
