package pl.edu.pw.elka.car_agents.gui;

import pl.edu.pw.elka.car_agents.model.Car;
import pl.edu.pw.elka.car_agents.model.Coordinates;
import pl.edu.pw.elka.car_agents.model.Lane;
import pl.edu.pw.elka.car_agents.model.Road;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static java.awt.Color.*;

public class Gui extends JPanel {
    private static final int ROAD_THICKNESS = 10;
    private static final int LANE_THICKNESS = 2;
    private static Color[] carColors = {BLUE, RED, GREEN, CYAN, YELLOW};

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Road r = createRoad();
        drawRoad(r, (Graphics2D) g);
        drawCar(new Car(new Coordinates(5, 10), 0f), (Graphics2D) g);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    // create the GUI explicitly on the Swing event thread
    private static void createAndShowGui() {
        Gui mainPanel = new Gui();

        JFrame frame = new JFrame("Gui");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private void drawRoad(Road road, Graphics2D graphics){
        graphics.setStroke(new BasicStroke(ROAD_THICKNESS));
        graphics.setColor(Color.BLACK);
        graphics.draw(new Line2D.Float(road.getStartCoordinates().getX(), road.getStartCoordinates().getY(),
                road.getEndCoordinates().getX(), road.getEndCoordinates().getY()));

        road.getLanes().forEach(l -> drawLane(l, graphics));

    }

    private void drawLane(Lane lane, Graphics2D graphics){
        graphics.setStroke(new BasicStroke(LANE_THICKNESS));
        graphics.setColor(Color.WHITE);
        graphics.draw(new Line2D.Float(lane.getStartCoordinates().getX(), lane.getStartCoordinates().getY(),
                lane.getEndCoordinates().getX(), lane.getEndCoordinates().getY()));
    }

    private void drawCar(Car car, Graphics2D graphics){
        graphics.setStroke(new BasicStroke(LANE_THICKNESS));
        graphics.setColor(carColors[ThreadLocalRandom.current().nextInt(0, carColors.length)]);
        graphics.draw(new Rectangle.Float(car.getCoordinates().getX(), car.getCoordinates().getY(), 7, 5));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Gui::createAndShowGui);
    }

    private Road createRoad(){
        Road r = new Road();
        r.setStartCoordinates(new Coordinates(0, 0));
        r.setEndCoordinates(new Coordinates(50, 50));

        Lane l = new Lane();
        l.setEndCoordinates(new Coordinates(0, 0));
        l.setStartCoordinates(new Coordinates(50, 50));
        r.getLanes().add(l);
        return r;
    }


}