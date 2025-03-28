package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MainFrame extends JFrame {
    private GeneticHandler handler;
    private GraphPanel graphPanel;
    private JButton prevButton, nextButton;
    private JButton drawFullGraphButton;
    private JButton resetButton;

    MainFrame(GeneticHandler handler) {
        this.handler = handler;
        ImageIcon icon = new ImageIcon("icon.png");

        this.setTitle("Genetic Algorithms");
        this.setIconImage(icon.getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(new BorderLayout());

        this.graphPanel = new GraphPanel(this.handler.getIterator(0));
        this.add(graphPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        prevButton = new JButton("<");
        nextButton = new JButton(">");
        drawFullGraphButton = new JButton("Graph");
        resetButton = new JButton("Reset");

        prevButton.setFocusPainted(false);
        nextButton.setFocusPainted(false);
        drawFullGraphButton.setFocusPainted(false);
        resetButton.setFocusPainted(false);

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(drawFullGraphButton);
        buttonPanel.add(resetButton);

        this.add(buttonPanel, BorderLayout.SOUTH);

        prevButton.addActionListener(e -> updateGraph(-1));
        nextButton.addActionListener(e -> updateGraph(1));
        drawFullGraphButton.addActionListener(e -> drawFullGraph());
        resetButton.addActionListener(e -> reset());

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                handleScroll(e);
            }
        });

        Action prevAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGraph(-1);
            }
        };

        Action nextAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGraph(1);
            }
        };

        InputMap inputMap = graphPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = graphPanel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "prev");
        actionMap.put("prev", prevAction);

        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "next");
        actionMap.put("next", nextAction);

        updateButtonStates();
        this.setVisible(true);
    }

    private void handleScroll(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            graphPanel.updateScale(1);
        } else {
            graphPanel.updateScale(-1);
        }
    }

    private void reset() {
        handler.reset();
        graphPanel.setIterator(handler.getCurrentIterator());
        graphPanel.repaint();
    }

    private void drawFullGraph() {
        this.graphPanel.toggleDrawingFunction();
    }

    private void updateGraph(int direction) {
        if (direction == -1) {
            handler.previousIterator();
        } else if (direction == 1) {
            handler.nextIterator();
        }

        graphPanel.setCurrentPoint(null);
        graphPanel.setIterator(handler.getCurrentIterator());
        updateButtonStates();
    }

    private void updateButtonStates() {
        prevButton.setEnabled(handler.hasPrevious());
        nextButton.setEnabled(handler.hasNext());
    }
}
