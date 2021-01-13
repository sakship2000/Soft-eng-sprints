/* 
    HogwartsHobo Game
    Mar. 30, 2020
*/

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class HogwartsHobo {
    
    public static double HEALTH = 0.14;
    public static long convert;
    public static void main(String[] args) {
        setup();
        // loop that creates the drawing of frames of game and sets up interaction
        boolean justPressedMouse = false;
        while(true) {
            draw();
            if (StdDraw.mousePressed()) {
                if (!justPressedMouse) mouseClicked();
                mousePressed();
                justPressedMouse = true;
            } else {
                justPressedMouse = false;
            }
            StdDraw.show(20);
        }
    }
    
    
    //***********************************************
    // Objects needed for the running of the game
    //***********************************************
    static Hobo hobo = null;
    static Track[] tracks = null;
    static GameTimer timer = new GameTimer();
    static long currentTime = 0;
    
    
    ///////////////////////////////////////////////
    //   Methods to run game
    ///////////////////////////////////////////////
    
   //method setup() called to initialize game
    public static void setup() {
        //Create 9 tracks and the hobo
        hobo = new Hobo(0.5, 0);
        tracks = new Track[9];
        tracks[0] = new Track(0.1);
        tracks[1] = new Track(0.2);
        tracks[2] = new Track(0.3);   
        tracks[3] = new Track(0.4);
        tracks[4] = new Track(0.5);
        tracks[5] = new Track(0.6);   
        tracks[6] = new Track(0.7);
        tracks[7] = new Track(0.8);
        tracks[8] = new Track(0.9);
    }
    
    public static int time;
    public static boolean death = false;
    // each frame of animation is drawn
    public static void draw() {
        StdDraw.clear(StdDraw.GRAY);
        if(death == false){
        StdDraw.setPenColor(Color.white);
        currentTime = timer.getTimeElapsed();
        StdDraw.text(0.84, 0.97, "Time Spent Alive:" + Long.toString(currentTime));
        }
        // draw and move the hobo based on the pressed key
        hobo.draw();
        // controls the movement of the hobo
        if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) { //the following are directions...
            hobo.move(Hobo.LEFT);
        } else if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) { //hobo can move
            hobo.move(Hobo.RIGHT);
        } else if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) { 
            hobo.move(Hobo.UP);
        } else if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) { 
            hobo.move(Hobo.DOWN);
        }
        
        // creates health status bar
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.filledRectangle( 0.15, 0.97, 0.14, 0.02);
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.fillHealthBar( 0.01, 0.99, HEALTH, 0.02);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.rectangle( 0.15, 0.97, 0.14, 0.02);
                        
        // check if the hobo is impacting anything on the track, handles healthbar if hit, detects hobo picked up a plane  
        for (int i = 0; i < tracks.length; i++) {
            if (HEALTH <= 0.0028){
                gameOver();
            }
            else if (tracks[i].detectImpact(hobo)){
                if (HEALTH > 0.0028){
                HEALTH = HEALTH - 0.0028; //handles decrease in health 
                }
            }
            tracks[i].draw();
            tracks[i].step();
            if (tracks[i].detectImpact2(hobo)){tracks[i].detectImpact3(hobo);};   
        }
        //Drawing Planes
        int index; 
        index = (int) Math.random() * 1;
        tracks[index].draw2(); 
        tracks[index].spawnPlane();
    }

    
    //Displays game over screen
    public static void gameOver(){
        death = true;
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(0.5, 0.5, "GAME OVER");
        StdDraw.text(0.5, 0.46, "Better luck next time, pal :(");
        StdDraw.text(0.5, 0.35, "You survived for " + currentTime + " seconds.");
    }
    
    // deals with the case when mouse is pressed
    public static void mousePressed() {
        // empty
    }
    
    // deals with the case when the mouse is clicked
    public static void mouseClicked() {
        // empty
    }
    
    
}  // end HogwartsHobo



//Hobo Class 
class Hobo {
    
    private double x;
    private double y;
    private double size;
    private double stepSize;
    private int numLives;
    
    //constants for movement
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;
    
    // creates a hobo at given location (x,y)
    public Hobo(double x, double y) {
        this.x = x;
        this.y = y;
        size = 0.01;
        stepSize = 0.015;
        numLives = 3;
    }
    
    // getter for x-coordinate
    public double getX() { return x; }
    
    // getter for y-coordinate
    public double getY() { return y; }
    
    // constructs hobo
    public void draw() {
        StdDraw.setPenColor(new Color(139, 69, 19));
        StdDraw.filledCircle(x, y, size);
    }
    
    // moves hobo
    // direction = up/down/left/right
    public void move(int direction) {
        switch (direction) {
            case LEFT:  
                if (x > 0) x -= stepSize; 
                break;
            case RIGHT: 
                if (x < 1) x += stepSize; 
                break;
            case UP:    
                if (y < 1) y += stepSize; 
                break;
            case DOWN:  
                if (y > 0) y -= stepSize; 
                break;
        }
            
    }
    
} // end Hobo


//Track Class
class Track {
    
    private int direction;  //uses the LEFT direction 
    public static final int LEFT = 0;
    
    private ArrayList<Train> trains = new ArrayList<Train>();
    private ArrayList<Plane> planes = new ArrayList<Plane>();
    private double yPosition;
    private double width;
    private double speed;
    private boolean generateNewTrains;
    private double plane;
    private boolean planeExist = false;
    
