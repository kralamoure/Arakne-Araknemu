package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.data.value.Dimensions;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.exploration.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.event.listener.map.SendNewSprite;
import fr.quatrevieux.araknemu.game.event.listener.map.SendPlayerMove;
import fr.quatrevieux.araknemu.game.event.listener.map.ValidatePlayerPath;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Decoder;
import fr.quatrevieux.araknemu.game.world.util.Sender;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Map object for exploration
 *
 * @todo optimize for inactive cells
 */
final public class ExplorationMap implements Dispatcher {
    public class Cell {
        final private MapTemplate.Cell template;

        public Cell(MapTemplate.Cell template) {
            this.template = template;
        }

        /**
         * Can walk on this cell ?
         */
        public boolean isWalkable() {
            return template.active() && template.movement() > 0;
        }
    }

    final private MapTemplate template;
    final private List<Cell> cells;
    final private ConcurrentMap<Integer, ExplorationPlayer> players = new ConcurrentHashMap<>();
    final private DefaultListenerAggregate dispatcher = new DefaultListenerAggregate();

    public ExplorationMap(MapTemplate template) {
        this.template = template;

        cells = template.cells()
            .stream()
            .map(Cell::new)
            .collect(Collectors.toList())
        ;

        dispatcher.add(new SendNewSprite(this));
        dispatcher.add(new ValidatePlayerPath(this));
        dispatcher.add(new SendPlayerMove(this));
    }

    public int id() {
        return template.id();
    }

    public String date() {
        return template.date();
    }

    public String key() {
        return template.key();
    }

    public Dimensions dimensions() {
        return template.dimensions();
    }

    /**
     * Get the number of cells of the map
     */
    public int size() {
        return cells.size();
    }

    public Cell cell(int id) {
        return cells.get(id);
    }

    /**
     * Add a new player to the map
     */
    public void add(ExplorationPlayer player) {
        if (players.containsKey(player.id())) {
            throw new IllegalArgumentException("The player is already added");
        }

        players.put(player.id(), player);

        dispatch(new NewSpriteOnMap(player.sprite()));
    }

    /**
     * Get list of map sprites
     */
    public Collection<Sprite> sprites() {
        return players
            .values()
            .stream()
            .map(ExplorationPlayer::sprite)
            .collect(Collectors.toList())
        ;
    }

    /**
     * Get all players on map
     */
    public Collection<ExplorationPlayer> players() {
        return players.values();
    }

    /**
     * Get the map decoder
     */
    public Decoder decoder() {
        return new Decoder(this);
    }

    @Override
    public void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    /**
     * Send a packet to the map
     */
    public void send(Object packet) {
        String str = packet.toString(); // Store string value for optimisation

        for (Sender player : players.values()) {
            player.send(str);
        }
    }

    ListenerAggregate dispatcher() {
        return dispatcher;
    }
}
