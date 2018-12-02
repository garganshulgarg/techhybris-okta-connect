package com.techhybris.facades;

/**
 * The interface User association facade.
 */
public interface UserAssociationFacade {


    /**
     * Validate whether user exist in Hybris System or Not.
     *
     * @param userId the user id
     * @return the boolean
     */
    boolean validateUserExistence(String userId);
}
