/*
 * MegaMek - Copyright (C) 2000-2005 Ben Mazur
 * (bmazur@sev.org)
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
package megamek.common;

import junit.framework.TestCase;
import megamek.common.options.GameOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

/**
 * @author Deric "Netzilla" Page (deric dot page at usa dot net)
 * @since 10/30/13 9:25 AM
 */
@RunWith(JUnit4.class)
public class CrewTest {

    @Test
    public void testGetBVSkillMultiplier() {
        int gunnery = 4;
        int piloting = 5;

        // Test the default case.
        Game mockGame = null;
        double expected = 1.0;
        double actual = Crew.getBVSkillMultiplier(gunnery, piloting, mockGame);
        TestCase.assertEquals(expected, actual, 0.001);

        mockGame = Mockito.mock(Game.class);
        GameOptions mockOptions = Mockito.mock(GameOptions.class);
        Mockito.when(mockGame.getOptions()).thenReturn(mockOptions);
        expected = 1.0;
        actual = Crew.getBVSkillMultiplier(gunnery, piloting, mockGame);
        TestCase.assertEquals(expected, actual, 0.001);

        // Test a 3/4 pilot.
        gunnery = 3;
        piloting = 4;
        Mockito.when(mockOptions.booleanOption(Mockito.eq("alternate_pilot_bv_mod"))).thenReturn(false);
        expected = 1.32;
        actual = Crew.getBVSkillMultiplier(gunnery, piloting, mockGame);
        TestCase.assertEquals(expected, actual, 0.001);

        // Test a 5/6 pilot.
        gunnery = 5;
        piloting = 6;
        Mockito.when(mockOptions.booleanOption(Mockito.eq("alternate_pilot_bv_mod"))).thenReturn(false);
        expected = 0.86;
        actual = Crew.getBVSkillMultiplier(gunnery, piloting, mockGame);
        TestCase.assertEquals(expected, actual, 0.001);

        // Test a 2/6 pilot.
        gunnery = 2;
        piloting = 6;
        Mockito.when(mockOptions.booleanOption(Mockito.eq("alternate_pilot_bv_mod"))).thenReturn(false);
        expected = 1.35;
        actual = Crew.getBVSkillMultiplier(gunnery, piloting, mockGame);
        TestCase.assertEquals(expected, actual, 0.001);

        // Test a 0/0 pilot.
        gunnery = 0;
        piloting = 0;
        Mockito.when(mockOptions.booleanOption(Mockito.eq("alternate_pilot_bv_mod"))).thenReturn(false);
        expected = 2.42;
        actual = Crew.getBVSkillMultiplier(gunnery, piloting, mockGame);
        TestCase.assertEquals(expected, actual, 0.001);
    }
}
