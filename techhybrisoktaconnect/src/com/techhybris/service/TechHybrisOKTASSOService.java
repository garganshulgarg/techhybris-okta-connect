package com.techhybris.service;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.samlsinglesignon.DefaultSSOService;
import de.hybris.platform.samlsinglesignon.constants.SamlsinglesignonConstants;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;
import java.util.stream.Collectors;

public class TechHybrisOKTASSOService extends DefaultSSOService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TechHybrisOKTASSOService.class);

    private ModelService modelService;

    private UserService userService;

    /**
     * Instantiates a new techoktaoktasso service.
     */
    public TechHybrisOKTASSOService() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserModel getOrCreateSSOUser(final String id, final String name, final Collection<String> roles) {

        roles.add("customergroup");

        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("User info must not be empty");
        }
        final OKTASSOUserMapping userMapping = findOKTAMapping(roles);

        if (userMapping != null) {

            UserModel user = lookupExisting(id, userMapping);
            if (user == null) {
                user = createNewUser(id, name, userMapping);
                adjustUserAttributes(user, userMapping);
            }

            modelService.save(user);

            return user;
        } else {
            throw new IllegalArgumentException(
                    "No SSO user mapping available for roles " + roles + " - cannot accept user " + id);
        }
    }

    /**
     * create a new user.
     *
     * @param id          to be used as the user Id
     * @param name        name of the user
     * @param userMapping user mappings (groups and user type)
     * @return a new user model
     */
    protected UserModel createNewUser(final String id, final String name, final OKTASSOUserMapping userMapping) {
        UserModel user;
        user = modelService.create(userMapping.type);
        user.setUid(id);
        user.setName(name);

        final String defaultPasswordEncoder = StringUtils
                .defaultIfEmpty(Config.getParameter(SamlsinglesignonConstants.SSO_PASSWORD_ENCODING),
                        SamlsinglesignonConstants.MD5_PASSWORD_ENCODING);
        //should be default password but the token is encoded with md5
        userService.setPassword(user, UUID.randomUUID().toString(), defaultPasswordEncoder);
        return user;
    }

    /**
     * Check if a user exists or not.
     *
     * @param id      the user id to search for
     * @param mapping groups/user type
     * @return return user model in case the user is found or null if not found
     */
    protected UserModel lookupExisting(final String id, final OKTASSOUserMapping mapping) {
        try {
            return userService.getUserForUID(id);
        } catch (final UnknownIdentifierException e) {
            LOGGER.error("User Not Found " + id + " ", e);
            return null;
        }
    }

    /**
     * Find okta mapping oktasso user mapping.
     *
     * @param roles the roles
     * @return the oktasso user mapping
     */
    protected OKTASSOUserMapping findOKTAMapping(final Collection<String> roles) {
        OKTASSOUserMapping mergedMapping = null;
        for (final String role : roles) {
            final OKTASSOUserMapping mapping = getMappingForOKTARole(role);
            if (mapping != null) {
                if (mergedMapping == null) {
                    mergedMapping = new OKTASSOUserMapping();
                    mergedMapping.type = mapping.type;
                }
                if (mapping.type.equals(mergedMapping.type)) {
                    mergedMapping.groups.addAll(mapping.groups);
                } else {
                    throw new IllegalArgumentException(
                            "SSO user cannot be configured due to ambigous type mappings (roles: " + roles + ")");
                }
            }
        }
        return mergedMapping;
    }

    /**
     * getting the mapping for roles.
     *
     * @param role the role to get the mapping for
     * @return SSO user mapping object which has the user type and the groups
     */
    protected OKTASSOUserMapping getMappingForOKTARole(final String role) {
        final Map<String, String> params = Registry.getCurrentTenantNoFallback().getConfig()
                .getParametersMatching("sso\\.mapping\\." + role + "\\.(.*)", true);
        if (MapUtils.isNotEmpty(params)) {
            final OKTASSOUserMapping mapping = new OKTASSOUserMapping();
            mapping.type = params.get("usertype");
            mapping.groups = new LinkedHashSet<>(Arrays.asList(params.get("groups").split(",; ")));

            return mapping;
        }
        return null;
    }

    /**
     * Adjusting user groups.
     *
     * @param user    the user to adjust the groups for
     * @param mapping the mapping which holds the groups
     */
    protected void adjustUserAttributes(final UserModel user, final OKTASSOUserMapping mapping) {
        user.setGroups(mapping.groups.stream().map(it -> userService.getUserGroupForUID(it)).collect(Collectors.toSet()));
    }

    /**
     * set Model Service.
     *
     * @param modelService modelService
     */
    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * set user service.
     *
     * @param userService userService
     */
    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    /**
     * The type Oktasso user mapping.
     */
    protected static class OKTASSOUserMapping {
        /**
         * The Type.
         */
        protected String type;
        /**
         * The Groups.
         */
        private Collection<String> groups = new LinkedHashSet<>();
    }
}
