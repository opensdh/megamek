/**
 * MegaMek - Copyright (C) 2004,2005 Ben Mazur (bmazur@sev.org)
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
/*
 * Created on Sep 12, 2004
 *
 */
package megamek.common.weapons.battlearmor;

import megamek.common.TechAdvancement;
import megamek.common.weapons.PulseLaserWeapon;

/**
 * @author Jay Lawson
 */
public class ISBAMediumPulseLaser extends PulseLaserWeapon {
    /**
     *
     */
    private static final long serialVersionUID = 2676144961105838316L;

    /**
     *
     */
    public ISBAMediumPulseLaser() {
        super();
        name = "Medium Pulse Laser";
        setInternalName("ISBAMediumPulseLaser");
        addLookupName("IS BA Pulse Med Laser");
        addLookupName("IS BA Medium Pulse Laser");
        damage = 6;
        toHitModifier = -2;
        shortRange = 2;
        mediumRange = 4;
        longRange = 6;
        extremeRange = 8;
        waterShortRange = 2;
        waterMediumRange = 3;
        waterLongRange = 4;
        waterExtremeRange = 6;
        tonnage = 0.8f;
        criticals = 3;
        bv = 48;
        cost = 60000;
        flags = flags.or(F_BA_WEAPON).andNot(F_MECH_WEAPON).andNot(F_TANK_WEAPON).andNot(F_AERO_WEAPON).andNot(F_PROTO_WEAPON);
        shortAV = 6;
        maxRange = RANGE_SHORT;
        rulesRefs = "258, TM";
        techAdvancement.setTechBase(TechAdvancement.TECH_BASE_IS);
        techAdvancement.setISAdvancement(3052, 3060, 3062);
        techAdvancement.setTechRating(RATING_E);
        techAdvancement.setAvailability( new int[] { RATING_X, RATING_X, RATING_D, RATING_C });
    }
}
