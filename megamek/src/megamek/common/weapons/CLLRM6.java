/**
 * MegaMek - Copyright (C) 2005 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */
package megamek.common.weapons;

import megamek.common.TechAdvancement;

/**
 * @author Sebastian Brocks
 */
public class CLLRM6 extends LRMWeapon {

    /**
     *
     */
    private static final long serialVersionUID = -1358764761444243790L;

    /**
     *
     */
    public CLLRM6() {
        super();

        name = "LRM 6";
        setInternalName("CLLRM6");
        heat = 0;
        rackSize = 6;
        minimumRange = WEAPON_NA;
        tonnage = 1.2f;
        criticals = 0;
        bv = 69;
        // Per Herb all ProtoMech launcher use the ProtoMech Chassis progression.
        rulesRefs = "231, TM";

        techAdvancement.setTechBase(TechAdvancement.TECH_BASE_CLAN);
        techAdvancement.setClanAdvancement(3050, 3059, 3062);
        techAdvancement.setTechRating(RATING_F);
        techAdvancement.setAvailability( new int[] { RATING_X, RATING_X, RATING_F, RATING_D });
    }
}
