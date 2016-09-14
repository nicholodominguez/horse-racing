import java.util.*;

public class Race{
    public static void main(String[] args){
        Racetrack track = new Racetrack();
        int choice;
        
        //track.addHorses();
        //track.printHorses();
        track.initHorses();
        
        do{          
            choice = track.printMenu();	      
            switch(choice){
                case 1:
                    track.addHorse();
                    break;
                case 2:
                    track.printHorses();
                    break;
                case 3:
                    track.startRace();
                    break;
                case 4:
                    //tg.initTable();
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Invalid choice");
            }
            //tg.saveTable();           
        }while(choice != 5);
    }
}
