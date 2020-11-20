package tockenring.sber.grafic;


import javax.swing.*;
import java.awt.*;

class DrawingComponent extends JPanel {
    int xg[] =  GraficGenerator.x;
    int yg[] =  GraficGenerator.y;
    int ng = GraficGenerator.n;

    @Override
    protected void paintComponent(Graphics gh) {
        Graphics2D drp = (Graphics2D)gh;
        drp.drawLine(20, 220, 20, 350);
        drp.drawLine(20, 350, 360, 350);
        drp.drawString("Y", 25, 230);
        drp.drawString("X", 350, 346);
        drp.drawPolyline(xg, yg, ng);
    }
}

public class GraficGenerator extends JFrame{
    public static void setX(int[] x) {
        GraficGenerator.x = x;
    }

    public static void setY(int[] y) {
        GraficGenerator.y = y;
    }
    public  static int[] x;
    public  static int[] y;

    public static void setN(int n) {
        GraficGenerator.n = n;
    }

    public static int n;

    public GraficGenerator() {
        super("График по точкам");
    }
    public void draw(){
        JPanel jcp = new JPanel(new BorderLayout());
        setContentPane(jcp);
        jcp.add(new DrawingComponent (), BorderLayout.CENTER);
        jcp.setBackground(Color.gray);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static void main(String[] args)   {
        new GraficGenerator().setVisible(true);
    }
}