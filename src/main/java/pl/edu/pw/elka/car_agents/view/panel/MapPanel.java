package pl.edu.pw.elka.car_agents.view.panel;

import pl.edu.pw.elka.car_agents.Main;
import pl.edu.pw.elka.car_agents.map.RoadNetwork;
import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.util.SwingUtils;
import pl.edu.pw.elka.car_agents.view.SwingRoadNetworkView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MapPanel extends JPanel {

    private RoadNetwork roadNetwork;
    private BufferedImage[] images;
    private BufferedImage[] lanesTextures;

    public MapPanel(RoadNetwork roadNetwork) {
        this.roadNetwork = roadNetwork;
        this.images = new BufferedImage[2];
        this.lanesTextures = new BufferedImage[3];
        try {
            images[0] = ImageIO.read(new File("./textures/background_texture.jpg"));
            images[1] = ImageIO.read(new File("./textures/junction_texture.png"));
            lanesTextures[0] = ImageIO.read(new File("./textures/inner_lane.png"));
            lanesTextures[1] = ImageIO.read(new File("./textures/between_lane.png"));
            lanesTextures[2] = ImageIO.read(new File("./textures/outer_lane.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawBackground(g2d);
        drawRoads(g2d);
        drawJunctions(g2d);
    }

    private void drawBackground(Graphics2D g2d) {
        g2d.drawImage(images[0], 0, 0, SwingRoadNetworkView.WIDTH, SwingRoadNetworkView.HEIGHT, null);
    }

    private void drawRoads(Graphics2D g2d) {
        Junction[] junctions = roadNetwork.getJunctions();
        for (int i = 0; i < junctions.length; i++) {
            for (int j = i + 1; j < junctions.length; j++) {
                if (i == j)
                    continue;
                System.out.println("junctions: " + i + ":" + j);
                System.out.println("junctions[i].getRoads()[Junction.UP]: " + junctions[i].getRoads()[Junction.UP]);
                System.out.println("junctions[j].getRoads()[Junction.DOWN]: " + junctions[j].getRoads()[Junction.DOWN]);
                if (junctions[i].getRoads()[Junction.UP] == junctions[j].getRoads()[Junction.DOWN]) {
                    System.out.println("junctions[i].getRoads()[Junction.UP] == junctions[j].getRoads()[Junction.DOWN]");
                    int centerX = junctions[i].getCenterCoordinates().getX();
                    int startY = getMaxY(junctions[i]);
                    int endY = getMinY(junctions[j]);
                    drawLanesVertically(g2d, junctions, i, centerX, startY, endY);
                } else if (junctions[i].getRoads()[Junction.RIGHT] == junctions[j].getRoads()[Junction.LEFT]) {
                    System.out.println("junctions[i].getRoads()[Junction.RIGHT] == junctions[j].getRoads()[Junction.LEFT]");
                    int centerY = junctions[i].getCenterCoordinates().getY();
                    int startX = getMaxX(junctions[i]);
                    int endX = getMinX(junctions[j]);
                    drawLanesHorizontally(g2d, junctions, i, centerY, startX, endX);
                } else if (junctions[i].getRoads()[Junction.DOWN] == junctions[j].getRoads()[Junction.UP]) {
                    System.out.println("junctions[i].getRoads()[Junction.DOWN] == junctions[j].getRoads()[Junction.UP]");
                    int centerX = junctions[i].getCenterCoordinates().getX();
                    int startY = getMaxY(junctions[j]);
                    int endY = getMinY(junctions[i]);
                    drawLanesVertically(g2d, junctions, i, centerX, startY, endY);
                } else if (junctions[i].getRoads()[Junction.LEFT] == junctions[j].getRoads()[Junction.RIGHT]) {
                    System.out.println("junctions[i].getRoads()[Junction.LEFT] == junctions[j].getRoads()[Junction.RIGHT]");
                    int centerY = junctions[i].getCenterCoordinates().getY();
                    int startX = getMaxX(junctions[j]);
                    int endX = getMinX(junctions[i]);
                    drawLanesHorizontally(g2d, junctions, i, centerY, startX, endX);
                }
            }
        }
    }

    private void drawLanesHorizontally(Graphics2D g2d, Junction[] junctions, int i, int centerY, int startX, int endX) {
        for (int distanceX = startX; distanceX - Main.LANE_WIDTH < endX; distanceX += Main.LANE_WIDTH) {
            for (int i1 = 0; i1 < junctions[i].getRoads()[Junction.UP].getLanes().length / 2; i1++) {
                boolean isTheLastLane = i1 == junctions[i].getRoads()[Junction.UP].getLanes().length / 2 - 1;
                BufferedImage lanesTexture = isTheLastLane ? lanesTextures[2] : lanesTextures[i1];
                g2d.drawImage(SwingUtils.rotate(lanesTexture, Math.PI / 2), distanceX, centerY + i1 * Main.LANE_WIDTH,
                        Main.LANE_WIDTH, Main.LANE_WIDTH, null);
                g2d.drawImage(SwingUtils.rotate(lanesTexture, Math.PI * 1.5), distanceX, centerY - (i1 + 1) * Main.LANE_WIDTH,
                        Main.LANE_WIDTH, Main.LANE_WIDTH, null);
            }
        }
    }

    private void drawLanesVertically(Graphics2D g2d, Junction[] junctions, int i, int centerX, int startY, int endY) {
        for (int distanceY = startY; distanceY - Main.LANE_WIDTH < endY; distanceY += Main.LANE_WIDTH) {
            for (int i1 = 0; i1 < junctions[i].getRoads()[Junction.UP].getLanes().length / 2; i1++) {
                boolean isTheLastLane = i1 == junctions[i].getRoads()[Junction.UP].getLanes().length / 2 - 1;
                BufferedImage lanesTexture = isTheLastLane ? lanesTextures[2] : lanesTextures[i1];
                g2d.drawImage(lanesTexture, centerX + i1 * Main.LANE_WIDTH, distanceY,
                        Main.LANE_WIDTH, Main.LANE_WIDTH, null);
                g2d.drawImage(SwingUtils.rotate(lanesTexture, Math.PI), centerX - (i1 + 1) * Main.LANE_WIDTH, distanceY,
                        Main.LANE_WIDTH, Main.LANE_WIDTH, null);
            }
        }
    }

    private void drawJunctions(Graphics2D g2d) {

        for (Junction junction : roadNetwork.getJunctions()) {
            int lanesHorizontally = junction.getRoads()[Junction.LEFT].getLanes().length;
            int lanesVertically = junction.getRoads()[Junction.UP].getLanes().length;
            int startX = junction.getCenterCoordinates().getX() - (lanesHorizontally * Main.LANE_WIDTH) / 2;
            int startY = junction.getCenterCoordinates().getY() - (lanesVertically * Main.LANE_WIDTH) / 2;
            for (int i = 0; i < lanesHorizontally; i++) {
                for (int j = 0; j < lanesVertically; j++) {
                    g2d.drawImage(images[1], startX + i * Main.LANE_WIDTH, startY + j * Main.LANE_WIDTH,
                            Main.LANE_WIDTH, Main.LANE_WIDTH, null);
                }
            }
        }
    }

    private int getMaxX(Junction junction) {
        return junction.getCenterCoordinates().getX() + junction.getRoads()[Junction.UP].getLanes().length * Main.LANE_WIDTH / 2;
    }

    private int getMinX(Junction junction) {
        return junction.getCenterCoordinates().getX() - junction.getRoads()[Junction.UP].getLanes().length * Main.LANE_WIDTH / 2;
    }

    private int getMaxY(Junction junction) {
        return junction.getCenterCoordinates().getY() + junction.getRoads()[Junction.RIGHT].getLanes().length * Main.LANE_WIDTH / 2;
    }

    private int getMinY(Junction junction) {
        return junction.getCenterCoordinates().getY() - junction.getRoads()[Junction.RIGHT].getLanes().length * Main.LANE_WIDTH / 2;
    }
}
