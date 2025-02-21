/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.arakne.utils.maps.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;

/**
 * Simulator for punishment effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddCharacteristicOnDamageHandler
 */
public final class PunishmentSimulator implements EffectSimulator {
    @Override
    public void simulate(CastSimulation simulation, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        int duration = effect.effect().duration();

        if (duration == -1 || duration > 10) {
            duration = 10;
        } else if (duration == 0) {
            duration = 1;
        }

        final int value = duration * effect.effect().max();

        for (FighterData target : effect.targets()) {
            simulation.addBoost(value, target);
        }
    }
}
