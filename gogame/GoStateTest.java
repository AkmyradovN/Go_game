package gogame;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import java.io.File;
import java.util.Set;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class GoStateTest {
    @ParameterizedTest
    @ArgumentsSource(NeighborsProvider.class)
    void getNeighborsTest(Point point, Point[] expectedNeighbors) {
        GoState goState = new GoState(BoardSize.NINE);
        Point[] actualNeighbors = goState.getNeighbors(point);
        Set<Point> expectedSet = new HashSet<>(Arrays.asList(expectedNeighbors));
        Set<Point> actualSet = new HashSet<>(Arrays.asList(actualNeighbors));
        assertEquals(expectedSet, actualSet);
    }
    static class NeighborsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(new Point(0, 0), new Point[]{new Point(0, 1), new Point(1, 0)}),
                    Arguments.of(new Point(0, 7), new Point[]{new Point(0, 6), new Point(0, 8), new Point(1, 7)}),
                    Arguments.of(new Point(5, 5), new Point[]{new Point(5, 4), new Point(5, 6), new Point(4, 5), new Point(6, 5)})
            );
        }
    }
    @ParameterizedTest
    @CsvSource({"5,5", "0,0", "8,8"})
    void testIsLegalMove_nonEmptySpace(int x, int y) {
        GoState goState = new GoState(BoardSize.NINE);
        goState.placeStone(new Point(x, y));
        assertFalse(goState.isLegalMove(new Point(x, y)));
    }
    @Test
    void testIsLegalMove_preventSuicide() {
        GoState goState = new GoState(BoardSize.NINE);
        goState.makeMove(new Point(0, 0));
        goState.makeMove(new Point(0, 4));
        goState.makeMove(new Point(1, 1));
        goState.makeMove(new Point(0, 5));
        goState.makeMove(new Point(2, 0));
        assertFalse(goState.isLegalMove(new Point(1, 0)));
    }
    @Test
    void testIsLegalMove_noRepeatState() {
        GoState goState = new GoState(BoardSize.NINE);
        goState.placeStone(new Point(1, 1));
        goState.placeStone(new Point(1, 2));
        goState.placeStone(new Point(2, 1));
        goState.placeStone(new Point(2, 2));
        GoState copiedState = new GoState(goState);
        assertFalse(copiedState.makeMove(new Point(1, 1)));
    }
    @Test
    void testCheckCapture() {
        GoState goState = new GoState(BoardSize.NINE);
        goState.turn = Stone.BLACK;
        goState.placeStone(new Point(4, 4));
        goState.turn = Stone.WHITE;
        goState.placeStone(new Point(4, 3));
        goState.placeStone(new Point(5, 4));
        goState.placeStone(new Point(3, 4));
        goState.placeStone(new Point(4, 5));
        assertEquals(BoardSpace.EMPTY, goState.board[4][4]);
        assertEquals(1, goState.getCapturedCount(Stone.BLACK));
    }
    @Test
    void testGetLiberties() {
        GoState goState = new GoState(BoardSize.NINE);
        goState.placeStone(new Point(2, 2));
        Set<Point> scanned = new HashSet<>();
        Point[] liberties = goState.getLiberties(Stone.BLACK, new Point(2, 2), scanned);
        assertEquals(4, liberties.length);
    }
    @Test
    void testMakeMove_doublePass() {
        GoState goState = new GoState(BoardSize.NINE);
        assertFalse(goState.makeMove(null));
        assertTrue(goState.makeMove(null));
    }
    @Test
    void testSaveLoadGame() {
        GoState goState = new GoState(BoardSize.NINE);
        File filename = new File("testGame.ser");
        goState.saveGame(filename);
        GoState loadedState = GoState.loadGame(filename);
        assertEquals(goState, loadedState);
        filename.delete();
    }
}