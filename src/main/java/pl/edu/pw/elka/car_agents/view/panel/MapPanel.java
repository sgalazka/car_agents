package pl.edu.pw.elka.car_agents.view.panel;

import pl.edu.pw.elka.car_agents.Configuration;
import pl.edu.pw.elka.car_agents.map.RoadNetwork;
import pl.edu.pw.elka.car_agents.model.Junction;
import pl.edu.pw.elka.car_agents.util.SwingUtils;

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
        g2d.drawImage(images[0], 0, 0, Configuration.WIDTH, Configuration.HEIGHT, null);
    }

    private void drawRoads(Graphics2D g2d) {
        Junction[] junctions = roadNetwork.getJunctions();
        for (int i = 0; i < junctions.length; i++) {
            for (int j = i + 1; j < junctions.length; j++) {
                if (i == j)
                    continue;
                //System.out.println("junctions: " + i + ":" + j);
                //System.out.println("junctions[i].getRoads()[Junction.UP]: " + junctions[i].getRoads()[Junction.UP]);
                //System.out.println("junctions[j].getRoads()[Junction.DOWN]: " + junctions[j].getRoads()[Junction.DOWN]);
                if (junctions[i].getRoads()[Junction.UP] == junctions[j].getRoads()[Junction.DOWN]) {
                    //System.out.println("junctions[i].getRoads()[Junction.UP] == junctions[j].getRoads()[Junction.DOWN]");
                    int centerX = junctions[i].getCenterCoordinates().getX();
                    int startY = getMaxY(junctions[i]);
                    int endY = getMinY(junctions[j]);
                    drawLanesVertically(g2d, junctions, i, centerX, startY, endY);
                } else if (junctions[i].getRoads()[Junction.RIGHT] == junctions[j].getRoads()[Junction.LEFT]) {
                    //System.out.println("junctions[i].getRoads()[Junction.RIGHT] == junctions[j].getRoads()[Junction.LEFT]");
                    int centerY = junctions[i].getCenterCoordinates().getY();
                    int startX = getMaxX(junctions[i]);
                    int endX = getMinX(junctions[j]);
                    drawLanesHorizontally(g2d, junctions, i, centerY, startX, endX);
                } else if (junctions[i].getRoads()[Junction.DOWN] == junctions[j].getRoads()[Junction.UP]) {
                    //System.out.println("junctions[i].getRoads()[Junction.DOWN] == junctions[j].getRoads()[Junction.UP]");
                    int centerX = junctions[i].getCenterCoordinates().getX();
                    int startY = getMaxY(junctions[j]);
                    int endY = getMinY(junctions[i]);
                    drawLanesVertically(g2d, junctions, i, centerX, startY, endY);
                } else if (junctions[i].getRoads()[Junction.LEFT] == junctions[j].getRoads()[Junction.RIGHT]) {
                    //System.out.println("junctions[i].getRoads()[Junction.LEFT] == junctions[j].getRoads()[Junction.RIGHT]");
                    int centerY = junctions[i].getCenterCoordinates().getY();
                    int startX = getMaxX(junctions[j]);
                    int endX = getMinX(junctions[i]);
                    drawLanesHorizontally(g2d, junctions, i, centerY, startX, endX);
                }
            }
        }
    }

    private void drawLanesHorizontally(Graphics2D g2d, Junction[] junctions, int i, int centerY, int startX, int endX) {
        for (int distanceX = startX; distanceX - Configuration.LANE_WIDTH < endX; distanceX += Configuration.LANE_WIDTH) {
            for (int i1 = 0; i1 < junctions[i].getRoads()[Junction.UP].getOneDirectionNumberOfLanes(); i1++) {
                boolean isTheLastLane = i1 == junctions[i].getRoads()[Junction.UP].getOneDirectionNumberOfLanes() - 1;
                BufferedImage lanesTexture = isTheLastLane ? lanesTextures[2] : lanesTextures[i1];
                g2d.drawImage(SwingUtils.rotate(lanesTexture, Math.PI / 2), distanceX, centerY + i1 * Configuration.LANE_WIDTH,
                        Configuration.LANE_WIDTH, Configuration.LANE_WIDTH, null);
                g2d.drawImage(SwingUtils.rotate(lanesTexture, Math.PI * 1.5), distanceX, centerY - (i1 + 1) * Configuration.LANE_WIDTH,
                        Configuration.LANE_WIDTH, Configuration.LANE_WIDTH, null);
            }
        }
    }

    private void drawLanesVertically(Graphics2D g2d, Junction[] junctions, int i, int centerX, int startY, int endY) {
        for (int distanceY = startY; distanceY - Configuration.LANE_WIDTH < endY; distanceY += Configuration.LANE_WIDTH) {
            for (int i1 = 0; i1 < junctions[i].getRoads()[Junction.UP].getOneDirectionNumberOfLanes(); i1++) {
                boolean isTheLastLane = i1 == junctions[i].getRoads()[Junction.UP].getOneDirectionNumberOfLanes() - 1;
                BufferedImage lanesTexture = isTheLastLane ? lanesTextures[2] : lanesTextures[i1];
                g2d.drawImage(lanesTexture, centerX + i1 * Configuration.LANE_WIDTH, distanceY,
                        Configuration.LANE_WIDTH, Configuration.LANE_WIDTH, null);
                g2d.drawImage(SwingUtils.rotate(lanesTexture, Math.PI), centerX - (i1 + 1) * Configuration.LANE_WIDTH, distanceY,
                        Configuration.LANE_WIDTH, Configuration.LANE_WIDTH, null);
            }
        }
    }

    private void drawJunctions(Graphics2D g2d) {

        for (Junction junction : roadNetwork.getJunctions()) {
            if (junction.isInOut())
                continue;
            int lanesHorizontally = junction.getRoads()[Junction.LEFT].getOneDirectionNumberOfLanes() * 2;
            int lanesVertically = junction.getRoads()[Junction.UP].getOneDirectionNumberOfLanes() * 2;
            int startX = junction.getCenterCoordinates().getX() - (lanesHorizontally * Configuration.LANE_WIDTH) / 2;
            int startY = junction.getCenterCoordinates().getY() - (lanesVertically * Configuration.LANE_WIDTH) / 2;
            for (int i = 0; i < lanesHorizontally; i++) {
                for (int j = 0; j < lanesVertically; j++) {
                    g2d.drawImage(images[1], startX + i * Configuration.LANE_WIDTH, startY + j * Configuration.LANE_WIDTH,
                            Configuration.LANE_WIDTH, Configuration.LANE_WIDTH, null);
                }
            }
        }
    }

    private int getMaxX(Junction junction) {
        int maxX = 0;
        try {
            maxX = junction.getCenterCoordinates().getX() + junction.getRoads()[Junction.UP].getOneDirectionNumberOfLanes() * Configuration.LANE_WIDTH;
        } catch (NullPointerException e) {
            return maxX;
        }
        return junction.getCenterCoordinates().getX();
    }

    private int getMinX(Junction junction) {
        int minX = 0;
        try {
            minX = junction.getCenterCoordinates().getX() - junction.getRoads()[Junction.UP].getOneDirectionNumberOfLanes() * Configuration.LANE_WIDTH;
        } catch (NullPointerException e) {
            return minX;
        }
        return junction.getCenterCoordinates().getX();
    }

    private int getMaxY(Junction junction) {
        int maxY = 0;
        try {
            maxY = junction.getCenterCoordinates().getY() + junction.getRoads()[Junction.RIGHT].getOneDirectionNumberOfLanes() * Configuration.LANE_WIDTH;
        } catch (NullPointerException e) {
            return maxY;
        }
        return junction.getCenterCoordinates().getY();
    }

    private int getMinY(Junction junction) {
        int maxY = 0;
        try {
            maxY = junction.getCenterCoordinates().getY() - junction.getRoads()[Junction.RIGHT].getOneDirectionNumberOfLanes() * Configuration.LANE_WIDTH;
        } catch (NullPointerException e) {
            return maxY;
        }
        return junction.getCenterCoordinates().getY();
    }
}
