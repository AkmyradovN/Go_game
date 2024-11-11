package gogame;

import java.util.Arrays;
import java.util.stream.Stream;

public enum BoardSize {
    NINE(9), THIRTEEN(13), NINETEEN(19);
    private final int size;
    BoardSize(int size){
        this.size = size;
    }
    public int getSize() {
        return size;
    }
    @Override
    public String toString() {
        switch (this) {
            case NINE:
                return "9x9";
            case THIRTEEN:
                return "13x13";
            case NINETEEN:
                return "19x19";
            default:
                throw new IllegalStateException("Invalid BoardSize");
        }
    }
    public static BoardSize fromInt(int size) {
        switch (size) {
            case 9:
                return NINE;
            case 13:
                return THIRTEEN;            
            case 19: 
                return NINETEEN;
            default:
                throw new IllegalArgumentException("Invalid board size: " + size);
        }
    }
    public static BoardSize fromString(String s) {
        return Stream.of(BoardSize.values())
            .filter(size -> size.toString().equals(s))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid board size: " + s));
    }
    public static String[] getStringValues() {
        return Arrays.stream(values()).map(BoardSize::toString).toArray(String[]::new);
    }
}