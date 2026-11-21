package game;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item('!', Color.RED, "Potion", null);
    }

    @Test
    void constructor_setsGlyphColorNameAndFallbackAppearance() {
        assertEquals('!', item.glyph());
        assertSame(Color.RED, item.color());
        assertEquals("Potion", item.name());
        assertEquals("Potion", item.appearance());
    }

    @Test
    void constructor_preservesExplicitAppearance() {
        Item explicit = new Item('?', Color.BLUE, "Scroll", "strange scroll");

        assertEquals("strange scroll", explicit.appearance());
    }

    @Test
    void constructor_initializesThrownAttackAndEmptyCollections() {
        assertEquals(1, item.thrownAttackValue());
        assertNotNull(item.writtenSpells());
        assertTrue(item.writtenSpells().isEmpty());
        assertEquals("", item.details());
    }

    @Test
    void modifyFoodValue_updatesAndReturnsFoodValue() {
        item.modifyFoodValue(12);

        assertEquals(12, item.foodValue());
    }

    @Test
    void modifyAttackValue_updatesAndReturnsAttackValue() {
        item.modifyAttackValue(7);

        assertEquals(7, item.attackValue());
    }

    @Test
    void modifyDefenseValue_updatesAndReturnsDefenseValue() {
        item.modifyDefenseValue(5);

        assertEquals(5, item.defenseValue());
    }

    @Test
    void modifyThrownAttackValue_updatesAndReturnsThrownAttackValue() {
        item.modifyThrownAttackValue(4);

        assertEquals(5, item.thrownAttackValue());
    }

    @Test
    void modifyRangedAttackValue_updatesAndReturnsRangedAttackValue() {
        item.modifyRangedAttackValue(3);

        assertEquals(3, item.rangedAttackValue());
    }

    @Test
    void setAndGetQuaffEffect_returnsSetEffect() {
        Effect effect = new Effect(10);
        item.setQuaffEffect(effect);

        assertSame(effect, item.quaffEffect());
    }

    @Test
    void quaffEffect_defaultsToNull() {
        assertNull(item.quaffEffect());
    }

    @Test
    void addWrittenSpell_addsObservableSpellData() {
        Effect effect = new Effect(4);

        item.addWrittenSpell("heal", 6, effect);

        assertEquals(1, item.writtenSpells().size());
        Spell spell = item.writtenSpells().get(0);
        assertEquals("heal", spell.name());
        assertEquals(6, spell.manaCost());
        assertEquals(4, spell.effect().duration);
    }

    @Test
    void details_reportsEveryConfiguredStatInStableOrder() {
        item.modifyAttackValue(3);
        item.modifyThrownAttackValue(2);
        item.modifyRangedAttackValue(4);
        item.modifyDefenseValue(5);
        item.modifyFoodValue(6);

        assertEquals(
            "  attack:3  thrown:3  ranged:4  defense:5  food:6",
            item.details()
        );
    }

    @Test
    void details_omitsUnconfiguredStats() {
        item.modifyAttackValue(1);

        assertEquals("  attack:1", item.details());
    }
}
