package game;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Color;

public class CreatureTest {

    private World world;
    private Creature creature;

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
}