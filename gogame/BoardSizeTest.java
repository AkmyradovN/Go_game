package gogame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BoardSizeTest {
    @ParameterizedTest
    @ValueSource(strings= {"9x9", "13x13", "19x19"})
    void testFromString(String sizeString) {
        BoardSize expected = switch (sizeString) {
            case "9x9" -> BoardSize.NINE;
            case "13x13" -> BoardSize.THIRTEEN;
            case "19x19" -> BoardSize.NINETEEN;
            default -> throw new IllegalArgumentException("Unexpected value: " + sizeString);
        };
        assertEquals(expected, BoardSize.fromString(sizeString));
    }
    @Test
    void testFailingFromString() {
        assertThrows(IllegalArgumentException.class, () -> BoardSize.fromString("TEN"));
        assertThrows(IllegalArgumentException.class, () -> BoardSize.fromString("15x15"));
        assertThrows(IllegalArgumentException.class, () -> BoardSize.fromString("19"));
    }
}
