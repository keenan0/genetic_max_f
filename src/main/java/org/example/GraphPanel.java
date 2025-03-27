package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class GraphPanel extends JPanel {
    private GeneticIterator iterator;
    private static final int MARGIN = 50;
    private static final double SCALE = 120;
    private boolean is_drawing_function = false;
    private Point2D.Double selectedPoint = null;

    private final int POINT_SIZE = 6;

    public GraphPanel(GeneticIterator iterator) {
        this.iterator = iterator;
        setPreferredSize(new Dimension(800, 500));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedPoint = getClickedPoint(e.getX(), e.getY());
                repaint();
            }
        });
    }

    private Point2D.Double getClickedPoint(int mouseX, int mouseY) {
        if (iterator != null) {
            for (Point2D.Double p : iterator.getPoints()) {
                int x = (int) (getWidth() / 2 + p.x * SCALE);
                int y = (int) (getHeight() - MARGIN - p.y * SCALE);

                if (Math.abs(mouseX - x) <= POINT_SIZE && Math.abs(mouseY - y) <= POINT_SIZE) {
                    return p;
                }
            }
        }

        return null;
    }

    public void toggleDrawingFunction() {
        is_drawing_function = !is_drawing_function;
        repaint();
    }

    public void setIterator(GeneticIterator it) {
        this.iterator = it;
        repaint();
    }

    public void setCurrentPoint(Point2D.Double p) {
        selectedPoint = p;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        g2d.setColor(Color.BLACK);
        g2d.drawLine(MARGIN, height - MARGIN, width - MARGIN, height - MARGIN);
        g2d.drawLine(width / 2, MARGIN, width / 2, height - MARGIN);

        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Iteration " + this.iterator.getId(), 20, 20);

        g2d.setColor(Color.RED);
        if (iterator != null) {
            for (Point2D.Double p : iterator.getPoints()) {
                int x = (int) (width / 2 + p.x * SCALE);
                int y = (int) (height - MARGIN - p.y * SCALE);
                g2d.fillOval(x - 3, y - 3, POINT_SIZE, POINT_SIZE);
            }
        }

        if(is_drawing_function) {
            g2d.setColor(Color.PINK);
            double step = 0.01;

            for (double x = -1; x < 2; x += step) {
                double y1 = this.iterator.f(x);
                double y2 = this.iterator.f(x + step);

                int px1 = (int) (width / 2 + x * SCALE);
                int py1 = (int) (height - MARGIN - y1 * SCALE);
                int px2 = (int) (width / 2 + (x + step) * SCALE);
                int py2 = (int) (height - MARGIN - y2 * SCALE);

                g2d.drawLine(px1, py1, px2, py2);
            }
        }

        if (selectedPoint != null) {
            g2d.setColor(Color.BLACK);
            int x = (int) (width / 2 + selectedPoint.x * SCALE);
            int y = (int) (height - MARGIN - selectedPoint.y * SCALE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(String.format("(%.2f, %.2f)", selectedPoint.x, selectedPoint.y), x + 10, y - 10);
        }
    }
}
