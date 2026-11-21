package game;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorldTest {

    private World world;

    private Item newItem(char glyph, String name) {
        return new Item(glyph, Color.WHITE, name, null);
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
    }

    @Test
    void constructor_preservesWidthHeightAndDepth() {
        assertEquals(3, world.width());
        assertEquals(3, world.height());
        assertEquals(1, world.depth());
    }

    @Test
    void tile_negativeCoordinatesReturnsBounds() {
        assertSame(Tile.BOUNDS, world.tile(-1, 0, 0));
        assertSame(Tile.BOUNDS, world.tile(0, -1, 0));
        assertSame(Tile.BOUNDS, world.tile(0, 0, -1));
    }

    @Test
    void tile_coordinatesAtUpperBoundaryReturnBounds() {
        assertSame(Tile.BOUNDS, world.tile(3, 0, 0));
        assertSame(Tile.BOUNDS, world.tile(0, 3, 0));
        assertSame(Tile.BOUNDS, world.tile(0, 0, 1));
    }

    @Test
    void tile_validCoordinatesReturnsFloor() {
        assertSame(Tile.FLOOR, world.tile(0, 0, 0));
        assertSame(Tile.FLOOR, world.tile(2, 2, 0));
    }

    @Test
    void creature_emptyLocationReturnsNull() {
        assertNull(world.creature(1, 1, 0));
    }

    @Test
    void add_creaturePlacesCreatureAtCoordinates() {
        Creature creature = new Creature(world, '@', Color.GRAY, "hero", 100, 10, 5);
        creature.x = 1;
        creature.y = 1;
        creature.z = 0;

        world.add(creature);

        assertSame(creature, world.creature(1, 1, 0));
    }

    @Test
    void remove_creatureClearsCreatureAtCoordinates() {
        Creature creature = new Creature(world, '@', Color.GRAY, "hero", 100, 10, 5);
        creature.x = 1;
        creature.y = 1;
        creature.z = 0;
        world.add(creature);

        world.remove(creature);

        assertNull(world.creature(1, 1, 0));
    }

    @Test
    void glyph_emptyLocationReturnsTileGlyph() {
        assertEquals(Tile.FLOOR.glyph(), world.glyph(1, 1, 0));
    }

    @Test
    void glyph_itemLocationReturnsItemGlyph() {
        Item item = newItem('x', "Test");
        world.addAtEmptySpace(item, 1, 1, 0);

        assertEquals('x', world.glyph(1, 1, 0));
    }

    @Test
    void glyph_creatureLocationReturnsCreatureGlyph() {
        Creature creature = new Creature(world, '@', Color.GRAY, "hero", 100, 10, 5);
        creature.x = 1;
        creature.y = 1;
        creature.z = 0;
        world.add(creature);

        assertEquals('@', world.glyph(1, 1, 0));
    }

    @Test
    void color_creatureLocationReturnsCreatureColor() {
        Creature creature = new Creature(world, '@', Color.MAGENTA, "hero", 100, 10, 5);
        creature.x = 1;
        creature.y = 1;
        creature.z = 0;
        world.add(creature);

        assertSame(Color.MAGENTA, world.color(1, 1, 0));
    }

    @Test
    void item_emptyLocationReturnsNull() {
        assertNull(world.item(1, 1, 0));
    }

    @Test
    void addAtEmptySpace_nullItemReturnsTrueAndChangesNothing() {
        assertTrue(world.addAtEmptySpace(null, 1, 1, 0));
        assertNull(world.item(1, 1, 0));
    }

    @Test
    void addAtEmptySpace_emptyLocationPlacesItemAtRequestedLocation() {
        Item item = newItem('x', "Test");

        assertTrue(world.addAtEmptySpace(item, 1, 1, 0));
        assertSame(item, world.item(1, 1, 0));
    }

    @Test
    void addAtEmptySpace_occupiedLocationPlacesItemInAdjacentGroundLocation() {
        Item first = newItem('a', "First");
        Item second = newItem('b', "Second");
        assertTrue(world.addAtEmptySpace(first, 1, 1, 0));

        assertTrue(world.addAtEmptySpace(second, 1, 1, 0));
        assertSame(first, world.item(1, 1, 0));

        int secondItemLocations = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (world.item(x, y, 0) == second)
                    secondItemLocations++;
            }
        }
        assertEquals(1, secondItemLocations);

        boolean isAdjacent = false;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (world.item(x, y, 0) == second)
                    isAdjacent = Math.abs(x - 1) <= 1 && Math.abs(y - 1) <= 1;
            }
        }
        assertTrue(isAdjacent);
    }

    @Test
    void addAtEmptySpace_allLocationsOccupiedReturnsFalse() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                assertTrue(world.addAtEmptySpace(newItem('a', "Existing"), x, y, 0));
            }
        }
        Item item = newItem('b', "Cannot fit");

        assertFalse(world.addAtEmptySpace(item, 1, 1, 0));

        int matchingItems = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (world.item(x, y, 0) == item)
                    matchingItems++;
            }
        }
        assertEquals(0, matchingItems);
    }

    @Test
    void addAtEmptySpace_startingLocationIsWallReturnsFalse() {
        Tile[][][] tiles = new Tile[1][1][1];
        tiles[0][0][0] = Tile.WALL;
        World walledWorld = new World(tiles);
        Item item = newItem('x', "Test");

        assertFalse(walledWorld.addAtEmptySpace(item, 0, 0, 0));
        assertNull(walledWorld.item(0, 0, 0));
    }

    @Test
    void remove_itemByCoordinatesClearsLocation() {
        Item item = newItem('x', "Test");
        world.addAtEmptySpace(item, 1, 1, 0);

        world.remove(1, 1, 0);

        assertNull(world.item(1, 1, 0));
    }

    @Test
    void remove_itemObjectClearsMatchingLocation() {
        Item item = newItem('x', "Test");
        world.addAtEmptySpace(item, 1, 1, 0);

        world.remove(item);

        assertNull(world.item(1, 1, 0));
    }

    @Test
    void remove_missingItemPreservesExistingItems() {
        Item existing = newItem('a', "Existing");
        Item missing = newItem('b', "Missing");
        world.addAtEmptySpace(existing, 1, 1, 0);

        world.remove(missing);

        assertSame(existing, world.item(1, 1, 0));
    }
}
