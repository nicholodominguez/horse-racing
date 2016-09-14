import java.util.*;
import java.lang.Runnable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Callable;
import java.util.concurrent.BrokenBarrierException;

public class Horse implements Runnable, Callable{
    public static final double MIN_SPEED = 1.0; 
    public static final double MAX_SPEED = 10.0;
    public static final double BARN_LENGTH = 10.0;
    private double trackLength;
    private String horseName;
    private String warcry;
    private boolean isHealthy;
    private volatile boolean isLast;
    private volatile boolean raceStarted;
    private volatile double location;
    private volatile double barnLocation;
    private volatile double speed;
    private Random r;
    private volatile CyclicBarrier gate;
    public long timeStarted;
    
    public Horse(String name, String warcry, double trackLength, boolean isHealthy){
        this.horseName = name;
        this.warcry = warcry;
        this.isHealthy = isHealthy;
        this.r = new Random();
        this.location = 0;
        this.barnLocation = 0;
        this.trackLength = trackLength;
        this.isLast = false;
        this.raceStarted = true;
    }
    
    @Override
    public void run(){
       
        try{
            gate.await();
            this.timeStarted = System.currentTimeMillis();
            while(!atGate()){
                updateSpeed();
                updateLocation();
                System.out.println(System.currentTimeMillis() + ": " + horseName + " walking to gate. Distance left: " + String.format("%.2f",(BARN_LENGTH - barnLocation)));
            }
            System.out.println(System.currentTimeMillis() + ": " + horseName + " waiting at gate");
            gate.await();
            while(!finished()){
                updateSpeed();
                updateLocation();
                System.out.println(System.currentTimeMillis() + ": " + horseName + " racing. Distance left: " + String.format("%.2f",(trackLength - location)));
            }
            System.out.println(System.currentTimeMillis() + ": " + horseName + " finished");
        }catch(InterruptedException e){
            System.out.println("Horse " + horseName + " is interrupted");        
        }catch(BrokenBarrierException e){
            System.out.println("Horse " + horseName + " is interrupted");        
        }      
    }
    
    @Override
    public double call() {
        double location = 0;
        try {
            location =  this.location;
        } catch (InterruptedException e) {
            System.out.println("Horse " + horseName + " is interrupted"); 
        }

        return location;
    }
    
    public void setGate(CyclicBarrier gate){
        this.gate = gate;
    }
    
    public double getSpeed(){
        return this.speed;
    }
    
    public String getName(){
        return this.horseName;
    }
    
    public String getWarcry(){
        return this.warcry;
    }
    
    public boolean isHealthy(){
        return this.isHealthy;
    }
    
    public void prepareToRace(){
        this.raceStarted = !this.raceStarted; 
    }
    
    public double getTrackLength(){
        return this.trackLength;
    }
    
    public double getBarnLength(){
        return this.BARN_LENGTH;
    }
    
    public double getBarnLocation(){
        return this.barnLocation;
    }
    
    public double getLocation(){
        return this.location;
    }
    
    public boolean finished(){
        if(this.getLocation() >= this.getTrackLength()){
            return true;
        }
        else return false;
    }
    
    public boolean atGate(){
        if(this.getBarnLocation() >= this.getBarnLength()){
            this.barnLocation = this.BARN_LENGTH;
            return true;
        }
        else return false;    
    }
    
    public void updateSpeed(){
        this.speed = ((this.MAX_SPEED - this.MIN_SPEED) * r.nextDouble()) + this.MIN_SPEED;
    }
    
    public void updateLocation(){
        if(this.raceStarted){
            this.location += this.speed;
            if(this.location >= this.trackLength) this.location = this.trackLength;
        }
        else{
            this.barnLocation += this.speed;
            if(this.barnLocation >= this.BARN_LENGTH) this.barnLocation = this.BARN_LENGTH;
        }
    }
    
    /*public void countdown(){
        for(int i = 0; i < 4; i++){
            System.out.println(System.currentTimeMillis() + ": Race is starting in "+(i+1)+"...");
        }
    }*/
}
