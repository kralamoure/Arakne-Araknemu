package fr.quatrevieux.araknemu.game.exploration.npc.store;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The Npc store
 *
 * Note: the store is related to a npc template and not a npc, so the same store can be shared between two npcs
 */
final public class NpcStore {
    final private ItemService itemService;
    final private GameConfiguration.EconomyConfiguration configuration;
    final private Map<Integer, ItemTemplate> itemTemplates;

    public NpcStore(ItemService itemService, GameConfiguration.EconomyConfiguration configuration, Collection<ItemTemplate> itemTemplates) {
        this.itemService = itemService;
        this.configuration = configuration;
        this.itemTemplates = itemTemplates.stream().collect(Collectors.toMap(ItemTemplate::id, Function.identity()));
    }

    /**
     * Get all available item templates
     */
    public Collection<ItemTemplate> available() {
        return itemTemplates.values();
    }

    /**
     * Check if the given item template is available
     *
     * @param id The item template id
     *
     * @return true if available
     */
    public boolean has(int id) {
        return itemTemplates.containsKey(id);
    }

    /**
     * Try to generate the items
     *
     * Note: Each items are regenerated, so items may have different stats and quantity
     *
     * @param id The item template id
     * @param quantity The asked quantity
     *
     * @return Map of generated item, associated with quantity
     */
    public Map<Item, Integer> get(int id, int quantity) {
        Map<Item, Integer> items = new HashMap<>();

        for (; quantity > 0; --quantity) {
            Item generated = itemService.create(itemTemplates.get(id));

            items.put(generated, items.getOrDefault(generated, 0) + 1);
        }

        return items;
    }

    /**
     * Get the price for generate given items
     *
     * @param id The item template id
     * @param quantity The asked quantity
     *
     * @return The price
     */
    public long price(int id, int quantity) {
        return itemTemplates.get(id).price() * quantity;
    }

    /**
     * Get the price for sell the item to the NPC
     *
     * @param item Item to sell
     * @param quantity Quantity to sell
     *
     * @return The cost in kamas
     */
    public long sellPrice(Item item, int quantity) {
        long basePrice = (long) (configuration.npcSellPriceMultiplier() * item.template().price());

        return basePrice * quantity;
    }
}