    // create track at the given y-position
    public Track(double yPosition) {
        this.yPosition = yPosition;
        width = 0.3;
        direction = 1; 
        speed = 0.08;
        generateNewTrains = true;
    }
    
    // create tracks, along with trains
    public void draw() {
        // create all trains in a track
        for (int i = 0; i < trains.size(); i++) {
            Train c = trains.get(i);
            c.draw();
        }
    }

    //Plane frequency 
    public double plane(){
        plane = Math.random() * 1000; 
        return plane;
    }
    //draw planes
    public void draw2()
    {
            if ( 0 < planes.size()) {
                Plane p = planes.get(0);
                p.draw();
            }
    }
    //Spawns plane if there is none 
    public void spawnPlane() {
        double planeLoc = plane();
        if( planeLoc <= 11){
        double x;
        x = Math.random() + 0.5;
        if(x > 1) x = 1;
        yPosition = Math.random(); 
        Plane plane = new Plane(x, yPosition,  .037);
        if(!planeExist)
        planes.add(plane);
        planeExist = true;
        }
    }
    
    // step trains in all track
    public void step() {
        
        // add new trains with some probability
        if (generateNewTrains && Math.random() < 0.005) {
            
            double x = 0, dx = 0;
            if (direction == LEFT) {
                x = 1.2;
                dx = -speed;
            } else {
                x = -20;
                dx = speed;
            }
            
            Train c = new Train(x, yPosition, width / 2, dx);
            trains.add(c);
        }
        
        // move all trains
        for (int i = 0; i < trains.size(); i++) {
            Train c = trains.get(i);
            c.move();
            if (c.getX() < -20 || c.getX() > 1.5) {
                trains.remove(i);
                i--;
            }
        }
       
        // option to switch Train direction
        // currently set to one direction only
        if (Math.random() < 0.01) {
            // switch direction
            direction = 1;
            generateNewTrains = false;  // temporarily disable new trains
        }
        
        // test to see if it is ok to resume Train generation
        if (trains.isEmpty()) {
            generateNewTrains = true;
        }
    }
    
    // detect impacts with the given hobo object
    public boolean detectImpact(Hobo f) {
        for (int i = 0; i < trains.size(); i++) {
            Train c = trains.get(i);
            if (c.detectImpact(f)) return true;
        }
        return false;
    }
    // detect if hobo picked up a plane obect
    public boolean detectImpact2(Hobo f) {
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            if (p.detectImpact2(f)){  
                planes.remove(0);
                planeExist = false;
                return true;
            }
        }
        return false;
    }
    // Paint where the upcoming trains are coming from
    public void detectImpact3(Hobo f) {
        for (int i = 0; i < trains.size();i++) {
            Train c = trains.get(i);
            StdDraw.setPenColor(Color.GREEN);
            StdDraw.filledRectangle(1, c.getY(), .3, .3);
        }  
    }
        
} // end track

//Paper Airplane Hint Objects
class Plane {
    private double x;
    private double y;
    private double size;
    private Color color;
    //Spawns plane at x,y coordinates and at specified size with a random color
    public Plane(double x,double y, double size){
        this.x = x;
        this.y = y;
        this.size = size;
        color = Color.white;
    }

    // getting for x-coordinate
    public double getX() { return x; }

    // draw plane
    public void draw() {
        StdDraw.setPenColor(color);
        StdDraw.filledRectangle(x, y, size, size);
    }
    //Detect if hobo is in range to pick up plane
    public boolean detectImpact2(Hobo f) {
        double fx = f.getX();
        double fy = f.getY();
        
        double minX = x - size;
        double maxX = x + size;
        double minY = y - size / 3;
        double maxY = y + size;
        
        return (minX <= fx && fx <= maxX && minY <= fy && fy <= maxY);
    }
}



class Train {
    
    // initializes private train variables
    private double x;
    private double y;
    private double size;
    private double dx;
    private Color color;
    
    // creates train at the given location (x,y) with size and velocity
    public Train(double x, double y, double size, double dx) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.dx = dx;
        color = new Color(165, 42, 42);
    }
    
    // getter for x-coordinate
    public double getX() { return x; }
    public double getY() { return y; }
        
    // creates train
    public void draw() {
        StdDraw.setPenColor(color);
        StdDraw.filledRectangle(x, y, size + 0.15, size / 3);
    }
    
    // moves train
    public void move() {
        x += dx;
    }
    
    // checks for any impact with the hobo object
    public boolean detectImpact(Hobo f) {
        double fx = f.getX();
        double fy = f.getY();
        
        double minX = x - (size + 0.15);
        double maxX = x + (size + 0.15);
        double minY = y - size / 3;
        double maxY = y + size / 3;
        
        return (minX <= fx && fx <= maxX && minY <= fy && fy <= maxY);
    }

    public boolean detectImpact3(Hobo f) {
        double fx = f.getX();

        double maxX = x + (30000000);

        
        return (fx <= maxX);
    }

    

} // end Train

//GameTimer objects, counts amount time hobo is alive
class GameTimer {
    long x = 0;

    public GameTimer() {
        x = System.nanoTime(); //time when GameTimer is created
    }

    //returns elapsed time since GameTimer was created
    public long getTimeElapsed() {
        long elapsedTime = (System.nanoTime() - x); //current time - time of creation of object
        long convert = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
        return convert;
    }
    
}


