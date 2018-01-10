package pl.edu.pw.elka.car_agents.view.panel;

import pl.edu.pw.elka.car_agents.Configuration;
import pl.edu.pw.elka.car_agents.util.SwingUtils;
import pl.edu.pw.elka.car_agents.view.model.CarCoordinates;
import pl.edu.pw.elka.car_agents.view.model.CarDirection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class CarLayerPanel extends JPanel {

    private List<CarCoordinates> carCoordinates;
    private BufferedImage texture;

    public CarLayerPanel() {
        try {
            texture = ImageIO.read(new File("./textures/car.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setOpaque(false);
    }

    public void setData(List<CarCoordinates> carCoordinates) {
        this.carCoordinates = carCoordinates;
        for (CarCoordinates carCoordinate : carCoordinates) {
            System.out.println(carCoordinate.toString());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        System.out.println("CarLayerPanel::paintComponent");
        if (carCoordinates == null)
            return;
        for (CarCoordinates carCoordinate : carCoordinates) {
            System.out.println("CarLayerPanel::paintComponent coordinate: " + carCoordinate.toString());
            int x = carCoordinate.getCoordinates().getX();
            int y = carCoordinate.getCoordinates().getY();
            if (carCoordinate.getDirection().equals(CarDirection.NORTH)) {
                ((Graphics2D) g).drawImage(texture, x - Configuration.CAR_WIDTH / 2, y - Configuration.CAR_HEIGHT, Configuration.CAR_WIDTH, Configuration.CAR_HEIGHT, null);
            } else if (carCoordinate.getDirection().equals(CarDirection.EAST)) {
                ((Graphics2D) g).drawImage(SwingUtils.rotate(texture, Math.PI * 0.5), x - Configuration.CAR_HEIGHT / 2, y - Configuration.CAR_WIDTH, Configuration.CAR_HEIGHT, Configuration.CAR_WIDTH, null);
            } else if (carCoordinate.getDirection().equals(CarDirection.SOUTH)) {
                ((Graphics2D) g).drawImage(SwingUtils.rotate(texture, Math.PI), x - Configuration.CAR_WIDTH / 2, y - Configuration.CAR_HEIGHT, Configuration.CAR_WIDTH, Configuration.CAR_HEIGHT, null);
            } else if (carCoordinate.getDirection().equals(CarDirection.WEST)) {
                ((Graphics2D) g).drawImage(SwingUtils.rotate(texture, Math.PI * 1.5), x - Configuration.CAR_HEIGHT / 2, y - Configuration.CAR_WIDTH, Configuration.CAR_HEIGHT, Configuration.CAR_WIDTH, null);
            }
        }
    }
}
