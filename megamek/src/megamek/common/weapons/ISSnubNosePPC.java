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
 * Created on Sep 13, 2004
 *
 */
package megamek.common.weapons;

import megamek.common.TechAdvancement;

/**
 * @author Andrew Hunter
 */
public class ISSnubNosePPC extends PPCWeapon {
    /**
     *
     */
    private static final long serialVersionUID = -5650794792475465261L;

    /**
     *
     */
    public ISSnubNosePPC() {
        super();

        name = "Snub-Nose PPC";
        setInternalName("ISSNPPC");
        addLookupName("ISSnubNosedPPC");
        heat = 10;
        damage = DAMAGE_VARIABLE;
        minimumRange = 0;
        shortRange = 9;
        mediumRange = 13;
        longRange = 15;
        extremeRange = 22;
        waterShortRange = 6;
        waterMediumRange = 8;
        waterLongRange = 9;
        waterExtremeRange = 13;
        damageShort = 10;
        damageMedium = 8;
        damageLong = 5;
        tonnage = 6.0f;
        criticals = 2;
        bv = 165;
        cost = 300000;
        maxRange = RANGE_MED;
        shortAV = 10;
        medAV = 8;
        // with a capacitor
        explosive = true;
        techAdvancement.setTechBase(TechAdvancement.TECH_BASE_IS);
        techAdvancement.setISAdvancement(2784, DATE_NONE, 3067, 2790, 3067);
        techAdvancement.setTechRating(RATING_E);
        techAdvancement.setAvailability( new int[] { RATING_E, RATING_F, RATING_E, RATING_X });
    }

    @Override
    public int getDamage(int range) {
        if (range <= shortRange) {
            return damageShort;
        }

        if (range <= mediumRange) {
            return damageMedium;
        }

        return damageLong;
    }

}
