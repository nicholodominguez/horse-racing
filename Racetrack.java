import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.lang.Runnable;
import java.util.concurrent.atomic.AtomicInteger; 
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Racetrack{
    private final int ASCII_RANGE_MAX = 126; 
    private final int ASCII_RANGE_MIN = 32;    
    private final int MIN_HORSES = 2; 
    private final int BARN_LENGTH = 10;
        
    private List<Horse> horses;
    private volatile List<String> statuses;
    private ExecutorService executor;
    private Future lastHorse;
    private double trackLength;
    private int healthyHorseCount;
    private volatile CyclicBarrier gate;/*
    private Timer t;
    private TimerTask timertask;*/
    private Scanner sc;/*
    private AtomicBoolean allHorsesFinished;
    private AtomicBoolean allHorsesAtGate;
    private AtomicInteger lastHorse;*/
    private volatile boolean allHorsesFinished;
    private volatile boolean allHorsesAtGate;
    private volatile int lastHorse;
    
    public Racetrack(){
        this.horses = new ArrayList<Horse>();
        this.statuses = new ArrayList<String>();
        this.sc = new Scanner(System.in);
        /*this.allHorsesFinished = new AtomicBoolean(false);
        this.allHorsesAtGate = new AtomicBoolean(false);
        this.lastHorse = new AtomicInteger(-1);*/
        this.allHorsesFinished = false;
        this.allHorsesAtGate = false;
        this.lastHorse = -1;
        this.healthyHorseCount = 0;
        
        /*this.timertask = new TimerTask() {

            @Override
            public void run() {
                printMap();
                if(isRaceFinished()){
                    t.cancel();
                    t.purge();
                    System.exit(0);
                }
            }
        };*/
        
        //this.t = new Timer();
    }
    
    public void startRace(){
        if(this.healthyHorseCount > 1){
            this.gate = new CyclicBarrier(this.healthyHorseCount, new Runnable(){
                @Override
                public void run(){
                    prepareHorses();
                    countdown();
                }
            });
            this.executor = Executors.newFixedThreadPool(this.healthyHorseCount+1);
            for(Horse h : horses){
                h.setGate(this.gate);
                if(h.isHealthy())
                    executor.execute(h);
            }
            
            executor.shutdown();
            while(!executor.isTerminated()){
                lastHorse = executor.submit();
            }
            //this.t.scheduleAtFixedRate(this.timertask, 0, 1000);
        }
        else{
            System.out.println("No. of healthy horses (" + this.healthyHorseCount + ") too low. ");
        }   
    }    
    
    public void printSpeed(){
    
        //System.out.print("\033[H\033[2J");        
        for(Horse h : horses){
            System.out.println(h.getName() + ": "+ String.format("%.2f",h.getSpeed()) + " mps at " + String.format("%.2f",h.getLocation()) + " m");
        }
    }
   
    public void initHorses(){
        int input, track;
        
        this.trackLength = this.getInputInt("Enter track length: ", 0);
        
        input = this.getInputInt("Enter no. of horses(min. 2): ", 2);
        this.horses = new ArrayList<Horse>();
        for(int i=0; i < input; i++){
            this.addHorse();
        }
    }
    
    public int printMenu(){
        int input = 0;

        System.out.println("------------");
        System.out.println("[1] Add Horse");
        System.out.println("[2] Print Horse");
        System.out.println("[3] Start Race");
        System.out.println("[4] Reset");
        System.out.println("[5] Exit");
        System.out.println("------------");
        do{
            input = this.getInputInt("Option: ", 1);
            if(input < 1 || input > 5) System.out.println("Input too high or too low.");
        }while(input < 1 || input > 5);

        return input;    
    }
    
    public void addHorse(){
        String name;
        String warcry;
        String health;
        boolean healthy;
        Random r = new Random();
        
        name = getInputStr("Enter horse's name: ");
        warcry = getInputStr("Enter warcry: ");
        healthy = r.nextBoolean();
        //healthy = true;
        
        if(healthy){
            health = "healthy";
            this.healthyHorseCount++;
        }
        else
            health = "sick";
            
        System.out.println("A " + health + " horse named " + name + " is registered in the race.\n");
        
        this.horses.add(new Horse(name, warcry, this.trackLength, healthy));
    }
    
    public void printHorses(){
        for(Horse h : horses){
            String health = h.isHealthy()?"healthy":"sick";
            System.out.println(h.getName() + " (" + h.getWarcry() + ") is " + health);
        }
    
    }
    
    public void prepareHorses(){
        for(Horse h : horses){
            h.prepareToRace();
        }
    }
    
    public void countdown(){
        for(int i = 4; i >= 0; i--){
            System.out.println(System.currentTimeMillis() + ": Race is starting in "+(i+1)+"...");
        }
    }
    
    /*public void printMap(){
        String str;
        System.out.print("\033[H\033[2J");
        statuses.clear();
        
        
        /*for(Horse h : horses){
            if(h.isHealthy()){
                int at = (int) (h.getBarnLocation() + h.getLocation());
                if(at < this.lastHorse.get()) this.lastHorse.set(at);
                if(this.lastHorse.get() >= (this.trackLength + this.BARN_LENGTH)) this.allHorsesFinished.set(true);
                str = at >= (this.trackLength + this.BARN_LENGTH) ? h.getWarcry() : ""; 
                String loc = this.stringMaker(at, str);
                System.out.println(loc);
                statuses.add(h.getName() + ": " + h.getStatus()+" "+ this.allHorsesFinished.get() + " " + this.lastHorse.get());
                //statuses.add(h.getName() + ": " + h.getStatus()+" "+ h.getBarnLocation() + " " + h.timeStarted);
            }
        }
        
        for(Horse h : horses){
            if(h.isHealthy()){
                int at = (int) (h.getBarnLocation() + h.getLocation());
                if(at < this.lastHorse) this.lastHorse = at;
                if(this.lastHorse >= (this.trackLength + this.BARN_LENGTH)) this.allHorsesFinished = true;
                str = at >= (this.trackLength + this.BARN_LENGTH) ? h.getWarcry() : ""; 
                String loc = this.stringMaker(at, str);
                System.out.println(loc);
                statuses.add(h.getName() + ": " + h.getStatus()+" "+ this.allHorsesFinished + " " + this.lastHorse);
                //statuses.add(h.getName() + ": " + h.getStatus()+" "+ h.getBarnLocation() + " " + h.timeStarted);
            }
        }
        
        for(String s : statuses){
            System.out.println(s);
        }*/
        
        //System.out.println(this.healthyHorseCount);
    //}
    
    /*public String stringMaker(int at, String name){
        StringBuilder st = new StringBuilder("|");
        String add;
        
        for(int i = 0; i < this.BARN_LENGTH + this.trackLength; i++){
            if(i == this.BARN_LENGTH){
                st.append("|");
                continue;    
            }
            add = i <= at ? (at == i ? "*" : "-" ) : " ";
            st.append(add);
        }
        st.append("|\t"+name);
        return st.toString();    
    }*/
    
    public boolean isRaceFinished(){
        //return this.allHorsesFinished.get();
        return this.allHorsesFinished;
    }
    
    public int getInputInt(String msg, int floor){
        boolean isAlpha = true;
        int row = 0;
        
        while(isAlpha){
            try{
                System.out.print(msg);
                row = Integer.parseInt(sc.nextLine());
                if(row < floor){
                    System.out.println("Integer too low, it should be greater than "+this.MIN_HORSES);    
                }
                else isAlpha = false;
            }catch(NumberFormatException e){
                isAlpha = true;
                System.out.println("Input not an integer");
            }
        }    
        return row;    
    }

    public String getInputStr(String msg){
        boolean isValid = false;
        String input = "";

        while(!isValid){
            System.out.print(msg);
            if(sc.hasNextLine()){
                input = sc.nextLine();
                if(input.length() <= 0){
                    System.out.println("Input too short.");               
                }
                else{
                    Character c = containsInvalidChar(input);
                    if(c != null){
                        System.out.println("Invalid character " + c);                            
                    }
                    else isValid = true;
                }
            }           
        }

        return input;     
    }

    public String getInputStr(String msg, int size){
        boolean isValid = false;
        String input = "";

        while(!isValid){
            System.out.print(msg);
            if(sc.hasNextLine()){
                input = sc.nextLine();
                if(input.length() <= 0){
                    System.out.println("Input too short");               
                }
                else{
                    Character c = containsInvalidChar(input);
                    if(c != null){
                        System.out.println("Invalid character " + c);                            
                    }
                    else isValid = true;
                }
            }           
        }

        return input;     
    }
    
    public Character containsInvalidChar(String input){
        int inputLen = input.length();

        for(int i = 0; i < inputLen; i++){
            int ascii = (int)input.charAt(i);             
            if(ascii < ASCII_RANGE_MIN || ascii > ASCII_RANGE_MAX){
                return input.charAt(i);
            }
        }

        return null;
    }
} 
