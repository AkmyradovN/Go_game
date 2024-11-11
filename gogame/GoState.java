package gogame;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.io.*;

public class GoState implements Serializable, Predicate<Point> {
    public final BoardSpace[][] board;
    private int blackCaptured;
    private int whiteCaptured;
    public Stone turn;
    private final Set<GoState> prevStates = new HashSet<>();
    private static final long serialVersionUID = 1L;

    public GoState(BoardSize s) {
        this.board = new BoardSpace[s.getSize()][s.getSize()];
        for (BoardSpace[] row : board) {
            Arrays.fill(row, BoardSpace.EMPTY);
        }
        this.blackCaptured = 0;
        this.whiteCaptured = 0;
        this.turn = Stone.BLACK;
    }
    public GoState(int size) {
        this(BoardSize.fromInt(size));
    }
    public GoState(GoState other) {
        this.board = Arrays.stream(other.board).map(BoardSpace[]::clone).toArray(BoardSpace[][]::new);
        this.blackCaptured = other.blackCaptured;
        this.whiteCaptured = other.whiteCaptured;
        this.turn = other.turn;
        this.prevStates.addAll(other.prevStates);
    }
    @Override
    public boolean test(Point p) {
        int size = board.length;
        return p.x >= 0 && p.x < size && p.y >= 0 && p.y < size;
    }
    public Point[] getNeighbors(Point p) {
        return Stream.of(new Point(p.x, p.y - 1), new Point(p.x, p.y + 1), new Point(p.x - 1, p.y), new Point(p.x + 1, p.y)).filter(this::test).toArray(Point[]::new);
    }
    public Point[] getLiberties(Stone s, Point p, Set<Point> scanned) {
        Stack<Point> toScan = new Stack<>();
        toScan.push(p);
        Set<Point> liberties = new HashSet<>();
        while (!toScan.isEmpty()) {
            Point current = toScan.pop();
            if (scanned.contains(current)) continue;
            scanned.add(current);
            if (!test(current)) continue;
            BoardSpace space = board[current.x][current.y];
            if (space == BoardSpace.EMPTY && current != p) {
                liberties.add(current);
            } else if (space.stone == s) {
                Arrays.stream(getNeighbors(current)).filter(neighbor -> !scanned.contains(neighbor)).forEach(toScan::push);
            }
        }
        return liberties.toArray(new Point[0]);
    }
    public void checkCaptured(Point p) {
        Stone opptColor = turn.opposite();
        Arrays.stream(getNeighbors(p)).filter(neighbor -> board[neighbor.x][neighbor.y].stone == opptColor).forEach(neighbor -> {Set<Point> scanned = new HashSet<>();
                Point[] liberties = getLiberties(opptColor, neighbor, scanned);
                if (liberties.length == 0) {
                    long capturedCount = scanned.stream().filter(captured -> board[captured.x][captured.y].stone == opptColor).peek(captured -> board[captured.x][captured.y] = BoardSpace.EMPTY).count(); 
                    if (opptColor == Stone.BLACK) {
                        blackCaptured += capturedCount;
                    } 
                    else {
                        whiteCaptured += capturedCount;
                    }
                }
            });
    }
    public GoState placeStone(Point p) {
        board[p.x][p.y] = BoardSpace.fromStone(turn);
        checkCaptured(p);
        return this;
    }
    public boolean isLegalMove(Point p) {
        if (!test(p) || board[p.x][p.y] != BoardSpace.EMPTY) return false;   
        GoState simulatedState = new GoState(this);
        simulatedState.placeStone(p);
        Set<Point> scanned = new HashSet<>();
        Point[] liberties = simulatedState.getLiberties(turn, p, scanned);
        boolean hasLiberties = liberties.length > 0;
        if (!hasLiberties) {
            boolean capturesOpponent = Arrays.stream(getNeighbors(p)).anyMatch(neighbor -> board[neighbor.x][neighbor.y].stone == turn.opposite() && simulatedState.getLiberties(turn.opposite(), neighbor, new HashSet<>()).length == 0);
            if (!capturesOpponent) {
                return false;
            }
        }
        return !prevStates.contains(simulatedState);
    }
    public boolean makeMove(Point p) {
        if (p == null) {
            prevStates.add(new GoState(this));       
            turn = turn.opposite();
            return prevStates.contains(new GoState(this));
        }
        if (!isLegalMove(p)) return false;
        prevStates.add(new GoState(this));
        placeStone(p);
        turn = turn.opposite();
        return prevStates.contains(new GoState(this));
    }
    public void saveGame(File filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save game file to: " + filename, e);
        }
    }
    public static GoState loadGame(File filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (GoState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Failed to load game file to: " + filename, e);
        }
    }
    public int getCapturedCount(Stone stone) {
        return stone == Stone.BLACK ? blackCaptured : whiteCaptured;
    }
    @Override
    public String toString() {
        return "Black Captured: " + blackCaptured + "\nWhite Captured: " + whiteCaptured;
    }
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board) + Objects.hash(turn);
    }
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof GoState)) return false;
        GoState other = (GoState) o;
        return Arrays.deepEquals(this.board, other.board) && this.turn == other.turn;
    }
}