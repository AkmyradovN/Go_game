package gogame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.JComponent;

public class GoPane extends JComponent implements MouseListener, MouseMotionListener, ComponentListener
{
    private GoState state;
    private int pixelsPerSpace;
    private int pixelSize;
    private Point hover;
    private Consumer<String> updateLabel;
    public Runnable endGame;
    public boolean isActive;
    public GoPane(Consumer<String> updateLabel, GoState state) {
        this.updateLabel = updateLabel;
        setGoState(state);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addComponentListener(this);
    }
    public void setGoState(int size) { setGoState(new GoState(size)); }
    public void setGoState(GoState state) {
        this.state = state;
        componentResized(null);
    }
    public void toggle() {
        isActive = !isActive;
        if (isActive) updateLabel.accept(state.toString());
    }
    public void pass() {
        if (state.makeMove(null)) {
            endGame.run();
        }
        updateLabel.accept(state.toString());
        repaint();
    }
    public void saveGame(File file) {
        state.saveGame(file);
    }
    public void loadGame(File file) {
        setGoState(GoState.loadGame(file));
    }
    private void drawStone(Graphics g, Point p, Color c)
    {
        g.setColor(c);
        g.fillOval(p.x*pixelsPerSpace, p.y*pixelsPerSpace, pixelsPerSpace, pixelsPerSpace);
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GRAY);
        for (int x = 0; x < state.board.length; x++) { //draw grid lines
            g.fillRect(x*pixelsPerSpace+pixelsPerSpace/2, 0, 2, pixelSize);
            g.fillRect(0, x*pixelsPerSpace+pixelsPerSpace/2, pixelSize, 2);
        }
        for (int x = 0; x < state.board.length; x++) { //draw stones
            for (int y = 0; y < state.board.length; y++) {
                if (state.board[x][y] == BoardSpace.EMPTY)
                {
                    if (isActive && hover != null && hover.x == x && hover.y == y) {
                        drawStone(g, hover, state.turn == Stone.BLACK ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                    }
                    continue;
                }
                drawStone(g, new Point(x, y), state.board[x][y] == BoardSpace.BLACK ? Color.BLACK : Color.WHITE);
            }
        }
    }
    public void mouseClicked(MouseEvent e) {
        if (!isActive) return;
        state.makeMove(new Point(e.getX() / pixelsPerSpace, e.getY() / pixelsPerSpace));
        updateLabel.accept(state.toString());
        repaint();
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) { hover = null; repaint(); }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {
        if (!isActive) return;
        hover = new Point(e.getX() / pixelsPerSpace, e.getY() / pixelsPerSpace);
        if (!state.isLegalMove(hover)) hover = null;
        repaint();
    }
    public void componentHidden(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e) {}
    public void componentResized(ComponentEvent e) {
        pixelsPerSpace = Math.min(getWidth(), getHeight()) / state.board.length;
        pixelSize = pixelsPerSpace * state.board.length;
        repaint();
    }
    public void componentShown(ComponentEvent e) {}
}
