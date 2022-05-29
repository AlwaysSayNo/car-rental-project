package com.epam.nazar.grinko.domians.helpers;

public enum CarStatus {

    /**
     * The car is in current moment rented / used by user.
     * <ul>
     *     <li>Cannot be rented by another user</li>
     *     <li>Cannot be modified/deleted by admin</li>
     *     <li>The manager can stop the rental and issue an invoice for repairs</li>
     * </ul>
     */
    RENTED,
    /**
     * The car isn`t in current moment rented / is in the garage.
     * <ul>
     *     <li>Can be rented by another user</li>
     *     <li>Cannot be modified/deleted by admin</li>
     * </ul>
     */
    NOT_RENTED,
    /**
     * The car isn`t in the public domain. Intermediate stage between RENTED and NOT_RENTED
     * <ul>
     *     <li>Cannot be rented by another user</li>
     *     <li>Can be modified/deleted by admin</li>
     *     <li>Status can be changed by admin to ON_HOLD</li>
     * </ul>
     */
    ON_PROCESSING,
    /**
     * The car isn`t in the public domain.
     * <ul>
     *     <li>Cannot be rented by another user</li>
     *     <li>Can be modified/deleted by admin</li>
     * </ul>
     */
    ON_HOLD

}
