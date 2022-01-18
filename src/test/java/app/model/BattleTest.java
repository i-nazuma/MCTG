package app.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BattleTest {

    @Test
    @DisplayName("Testing the constructor of Battle")
    void setupBattle(){
        User userA = new User();
        User userB = new User();
        Battle battle = new Battle(1, false, userA, userB, null);
        int testId = battle.getId();
        boolean testIsFinished = battle.isFinished();
        User testUserA = battle.getPlayerA();
        User testUserB = battle.getPlayerB();
        assertEquals(testId, 1);
        assertEquals(testIsFinished, false);
        assertEquals(testUserA, userA);
        assertEquals(testUserB, userB);
        assertEquals(battle.getWinningPlayer(), null);
    }

    @Test
    @DisplayName("Testing the constructor of Battle for false statements")
    void setupBattleFalse(){
        User userA = new User();
        User userB = new User();
        Battle battle = new Battle(1, false, userA, userB, null);
        int testId = battle.getId();
        boolean testIsFinished = battle.isFinished();
        User testUserA = battle.getPlayerA();
        User testUserB = battle.getPlayerB();
        assertNotEquals(testId, 2);
        assertNotEquals(testIsFinished, true);
        assertNotEquals(testUserA, userB);
        assertNotEquals(testUserB, userA);
        assertNotEquals(battle.getWinningPlayer(), userA);
    }
}