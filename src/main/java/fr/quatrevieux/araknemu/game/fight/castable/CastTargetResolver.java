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

package fr.quatrevieux.araknemu.game.fight.castable;

import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Perform target resolution for a casted effect
 */
public final class CastTargetResolver {
    /**
     * Utility class : disable constructor
     */
    private CastTargetResolver() {}

    /**
     * Resolve targets of an effect
     *
     * @param caster The action caster
     * @param target The target cell
     * @param action Action to perform
     * @param effect The effect to resolve
     *
     * @return List of fighters
     *
     * @see fr.quatrevieux.araknemu.game.spell.effect.target.EffectTarget
     */
    public static <F extends FighterData> Collection<F> resolveFromEffect(F caster, BattlefieldCell target, Castable action, SpellEffect effect) {
        return resolveFromEffect(caster, caster.cell(), target, action, effect);
    }

    /**
     * Resolve targets of an effect
     *
     * @param caster The action caster
     * @param from The cell from where the effect is cast
     * @param target The target cell
     * @param action Action to perform
     * @param effect The effect to resolve
     *
     * @return List of fighters
     *
     * @see fr.quatrevieux.araknemu.game.spell.effect.target.EffectTarget
     */
    public static <F extends FighterData> Collection<F> resolveFromEffect(F caster, BattlefieldCell from, BattlefieldCell target, Castable action, SpellEffect effect) {
        if (effect.target().onlyCaster()) {
            return Collections.singleton(caster);
        }

        if (action.constraints().freeCell()) {
            return Collections.emptyList();
        }

        return resolveFromEffectArea(caster, from, target, effect);
    }

    /**
     * Perform resolution from effect target and effect area
     */
    @SuppressWarnings("cast.unsafe") // @Nullable cast cause a compiler crash on java 8
    private static <F extends FighterData> Collection<F> resolveFromEffectArea(F caster, BattlefieldCell from, BattlefieldCell target, SpellEffect effect) {
        // Use lazy instantiation and do not use stream API to optimise memory allocations
        F firstTarget = null;
        Collection<F> targets = null;

        for (BattlefieldCell cell : effect.area().resolve(target, from)) {
            final @Nullable F resolvedTarget = (/*@Nullable*/ F) cell.fighter();

            if (resolvedTarget == null || !effect.target().test(caster, resolvedTarget)) {
                continue;
            }

            // Found the first target
            if (firstTarget == null) {
                firstTarget = resolvedTarget;
                continue;
            }

            // Multiple targets are found : instantiate the collection
            if (targets == null) {
                targets = new ArrayList<>();
                targets.add(firstTarget);
            }

            targets.add(resolvedTarget);
        }

        // There is multiple targets
        if (targets != null) {
            return targets;
        }

        // There is only one target : create a singleton
        if (firstTarget != null) {
            return Collections.singleton(firstTarget);
        }

        // No targets are resolved
        return Collections.emptyList();
    }
}
