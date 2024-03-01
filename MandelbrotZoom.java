import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.*;

public class MandelbrotZoom extends JFrame implements ActionListener {

    public final int screenWidth = 1200;
    public final int screenHeight = 1000;
    public JPanel controlPanel;
    public JPanel buttonsPanel;
    public JButton[] buttons = new JButton[10];
    public BufferedImage fractalImage;
    public int startingMaxIterations = 50;
    public int maxIterations = 50;
    public double zx, cx;
    public double zy, cy;
    public double zoom = 150;
    public double xPos;
    public double yPos = screenHeight/2;
    public int randomR = (int)(Math.random()*100);
    public int randomG = (int)(Math.random()*100);
    public int randomB = (int)(Math.random()*100);
    public Color belongsColor = Color.BLACK;
    public int blackPixelCount = 0, looping = 0;
    MandelbrotZoom() {
        this.setTitle("Mandelbrot Zoom");
        this.setSize(screenWidth, screenHeight);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);

        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        this.controlPanel = new JPanel();
        this.controlPanel.setBounds(screenWidth - 180, 0, 180, screenHeight);
        this.controlPanel.setBackground(Color.BLACK);
        this.controlPanel.setLayout(null);

        this.xPos = (this.getWidth() - this.controlPanel.getWidth())/2;

        this.buttonsPanel = new JPanel(new GridLayout(5, 2));
        this.buttonsPanel.setBackground(Color.RED);
        this.buttonsPanel.setBounds(0, this.controlPanel.getHeight() / 2 - this.controlPanel.getWidth(), this.controlPanel.getWidth(), this.controlPanel.getWidth() + 50);

        this.buttons[0] = new JButton("+");
        this.buttons[1] = new JButton("++");
        this.buttons[2] = new JButton("-");
        this.buttons[3] = new JButton("--");
        this.buttons[4] = new JButton("^");
        this.buttons[5] = new JButton("v");
        this.buttons[6] = new JButton("<");
        this.buttons[7] = new JButton(">");
        this.buttons[8] = new JButton("reset");
        this.buttons[9] = new JButton("colors");

        for (int i = 0; i < this.buttons.length; i++) {
            this.buttonsPanel.add(this.buttons[i]);
            this.buttons[i].addActionListener(this);
        }

        this.generateFractalImage();

        this.controlPanel.add(this.buttonsPanel);
        contentPane.add(this.controlPanel);
        contentPane.add(new ImagePaint());

        this.setVisible(true);
    }

    public class ImagePaint extends JPanel {
        public ImagePaint() {
            this.setBounds(0, 0, 1020, 1000);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(fractalImage, 0, 0, this);
        }
    }

    public void generateFractalImage() {
        this.fractalImage = new BufferedImage(this.getWidth()-this.controlPanel.getWidth(), this.getHeight(),BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < this.getWidth()-this.controlPanel.getWidth(); x++) {
            for (int y = 0; y < this.getHeight(); y++) {
                this.zx = 0;
                this.zy = 0;
                this.cx = (x-xPos)/zoom;
                this.cy = (y-yPos)/zoom;
                int iterations = 0;
                double tmp;
                while (iterations < maxIterations && zx*zx + zy*zy < 4) {
                    tmp = zx*zx - zy*zy + cx;
                    this.zy = 2*zx*zy + cy;
                    zx = tmp;
                    iterations++;
                }
                if (iterations == maxIterations) {
                    this.fractalImage.setRGB(x, y, belongsColor.getRGB());
                } else {
                    double percentage = (double)iterations/maxIterations;
                    int r = (int)(percentage * 0x1000000)*randomR;
                    int g = (int)(percentage * 0x1000000)*randomG;
                    int b = (int)(percentage * 0x1000000)*randomB;

                    if (percentage!=0)
                        this.fractalImage.setRGB(x, y, r + g + b);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String eventName  = e.getActionCommand();

        switch (eventName) {
            case "+" ->  {
                this.zoom += 100;
                this.xPos += 100;
            }
            case "++" -> {
                this.zoom *= 2;
                //this.xPos += 1000
                looping = 0;
                //count black pixels
                boolean found = false;
                while (!found) {
                    this.blackPixelCount = 0;
                    for (int x = 0; x < this.getWidth() - this.controlPanel.getWidth(); x++) {
                        for (int y = 0; y < this.getHeight(); y++) {
                            if (this.fractalImage.getRGB(x, y) == Color.BLACK.getRGB()) {
                                this.blackPixelCount++;
                            }
                        }
                    }
                    if (looping < 50) {
                        if (this.blackPixelCount > 510_000) {
                            this.yPos *= 2;
                            looping++;
                            System.out.println(yPos);
                        } else if (this.blackPixelCount == 0) {
                            this.yPos /= 2;
                            looping++;
                            System.out.println(yPos);
                        } else
                            found = true;
                    } else {
                        if (this.blackPixelCount > 510_000) {
                            this.yPos += 10_000;
                            System.out.println(yPos);
                        } else if (this.blackPixelCount < 200_000) {
                            this.yPos -= 100;
                            System.out.println(yPos);
                        } else
                                found = true;
                    }
                    this.maxIterations = 25;
                    generateFractalImage();
                }
                System.out.println("There are: " + blackPixelCount + " black pixels on the screen.");
                System.out.println("The zoom equals: " + this.zoom);
                this.maxIterations = this.startingMaxIterations;
            }
            case "-" -> this.zoom -= 100;
            case "--" -> {
                this.zoom -= 1000;
                this.xPos -= 1000;
            }
            case "^" -> this.yPos += 100;
            case "v" -> this.yPos -= 100;
            case "<" -> this.xPos += 100;
            case ">" -> this.xPos -= 100;
            case "reset" -> {
                this.xPos = (this.screenWidth-this.controlPanel.getWidth())/2;
                this.yPos = this.screenHeight/2;
                this.zoom = 150;
            }
            case "colors" -> {
                this.randomR = (int)(Math.random()*100);
                this.randomG = (int)(Math.random()*100);
                this.randomB = (int)(Math.random()*100);
            }
        }
        generateFractalImage();
        repaint();
    }
}