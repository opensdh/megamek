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
package megamek.common.weapons.battlearmor;

import megamek.common.TechAdvancement;
import megamek.common.weapons.AdvancedSRMWeapon;
/**
 * @author Sebastian Brocks
 */
public class CLAdvancedSRM4 extends AdvancedSRMWeapon {

    /**
     *
     */
    private static final long serialVersionUID = 1382352551382640865L;

    /**
     *
     */
    public CLAdvancedSRM4() {
        super();
        name = "Advanced SRM 4";
        setInternalName("CLAdvancedSRM4");
        addLookupName("Clan Advanced SRM-4");
        addLookupName("Clan Advanced SRM 4");
        rackSize = 4;
        shortRange = 4;
        mediumRange = 8;
        longRange = 12;
        extremeRange = 16;
        bv = 60;
        flags = flags.or(F_NO_FIRES).or(F_BA_WEAPON).andNot(F_MECH_WEAPON).andNot(F_TANK_WEAPON).andNot(F_AERO_WEAPON).andNot(F_PROTO_WEAPON);
        tonnage = .15f;
        criticals = 3;
        cost = 60000;
        techAdvancement.setTechBase(TechAdvancement.TECH_BASE_CLAN);
        techAdvancement.setClanAdvancement(3047, 3056, 3062);
        techAdvancement.setTechRating(RATING_F);
        techAdvancement.setAvailability( new int[] { RATING_X, RATING_X, RATING_F, RATING_D });
    }
}
