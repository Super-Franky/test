import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class Pinball extends JFrame {

    private JPanel buttons;
    private JButton startButton;
    private JButton pauseButton;
    private JButton newBallButton;

    private BallArea ballArea;


    Pinball(){
        setTitle("弹球小游戏");
        setBounds(420,80,700,700);
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try{
                    ballArea.pause();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("lastGame.txt"));
                    objectOutputStream.writeObject(ballArea);
                    objectOutputStream.close();
                }
                catch (Exception exception){
                    exception.printStackTrace();
                }
                System.exit(0);
            }
        });

        startButton = new JButton("start");
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> {
            ballArea.start();
            ballArea.requestFocus();
        });

        pauseButton = new JButton("pause");
        pauseButton.setFocusPainted(false);
        pauseButton.addActionListener(e -> {
            ballArea.pause();
            ballArea.requestFocus();
        });

        newBallButton = new JButton("new ball");
        newBallButton.setFocusPainted(false);
        newBallButton.addActionListener(e -> {
            ballArea.newBall();
            ballArea.requestFocus();
        });


        buttons = new JPanel();
        buttons.setBounds(0,0,700,70);
        buttons.setBackground(Color.white);
        buttons.setBorder(BorderFactory.createRaisedBevelBorder());
        buttons.add(startButton);
        buttons.add(pauseButton);
        buttons.add(newBallButton);
        buttons.setVisible(true);

        add(BorderLayout.NORTH,buttons);

        setVisible(true);

        if (JOptionPane.showConfirmDialog(null,"Continue the last game？","Continue the last game",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
            //继续上次游戏
            try{
                FileInputStream fileInputStream = new FileInputStream("lastGame.txt");
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                ballArea = (BallArea)objectInputStream.readObject();
                if (ballArea.getGame_mode().equals("pve")){
                    add(BorderLayout.CENTER,ballArea);
                    ballArea.start();
                    ballArea.setPVEKeyEvent();
                    ballArea.requestFocus();
                }
                else {
                    add(BorderLayout.CENTER,ballArea);
                    ballArea.start();
                    ballArea.setPVPKeyEvent();
                    ballArea.requestFocus();
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            //开启新游戏
            String [] options = {"pve","pvp"};
            if (JOptionPane.showOptionDialog(null,"please choose your game mode：","game mode",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,null) == 0){
                //游戏规则
                String message = "You can control one ball use wasd or ↑↓←→ in pve game.\n" +
                        "The random size ball will continuous join the game," +
                        "try to eat the ball smaller than you and try not be eaten by the big one.\n" +
                        "Once you are big enough,you win.\n" +
                        "(click the startButton\\pauseButton to start\\pause the game, and click the newBallButton to add a new ball.)";
                JOptionPane.showConfirmDialog(null, message, "rule", JOptionPane.PLAIN_MESSAGE);

                ballArea = new BallArea("pve");
                add(BorderLayout.CENTER,ballArea);
                setVisible(true);
                ballArea.PVEFight(30);
                ballArea.setPVEKeyEvent();
            }
            else {
                //游戏规则
                String message = "You can use wasd to control playerball1 and use ↑↓←→ to control playerball2.\n" +
                        "The random size ball will continuous join the game," +
                        "try to eat the ball smaller than you and try not be eaten by the big one.\n" +
                        "If you are bigger than the other, try to eat him/her to win!!\n" +
                        "(click the startButton\\pauseButton to start\\pause the game, and click the newBallButton to add a new ball.)";
                JOptionPane.showConfirmDialog(null, message, "rule", JOptionPane.PLAIN_MESSAGE);

                ballArea = new BallArea("pvp");
                add(BorderLayout.CENTER,ballArea);
                setVisible(true);
                ballArea.PVPFight(30);
                ballArea.setPVPKeyEvent();
            }

        }
    }

}
