package fr.quatrevieux.araknemu.game.event.listener.player.inventory;

import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectAdded;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;

/**
 * Save to database the new object
 */
final public class SaveNewItem implements Listener<ObjectAdded> {
    final private PlayerItemRepository repository;

    public SaveNewItem(PlayerItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void on(ObjectAdded event) {
        if (!(event.entry() instanceof InventoryEntry)) {
            return;
        }

        InventoryEntry entry = (InventoryEntry) event.entry();

        repository.add(entry.entity());
    }

    @Override
    public Class<ObjectAdded> event() {
        return ObjectAdded.class;
    }
}
