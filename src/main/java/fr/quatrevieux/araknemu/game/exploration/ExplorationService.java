package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.game.event.exploration.StartExploration;
import fr.quatrevieux.araknemu.game.event.listener.player.StopExploration;
import fr.quatrevieux.araknemu.game.event.listener.player.InitializeGame;
import fr.quatrevieux.araknemu.game.event.listener.player.SendMapData;
import fr.quatrevieux.araknemu.game.exploration.action.factory.ActionFactory;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;

/**
 * Base service for handle game exploration
 */
final public class ExplorationService {
    final private ExplorationMapService mapService;
    final private ActionFactory actionFactory;

    public ExplorationService(ExplorationMapService mapService, ActionFactory actionFactory) {
        this.mapService = mapService;
        this.actionFactory = actionFactory;
    }

    /**
     * Start exploration for a player
     */
    public ExplorationPlayer start(GamePlayer player) {
        ExplorationPlayer exploration = new ExplorationPlayer(player);

        exploration.dispatcher().add(new InitializeGame(exploration, mapService));
        exploration.dispatcher().add(new SendMapData(exploration));
        exploration.dispatcher().add(new StopExploration(exploration));

        exploration.dispatch(new StartExploration());

        return exploration;
    }

    /**
     * Start an action for the player
     *
     * @param player The action perform
     * @param request The received action request
     *
     * @throws Exception When cannot perform the action
     */
    public void action(ExplorationPlayer player, GameActionRequest request) throws Exception {
        player.actionQueue().push(
            actionFactory.create(player, request)
        );
    }
}
