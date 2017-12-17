package pl.edu.pw.elka.car_agents.view;

import pl.edu.pw.elka.car_agents.Configuration;
import pl.edu.pw.elka.car_agents.map.RoadNetwork;
import pl.edu.pw.elka.car_agents.view.model.CarCoordinates;
import pl.edu.pw.elka.car_agents.view.panel.CarLayerPanel;
import pl.edu.pw.elka.car_agents.view.panel.MapPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class SwingRoadNetworkView extends RoadNetworkView {

    private JFrame mainFrame;
    private MapPanel mapPanel;
    private CarLayerPanel carLayerPanel;

    public SwingRoadNetworkView(OnWindowCloseListener onWindowCloseListener, RoadNetwork roadNetwork) {
        super(onWindowCloseListener, roadNetwork);
        this.onWindowCloseListener = onWindowCloseListener;
        this.mainFrame = new JFrame("Car Agents");
        this.mapPanel = new MapPanel(roadNetwork);
        this.carLayerPanel = new CarLayerPanel();
        JPanel layeredPane = new JPanel();
        layeredPane.setLayout(null);
        mapPanel.setBounds(0, 0, Configuration.WIDTH, Configuration.HEIGHT);
        carLayerPanel.setBounds(0, 0, Configuration.WIDTH, Configuration.HEIGHT);
//        mainFrame.setSize(new Dimension(1700, 1000));
        layeredPane.add(carLayerPanel);
        layeredPane.add(mapPanel);


        mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        mainFrame.add(layeredPane, BorderLayout.CENTER);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (onWindowCloseListener != null)
                    onWindowCloseListener.onWindowClosed();
                e.getWindow().dispose();
            }
        });
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    public void drawCars(List<CarCoordinates> carCoordinates) {
        carLayerPanel.setData(carCoordinates);
        carLayerPanel.repaint();
    }
}

