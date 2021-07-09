import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class BallArea extends JPanel implements Serializable{

    private class Ball implements Serializable{
        Color color;//球的颜色
        int x;//x坐标
        int y;//y坐标
        int rad;//半径
        int weight;//小球的质量
        int speed;//速度
        int xSpeed;//x方向的分速度
        int ySpeed;//y方向的分速度
        BallMoving runThread;//小球移动线程

        Ball(Color color,int x,int y,int dia,int speed,int xSpeed,int ySpeed){
            this.color = color;
            this.x = x;
            this.y = y;
            this.rad = dia;
            this.weight = dia * dia / 35;
            this.speed = speed;
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;

            runThread = new BallMoving();
            runThread.setDaemon(true);
            runThread.start();
        }

        void start(){
            runThread = new BallMoving();
            runThread.setDaemon(true);
            runThread.start();
        }

        class BallMoving extends Thread implements Serializable{
            @Override
            public void run(){
                while (running){
                    synchronized (this){
                        x += xSpeed;
                        y += ySpeed;
                    }
                    try {
                        sleep(speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private class Repainting extends Thread implements Serializable{
        @Override
        public void run() {
            while (running){
                //重绘
                repaint();

                try {
                    sleep(refreshTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class BorderColl extends Thread implements Serializable{//检测边界碰撞的线程
        @Override
        public void run() {
            while (running){

                try{
                    for (int i = 0; i < balls.size(); i++){
                        synchronized (balls.get(i)){
                            Ball ball = balls.get(i);
                            //检测上边界
                            if (ball.y < 0){
                                ball.ySpeed = -ball.ySpeed;
                                ball.y = 1;
                            }
                            //检测下边界
                            else if (ball.y + ball.rad * 2 > getHeight() - 2){
                                ball.ySpeed = -ball.ySpeed;
                                ball.y = getHeight() - 3 - ball.rad * 2;
                            }
                            //检测左边界
                            else if (ball.x < 0){
                                ball.xSpeed = -ball.xSpeed;
                                ball.x = 1;
                            }
                            //检测右边界
                            else if (ball.x + ball.rad * 2 > getWidth() - 2){
                                ball.xSpeed = -ball.xSpeed;
                                ball.x = getWidth() - 3 - ball.rad * 2;
                            }
                        }
                    }
                    sleep(refreshTime);
                }
                catch (Exception e){
                }
            }
        }
    }
    private class BallColl extends Thread implements Serializable{//检测小球互相碰撞的线程
        @Override
        public void run() {
            while (running){
                try{
                    for (int i = 0; i < balls.size() - 1; i++){
                        for (int j = i + 1; j < balls.size(); j++){
                            synchronized (balls.get(i)){
                                synchronized (balls.get(j)){
                                    Ball ball1 = balls.get(i);
                                    Ball ball2 = balls.get(j);
                                    if (isColl(ball1.x,ball2.x,ball1.y,ball2.y,ball1.rad,ball2.rad)){
                                        int vx1 = ball1.xSpeed;
                                        int vx2 = ball2.xSpeed;
                                        int vy1 = ball1.ySpeed;
                                        int vy2 = ball2.ySpeed;
                                        int m1 = ball1.weight;
                                        int m2 = ball2.weight;

                                        ball1.xSpeed = ((m1-m2)*vx1 + 2*m2*vx2) / (m1+m2);
                                        ball2.xSpeed = ((m2-m1)*vx2 + 2*m1*vx1) / (m1+m2);
                                        ball1.ySpeed = ((m1-m2)*vy1 + 2*m2*vy2) / (m1+m2);
                                        ball2.ySpeed = ((m2-m1)*vy2 + 2*m1*vy1) / (m1+m2);
                                        Random random = new Random();
                                        if (ball1.xSpeed == 0){
                                            if (random.nextInt() > 0)
                                                ball1.xSpeed += random.nextInt(3);
                                            else
                                                ball1.xSpeed -= random.nextInt(3);
                                        }
                                        if (ball1.ySpeed == 0){
                                            if (random.nextInt() > 0)
                                                ball1.ySpeed += random.nextInt(3);
                                            else
                                                ball1.ySpeed -= random.nextInt(3);
                                        }
                                        if (ball2.xSpeed == 0){
                                            if (random.nextInt() > 0)
                                                ball2.xSpeed += random.nextInt(3);
                                            else
                                                ball2.xSpeed -= random.nextInt(3);
                                        }
                                        if (ball2.ySpeed == 0){
                                            if (random.nextInt() > 0)
                                                ball2.ySpeed += random.nextInt(3);
                                            else
                                                ball2.ySpeed -= random.nextInt(3);
                                        }

                                        //将两个球分离，以防两个球一直粘在一起
                                        while (isColl(ball1.x,ball2.x,ball1.y,ball2.y,ball1.rad,ball2.rad)){
                                            if ((ball1.x + ball1.rad) - (ball2.x + ball2.rad) <= 0){
                                                ball1.x--;
                                                ball2.x++;
                                            }
                                            else {
                                                ball1.x++;
                                                ball2.x--;
                                            }
                                            if ((ball1.y + ball1.rad) - (ball2.y + ball2.rad) <= 0){
                                                ball1.y--;
                                                ball2.y++;
                                            }
                                            else {
                                                ball1.y++;
                                                ball2.y--;
                                            }
                                        }

                                        while (ball1.y < 0)
                                            ball1.y++;
                                        while(ball1.y + ball1.rad * 2 > getHeight() - 2)
                                            ball1.y--;
                                        while (ball1.x < 0)
                                            ball1.x++;
                                        while(ball1.x + ball1.rad * 2 > getWidth() - 2)
                                            ball1.x--;

                                        while (ball2.y < 0)
                                            ball2.y++;
                                        while(ball2.y + ball2.rad * 2 > getHeight() - 2)
                                            ball2.y--;
                                        while (ball2.x < 0)
                                            ball2.x++;
                                        while(ball2.x + ball2.rad * 2 > getWidth() - 2)
                                            ball2.x--;
                                    }
                                }
                            }
                        }
                    }
                    sleep(refreshTime);
                }
                catch (Exception e) {
                }
            }
        }
    }
    private class playerBallColl extends Thread implements Serializable{//玩家PVE战斗的线程
        
        private Ball player;

        playerBallColl(Ball player){
            this.player = player;
        }
        
        @Override
        public void run(){
            while (fighting && running){

                for (int i = 0; i < balls.size(); i++){
                    if (isColl(player.x,balls.get(i).x, player.y,balls.get(i).y, player.rad,balls.get(i).rad)){
                        if (player.rad >= balls.get(i).rad){
                            player.rad += balls.get(i).rad / 3;
                            balls.remove(i);
                            i--;

                            while (player.y < 0)
                                player.y++;
                            while(player.y + player.rad * 2 > getHeight() - 2)
                                player.y--;
                            while (player.x < 0)
                                player.x++;
                            while(player.x + player.rad * 2 > getWidth() - 2)
                                player.x--;

                            if (player.y < 0 || player.y + player.rad * 2 > getHeight() - 2 || player.x < 0 || player.x + player.rad * 2 > getWidth() - 2){
                                pause();
                                JOptionPane.showConfirmDialog(null, "You Win!!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                                System.exit(0);
                            }
                        }
                        else {
                            try {
                                sleep(refreshTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (game_mode.equals("pve")){
                                pause();
                                JOptionPane.showConfirmDialog(null, "You Lost!!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                                System.exit(0);
                            }
                            else {
                                pause();
                                if (player.color == Color.red)
                                    JOptionPane.showConfirmDialog(null, "Player1 Lost!!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                                else
                                    JOptionPane.showConfirmDialog(null, "Player2 Lost!!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                                System.exit(0);
                            }
                        }
                    }
                }

                try {
                    sleep(refreshTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class generateBalls extends Thread implements Serializable{

        private int gapTime;

        generateBalls(int gapTime){ this.gapTime = gapTime; }

        @Override
        public void run(){
            while (running){

                newBall();

                try {
                    sleep(gapTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    private class playersMove extends Thread implements Serializable{
        @Override
        public void run(){

            int speed = 3;

            if (game_mode.equals("pvp")){
                while (true){
                    if (P1up && player1.y > 0)//W
                        player1.y -= speed;
                    if (P1down && player1.y + player1.rad * 2 < getHeight() - 2)//S
                        player1.y += speed;
                    if (P1left && player1.x > 0)//A
                        player1.x -= speed;
                    if (P1right && player1.x + player1.rad * 2 < getWidth() - 2)//D
                        player1.x += speed;

                    if (P2up && player2.y > 0)//up
                        player2.y -= speed;
                    if (P2down && player2.y + player2.rad * 2 < getHeight() - 2)//down
                        player2.y += speed;
                    if (P2left && player2.x > 0)//left
                        player2.x -= speed;
                    if (P2right && player2.x + player2.rad * 2 < getWidth() - 2)//right
                        player2.x += speed;

                    if (isColl(player1.x,player2.x,player1.y,player2.y,player1.rad,player2.rad)){
                        if (player1.rad >= player2.rad){
                            try {
                                sleep(refreshTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            pause();
                            JOptionPane.showConfirmDialog(null, "Player1 Win!!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                            System.exit(0);
                        }
                        else {
                            try {
                                sleep(refreshTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            pause();
                            JOptionPane.showConfirmDialog(null, "Player2 Win!!", "Game Over", JOptionPane.PLAIN_MESSAGE);
                            System.exit(0);
                        }
                    }
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                while (true){
                    if (P1up && player1.y > 0)//W
                        player1.y -= speed;
                    if (P1down && player1.y + player1.rad * 2 < getHeight() - 2)//S
                        player1.y += speed;
                    if (P1left && player1.x > 0)//A
                        player1.x -= speed;
                    if (P1right && player1.x + player1.rad * 2 < getWidth() - 2)//D
                        player1.x += speed;
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }



        }
    }

    private final ArrayList<Ball> balls;
    private Ball player1;//1p
    private Ball player2;//2p
    private boolean running;//整个程序正在执行的boolean
    private boolean fighting;//玩家加入战斗的boolean
    private String game_mode;//游戏模式

    //用于监听键盘事件
    private boolean P1up;
    private boolean P1down;
    private boolean P1left;
    private boolean P1right;
    private boolean P2up;
    private boolean P2down;
    private boolean P2left;
    private boolean P2right;

    private int refreshTime;//检测碰撞，刷新画板的时间
    Thread repaintThread;//刷新画板的线程
    Thread borderCollThread;//边界碰撞检查的线程
    Thread ballCollThread;//小球互碰检测的线程
    Thread generateBallsThread;//产生小球的线程
    Thread Player1Thread;//玩家一战斗的线程
    Thread Player2Thread;//玩家二战斗的线程
    Thread PlayersMoveThread;//玩家移动的线程

    BallArea(String mode){
        balls = new ArrayList<>();
        running = true;
        fighting = false;
        P1up = false;
        P1down = false;
        P1left = false;
        P1right = false;
        game_mode = mode;
        refreshTime = 100;

        requestFocus();
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        //画p1
        //画球
        g2d.setColor(player1.color);
        g2d.fillOval(player1.x, player1.y, player1.rad * 2, player1.rad * 2);

        //画五角星
        double ch = 72 * Math.PI / 180;// 圆心角的弧度数
        int x1 = player1.x + player1.rad,
                x2 = (int) (player1.x - Math.sin(ch) * player1.rad) + player1.rad,
                x3 = (int) (player1.x + Math.sin(ch) * player1.rad) + player1.rad,
                x4 = (int) (player1.x - Math.sin(ch / 2) * player1.rad) + player1.rad,
                x5 = (int) (player1.x + Math.sin(ch / 2) * player1.rad) + player1.rad;
        int y1 = player1.y,
                y2 = (int) (player1.y - Math.cos(ch) * player1.rad) + player1.rad,
                y3 = (int) (player1.y + Math.cos(ch / 2) * player1.rad) + player1.rad;
        int bx = (int) (player1.x + Math.cos(ch) * Math.tan(ch / 2) * player1.rad) + player1.rad;

        Polygon a = new Polygon();
        Polygon b = new Polygon();
        a.addPoint(x2, y2);
        a.addPoint(x5, y3);
        a.addPoint(bx, y2);
        b.addPoint(x1, y1);
        b.addPoint(bx, y2);
        b.addPoint(x3, y2);
        b.addPoint(x4, y3);
        g2d.setColor(Color.yellow);
        g2d.fillPolygon(a);
        g2d.fillPolygon(b);

        if (game_mode.equals("pvp")){//画p2
            //画球
            g2d.setColor(player2.color);
            g2d.fillOval(player2.x, player2.y, player2.rad * 2, player2.rad * 2);

            //画五角星
            ch = 72 * Math.PI / 180;// 圆心角的弧度数
            x1 = player2.x + player2.rad;
            x2 = (int) (player2.x - Math.sin(ch) * player2.rad) + player2.rad;
            x3 = (int) (player2.x + Math.sin(ch) * player2.rad) + player2.rad;
            x4 = (int) (player2.x - Math.sin(ch / 2) * player2.rad) + player2.rad;
            x5 = (int) (player2.x + Math.sin(ch / 2) * player2.rad) + player2.rad;
            y1 = player2.y;
            y2 = (int) (player2.y - Math.cos(ch) * player2.rad) + player2.rad;
            y3 = (int) (player2.y + Math.cos(ch / 2) * player2.rad) + player2.rad;
            bx = (int) (player2.x + Math.cos(ch) * Math.tan(ch / 2) * player2.rad) + player2.rad;

            a = new Polygon();
            b = new Polygon();
            a.addPoint(x2, y2);
            a.addPoint(x5, y3);
            a.addPoint(bx, y2);
            b.addPoint(x1, y1);
            b.addPoint(bx, y2);
            b.addPoint(x3, y2);
            b.addPoint(x4, y3);
            g2d.setColor(Color.BLACK);
            g2d.fillPolygon(a);
            g2d.fillPolygon(b);
        }


        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);
            g2d.setColor(ball.color);
            g2d.fillOval(ball.x, ball.y, ball.rad * 2, ball.rad * 2);
        }
    }

    public void start(){
        if (!running){
            running = true;

            repaintThread = new Repainting();
            repaintThread.setDaemon(true);
            repaintThread.start();

            borderCollThread = new BorderColl();
            borderCollThread.setDaemon(true);
            borderCollThread.start();

            ballCollThread = new BallColl();
            ballCollThread.setDaemon(true);
            ballCollThread.start();

            generateBallsThread = new generateBalls(3000);
            generateBallsThread.setDaemon(true);
            generateBallsThread.start();

            Player1Thread = new playerBallColl(player1);
            Player1Thread.setDaemon(true);
            Player1Thread.start();

            if (game_mode.equals("pvp")) {
                Player2Thread = new playerBallColl(player2);
                Player2Thread.setDaemon(true);
                Player2Thread.start();
            }

            PlayersMoveThread = new playersMove();
            PlayersMoveThread.setDaemon(true);
            PlayersMoveThread.start();

            for (Ball ball : balls)
                ball.start();

            requestFocus();
        }
    }
    public void pause(){
        P1up = false;
        P1down = false;
        P1left = false;
        P1right = false;
        P2up = false;
        P2down = false;
        P2left = false;
        P2right = false;
        running = false;
    }
    public void newBall(){
        Random random = new Random();
        Color color = new Color(random.nextInt(200) + 56,random.nextInt(200) + 56,random.nextInt(200) + 56);
        int dia = random.nextInt(getHeight() / 10) + 5;
        int x = random.nextInt(getWidth() - 2 * dia);
        int y = random.nextInt(getHeight() - 2 * dia);
        int speed = random.nextInt(50) + 10;
        int xSpeed = random.nextInt() % 6;
        int ySpeed = random.nextInt() % 6;

        boolean goodPosition = false;
        while (!goodPosition){
            goodPosition = true;
            if (isColl(x,player1.x,y,player1.y,dia,player1.rad) || (game_mode.equals("pvp") && isColl(x,player2.x,y,player2.y,dia,player2.rad))){
                goodPosition = false;
            }
            else {
                for (Ball ball : balls){
                    if (isColl(x,ball.x,y,ball.y,dia,ball.rad)){
                        goodPosition = false;
                        break;
                    }
                }
            }
            if (!goodPosition){
                x = random.nextInt(getWidth() - 2*dia);
                y = random.nextInt(getHeight() - 2*dia);
            }
        }

        balls.add(new Ball(color,x,y,dia,speed,xSpeed,ySpeed));
        if (refreshTime > speed)
            refreshTime = speed;
    }
    public void PVEFight(int dia){
        Random random = new Random();
        int x = random.nextInt(getWidth() - dia - 50);
        int y = random.nextInt(getHeight() - dia - 50);

        boolean goodPosition = false;
        while (!goodPosition){
            goodPosition = true;
            for (Ball ball : balls){
                if (isColl(x,ball.x,y,ball.y,dia,ball.rad)){
                    goodPosition = false;
                    break;
                }
            }
            if (!goodPosition){
                x = random.nextInt(getWidth() - dia - 50);
                y = random.nextInt(getHeight() - dia - 50);
            }
        }

        player1 = new Ball(Color.red,x,y,dia,Integer.MAX_VALUE,0,0);
        fighting = true;

        repaintThread = new Repainting();
        repaintThread.setDaemon(true);
        repaintThread.start();

        borderCollThread = new BorderColl();
        borderCollThread.setDaemon(true);
        borderCollThread.start();

        ballCollThread = new BallColl();
        ballCollThread.setDaemon(true);
        ballCollThread.start();

        generateBallsThread = new generateBalls(3000);
        generateBallsThread.setDaemon(true);
        generateBallsThread.start();

        Player1Thread = new playerBallColl(player1);
        Player1Thread.setDaemon(true);
        Player1Thread.start();

        PlayersMoveThread = new playersMove();
        PlayersMoveThread.setDaemon(true);
        PlayersMoveThread.start();
    }
    public void PVPFight(int dia){
        Random random = new Random();
        //p1的生成
        int x = random.nextInt(getWidth() - dia - 50);
        int y = random.nextInt(getHeight() - dia - 50);

        boolean goodPosition = false;
        while (!goodPosition){
            goodPosition = true;
            for (Ball ball : balls){
                if (isColl(x,ball.x,y,ball.y,dia,ball.rad)){
                    goodPosition = false;
                    break;
                }
            }
            if (!goodPosition){
                x = random.nextInt(getWidth() - dia - 50);
                y = random.nextInt(getHeight() - dia - 50);
            }
        }

        player1 = new Ball(Color.red,x,y,dia,Integer.MAX_VALUE,0,0);


        //p2的生成
        x = random.nextInt(getWidth() - dia - 50);
        y = random.nextInt(getHeight() - dia - 50);

        goodPosition = false;
        while (!goodPosition){
            goodPosition = true;
            for (Ball ball : balls){
                if (isColl(x,ball.x,y,ball.y,dia,ball.rad)){
                    goodPosition = false;
                    break;
                }
            }
            if (isColl(x,player1.x,y,player1.y,dia,player1.rad))
                goodPosition = false;
            if (!goodPosition){
                x = random.nextInt(getWidth() - dia - 50);
                y = random.nextInt(getHeight() - dia - 50);
            }
        }

        player2 = new Ball(Color.BLUE,x,y,dia,Integer.MAX_VALUE,0,0);




        fighting = true;

        repaintThread = new Repainting();
        repaintThread.setDaemon(true);
        repaintThread.start();

        borderCollThread = new BorderColl();
        borderCollThread.setDaemon(true);
        borderCollThread.start();

        ballCollThread = new BallColl();
        ballCollThread.setDaemon(true);
        ballCollThread.start();

        generateBallsThread = new generateBalls(3000);
        generateBallsThread.setDaemon(true);
        generateBallsThread.start();

        Player1Thread = new playerBallColl(player1);
        Player1Thread.setDaemon(true);
        Player1Thread.start();

        Player2Thread = new playerBallColl(player2);
        Player2Thread.setDaemon(true);
        Player2Thread.start();

        PlayersMoveThread = new playersMove();
        PlayersMoveThread.setDaemon(true);
        PlayersMoveThread.start();
    }
    public void setPVEKeyEvent(){
        addKeyListener(new KeyAdapter() {

                           @Override
                           public void keyPressed(KeyEvent e) {
                               super.keyPressed(e);
                               if (running && fighting){
                                   super.keyPressed(e);
                                   switch (e.getKeyCode()) {
                                       case KeyEvent.VK_W, KeyEvent.VK_UP -> P1up = true;
                                       case KeyEvent.VK_S, KeyEvent.VK_DOWN -> P1down = true;
                                       case KeyEvent.VK_A, KeyEvent.VK_LEFT -> P1left = true;
                                       case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> P1right = true;
                                   }
                               }
                           }

                           @Override
                           public void keyReleased(KeyEvent e) {
                               if (fighting){
                                   switch (e.getKeyCode()) {
                                       case KeyEvent.VK_W, KeyEvent.VK_UP -> P1up = false;
                                       case KeyEvent.VK_S, KeyEvent.VK_DOWN -> P1down = false;
                                       case KeyEvent.VK_A, KeyEvent.VK_LEFT -> P1left = false;
                                       case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> P1right = false;
                                   }
                               }
                           }

                       }
        );
        requestFocus();
    }
    public void setPVPKeyEvent(){
        addKeyListener(new KeyAdapter() {

                           @Override
                           public void keyPressed(KeyEvent e) {
                               super.keyPressed(e);
                               if (running && fighting){
                                   super.keyPressed(e);
                                   switch (e.getKeyCode()) {
                                       case KeyEvent.VK_W  -> P1up = true;
                                       case KeyEvent.VK_UP -> P2up = true;
                                       case KeyEvent.VK_S  -> P1down = true;
                                       case KeyEvent.VK_DOWN -> P2down = true;
                                       case KeyEvent.VK_A -> P1left = true;
                                       case KeyEvent.VK_LEFT -> P2left = true;
                                       case KeyEvent.VK_D -> P1right = true;
                                       case KeyEvent.VK_RIGHT -> P2right = true;
                                   }
                               }
                           }

                           @Override
                           public void keyReleased(KeyEvent e) {
                               if (fighting){
                                   switch (e.getKeyCode()) {
                                       case KeyEvent.VK_W  -> P1up = false;
                                       case KeyEvent.VK_UP -> P2up = false;
                                       case KeyEvent.VK_S  -> P1down = false;
                                       case KeyEvent.VK_DOWN -> P2down = false;
                                       case KeyEvent.VK_A -> P1left = false;
                                       case KeyEvent.VK_LEFT -> P2left = false;
                                       case KeyEvent.VK_D -> P1right = false;
                                       case KeyEvent.VK_RIGHT -> P2right = false;
                                   }
                               }
                           }

                       }
        );
        requestFocus();
    }

    public String getGame_mode() {
        return game_mode;
    }

    public boolean isColl(int x1, int x2, int y1, int y2, int d1, int d2){
        return Math.pow((x1 + d1) - (x2 + d2),2) + Math.pow((y1 + d1) - (y2 + d2),2) <= Math.pow(d1 + d2,2);
    }


}
