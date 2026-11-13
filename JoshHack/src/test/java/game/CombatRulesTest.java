package game;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CombatRulesTest {

    @ParameterizedTest
    @CsvSource({
        "50, 20, 70, false",
        "99, 0, 99, false",
        "90, 10, 100, false",
        "90, 11, 100, false",
        "100, 10, 100, false",
        "50, -20, 30, false",
        "10, -9, 1, false",
        "10, -10, 0, true",
        "10, -11, -1, true"
    })
    void testHpChanges(int currentHp, int amount, int expectedHp, boolean expectedDead) {
        int result = CombatRules.calculateNewHp(currentHp, 100, amount);

        assertEquals(expectedHp, result);
        assertEquals(expectedDead, CombatRules.isDead(result));
    }
}