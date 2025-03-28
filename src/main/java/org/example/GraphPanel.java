package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class GraphPanel extends JPanel {
    private GeneticIterator iterator;
    private static final int MARGIN = 50;
    private static double SCALE = 80;
    private boolean is_drawing_function = false;
    private Point2D.Double selectedPoint = null;

    private final double MAX_SCALE = 200;
    private final double MIN_SCALE = 10;

    private int POINT_SIZE = (int)(20 * (SCALE / MAX_SCALE));

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
                int y = (int) (getHeight() / 2 - p.y * SCALE);

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

    public void updateScale(int inc) {
        if(inc == -1) {
            if(SCALE > MIN_SCALE) SCALE-=1;
        } else  if(inc == 1) {
            if(SCALE < MAX_SCALE) SCALE+=1;
        }

        POINT_SIZE = (int)(20 * (SCALE / MAX_SCALE));

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
        g2d.drawLine(MARGIN, height / 2, width - MARGIN, height / 2);
        g2d.drawLine(width / 2, MARGIN, width / 2, height - MARGIN);

        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Iteration " + this.iterator.getId(), 20, 20);

        g2d.setColor(Color.RED);
        if (iterator != null) {
            for (Point2D.Double p : iterator.getPoints()) {
                int x = (int) (width / 2 + p.x * SCALE);
                int y = (int) (height / 2 - p.y * SCALE);
                g2d.fillOval(x - 3, y - 3, POINT_SIZE, POINT_SIZE);
            }
        }

        if(is_drawing_function) {
            g2d.setColor(Color.PINK);
            double step = 0.01;

            for (double x = Codificator.getLowerBound(); x < Codificator.getUpperBound(); x += step) {
                double y1 = this.iterator.f(x);
                double y2 = this.iterator.f(x + step);

                int px1 = (int) (width / 2 + x * SCALE);
                int py1 = (int) (height / 2 - y1 * SCALE);
                int px2 = (int) (width / 2 + (x + step) * SCALE);
                int py2 = (int) (height / 2 - y2 * SCALE);

                g2d.drawLine(px1, py1, px2, py2);
            }

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(String.format("%.2f", Codificator.getLowerBound()),
                    (int) (Codificator.getLowerBound() * SCALE + width / 2),
                    height / 2 + 20
            );

            g2d.setColor(Color.GRAY);
            int lower_x = (int) (Codificator.getLowerBound() * SCALE + width / 2);
            g2d.drawLine(
                    lower_x,
                    height / 2,
                    lower_x,
                    height / 2 - (int)(Math.round(iterator.f(Codificator.getLowerBound())) * SCALE)
            );

            g2d.setColor(Color.BLACK);
            g2d.drawString(String.format("%.2f", Codificator.getUpperBound()),
                    (int) (Codificator.getUpperBound() * SCALE + width / 2),
                    height / 2 + 20
            );

            g2d.setColor(Color.GRAY);
            int upper_x = (int) (Codificator.getUpperBound() * SCALE + width / 2);
            g2d.drawLine(
                    upper_x,
                    height / 2,
                    upper_x,
                    height / 2 - (int)(Math.round(iterator.f(Codificator.getUpperBound()) * SCALE))
            );
        }

        if (selectedPoint != null) {
            g2d.setColor(Color.BLACK);
            int x = (int) (width / 2 + selectedPoint.x * SCALE);
            int y = (int) (height / 2 - selectedPoint.y * SCALE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(String.format("(%.2f, %.2f)", selectedPoint.x, selectedPoint.y), x + 10, y - 10);
        }
    }
}
