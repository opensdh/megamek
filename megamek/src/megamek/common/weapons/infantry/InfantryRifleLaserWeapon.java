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
 * Created on Sep 7, 2005
 *
 */
package megamek.common.weapons.infantry;

import megamek.common.TechAdvancement;

/**
 * @author Sebastian Brocks
 */
public class InfantryRifleLaserWeapon extends InfantryWeapon {

    /**
     *
     */
    private static final long serialVersionUID = -9065123199493897216L;

    public InfantryRifleLaserWeapon() {
        super();

        name = "Laser Rifle";
        setInternalName(name);
        addLookupName("InfantryLaserRifle");
        cost = 1250;
        bv = 1.43;
        flags = flags.or(F_NO_FIRES).or(F_DIRECT_FIRE).or(F_LASER).or(F_ENERGY);
        infantryDamage = 0.28;
        infantryRange = 2;
        rulesRefs =" 273, TM";
        techAdvancement.setTechBase(TechAdvancement.TECH_BASE_ALL);
        techAdvancement.setAdvancement(DATE_NONE, DATE_NONE, 2100);
        techAdvancement.setTechRating(RATING_D);
        techAdvancement.setAvailability( new int[] { RATING_C, RATING_B, RATING_B, RATING_B });
    }
}
