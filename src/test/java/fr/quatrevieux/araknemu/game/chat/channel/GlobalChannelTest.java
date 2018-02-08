package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.event.common.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class GlobalChannelTest extends GameBaseCase {
    private PlayerService service;
    private Set<GamePlayer> receivers;

    private GamePlayer gp1, gp2, gp3, gp4, gp5;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushRaces();
        dataSet.use(PlayerItem.class);

        receivers = new HashSet<>();

        service = container.get(PlayerService.class);

        GameSession session1 = new GameSession(new DummyChannel());
        session1.attach(new GameAccount(new Account(1), container.get(AccountService.class), 2));
        gp1 = service.load(session1, dataSet.pushPlayer("Bob", 1, 2).id());
        gp1.dispatcher().add(BroadcastedMessage.class, e -> receivers.add(gp1));

        GameSession session2 = new GameSession(new DummyChannel());
        session2.attach(new GameAccount(new Account(2), container.get(AccountService.class), 2));
        gp2 = service.load(session2, dataSet.pushPlayer("Robert", 2, 2).id());
        gp2.dispatcher().add(BroadcastedMessage.class, e -> receivers.add(gp2));

        GameSession session3 = new GameSession(new DummyChannel());
        session3.attach(new GameAccount(new Account(3), container.get(AccountService.class), 2));
        gp3 = service.load(session3, dataSet.pushPlayer("Jean", 3, 2).id());
        gp3.dispatcher().add(BroadcastedMessage.class, e -> receivers.add(gp3));

        GameSession session4 = new GameSession(new DummyChannel());
        session4.attach(new GameAccount(new Account(4), container.get(AccountService.class), 2));
        gp4 = service.load(session4, dataSet.pushPlayer("Kevin", 4, 2).id());
        gp4.dispatcher().add(BroadcastedMessage.class, e -> receivers.add(gp4));

        GameSession session5 = new GameSession(new DummyChannel());
        session5.attach(new GameAccount(new Account(5), container.get(AccountService.class), 2));
        gp5 = service.load(session5, dataSet.pushPlayer("Louis", 5, 2).id());
        gp5.dispatcher().add(BroadcastedMessage.class, e -> receivers.add(gp5));
    }

    @Test
    void sendToAll() throws ChatException {
        GlobalChannel channel = new GlobalChannel(ChannelType.TRADE, service);

        channel.send(
            gp1,
            new Message(
                ChannelType.TRADE,
                null,
                "My message",
                ""
            )
        );

        assertCount(5, receivers);
        assertContains(gp1, receivers);
        assertContains(gp2, receivers);
        assertContains(gp3, receivers);
        assertContains(gp4, receivers);
        assertContains(gp5, receivers);
    }

    @Test
    void notAuthorized() {
        GlobalChannel channel = new GlobalChannel(ChannelType.TRADE, player -> false, service);

        try {
            channel.send(
                gp1,
                new Message(
                    ChannelType.TRADE,
                    null,
                    "My message",
                    ""
                )
            );

            fail("Exception must be thrown");
        } catch (ChatException e) {
            assertEquals(ChatException.Error.UNAUTHORIZED, e.error());
        }
    }

    @Test
    void filter() throws ChatException {
        GlobalChannel channel = new GlobalChannel(ChannelType.TRADE, player -> player.name().contains("o"), service);

        channel.send(
            gp1,
            new Message(
                ChannelType.TRADE,
                null,
                "My message",
                ""
            )
        );

        assertCount(3, receivers);

        assertContains(gp1, receivers);
        assertContains(gp2, receivers);
        assertContains(gp5, receivers);
    }
}
