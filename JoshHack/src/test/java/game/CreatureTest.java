package game;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class CreatureTest {

    private World world;
    private Creature creature;
    
    private static class RecordingCreatureAi extends CreatureAi {
        private final List<String> messages = new ArrayList<>();

        RecordingCreatureAi(Creature creature) {
            super(creature);
        }

        @Override
        public void onNotify(String message) {
            messages.add(message);
        }
    }

    @BeforeEach
    void setUp() {
        Tile[][][] tiles = new Tile[3][3][1];

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                tiles[x][y][0] = Tile.FLOOR;
            }
        }

        world = new World(tiles);

        creature = new Creature(world, '@', Color.GRAY, "josh", 100, 10, 5);
        new CreatureAi(creature);

        creature.x = 1;
        creature.y = 1;
        creature.z = 0;
    }

    @Test
    void testNormalDamage() {
        creature.modifyHp(-20, "damage");

        assertEquals(80, creature.hp());
    }

    @Test
    void testHealingOverMaxHp() {
        creature.modifyHp(-20, "damage");
        creature.modifyHp(30, "healing");

        assertEquals(100, creature.hp());
    }

    @Test
    void testAliveAtOneHp() {
        creature.modifyHp(-90, "damage");
        creature.modifyHp(-9, "damage");

        assertEquals(1, creature.hp());
    }

    @Test
    void testDeathAtZeroHp() {
        world.add(creature);

        assertSame(creature, world.creature(1, 1, 0));

        creature.modifyHp(-90, "damage");
        creature.modifyHp(-10, "damage");

        assertEquals(0, creature.hp());
        assertNull(world.creature(1, 1, 0));
    }

    @Test
    void testModifyHpBelowZeroDoesNotClampUpward() {
        world.add(creature);

        creature.modifyHp(-500, "catapult");

        assertEquals(-400, creature.hp());
        assertEquals("catapult", creature.causeOfDeath());
        assertNull(world.creature(1, 1, 0));
    }

    @Test
    void testDeathLeavesNamedCorpseAtLocation() {
        world.add(creature);

        creature.modifyHp(-100, "fatal damage");

        Item corpse = world.item(1, 1, 0);
        assertNotNull(corpse);
        assertEquals('%', corpse.glyph());
        assertEquals(Color.GRAY, corpse.color());
        assertEquals("josh corpse", corpse.name());
        assertEquals(500, corpse.foodValue());
    }

    @Test
    void testDeathDropsInventoryItemAtAnEmptyLocation() {
        Item sword = new Item('s', Color.WHITE, "sword", null);
        creature.inventory().add(sword);
        world.add(creature);

        creature.modifyHp(-100, "fatal damage");

        assertFalse(creature.inventory().contains(sword));

        boolean swordWasDropped = false;
        for (int x = 0; x < world.width(); x++) {
            for (int y = 0; y < world.height(); y++) {
                if (world.item(x, y, 0) == sword) {
                    swordWasDropped = true;
                }
            }
        }
        assertTrue(swordWasDropped);
    }

    @Test
    void testDoActionNotifiesSelfWithFormattedMessage() {
        RecordingCreatureAi ai = new RecordingCreatureAi(creature);
        creature.setCreatureAi(ai);
        world.add(creature);

        creature.doAction("walk %d steps", 3);

        assertEquals(List.of("You walk 3 steps."), ai.messages);
    }

    @Test
    void testDoActionNotifiesAnotherCreatureInSecondPerson() {
        RecordingCreatureAi actorAi = new RecordingCreatureAi(creature);
        creature.setCreatureAi(actorAi);
        world.add(creature);

        Creature goblin = new Creature(world, 'g', Color.GREEN, "goblin", 50, 5, 2);
        goblin.x = 1;
        goblin.y = 2;
        goblin.z = 0;
        RecordingCreatureAi goblinAi = new RecordingCreatureAi(goblin);
        goblin.setCreatureAi(goblinAi);
        world.add(goblin);

        creature.doAction("attack the %s for %d damage", "goblin", 7);

        assertEquals(List.of("You attack the goblin for 7 damage."), actorAi.messages);
        assertEquals(List.of("The josh attacks the goblin for 7 damage."), goblinAi.messages);
    }
}