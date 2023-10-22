import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class HotelSimulation {

    private static final int TOTAL_GUESTS = 25;
    private static final int TOTAL_ROOMS = 25;
    private static final AtomicInteger currentRoomNumber = new AtomicInteger(0);
    private static final Semaphore frontDeskSemaphore = new Semaphore(2); // Two front desk employees
    private static final Semaphore bellhopSemaphore = new Semaphore(2); // Two bellhops

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Simulation starts");
        
        // Create threads for front desk employees
        Thread frontDesk1 = new Thread(new FrontDesk(0));
        Thread frontDesk2 = new Thread(new FrontDesk(1));
        frontDesk1.start();
        frontDesk2.start();
        
        // Create threads for bellhops
        Thread bellhop1 = new Thread(new Bellhop(0));
        Thread bellhop2 = new Thread(new Bellhop(1));
        bellhop1.start();
        bellhop2.start();
        
        // Create threads for guests
        Thread[] guests = new Thread[TOTAL_GUESTS];
        for (int i = 0; i < TOTAL_GUESTS; i++) {
            guests[i] = new Thread(new Guest(i));
            guests[i].start();
            guests[i].join();
        }
        
        System.out.println("Simulation ends");
    }
    
    static class Guest implements Runnable {
        private final int id;
        private final int bags;

        public Guest(int id) {
            this.id = id;
            this.bags = (int) (Math.random() * 6); // Randomly assigns bags between 0 and 5
            System.out.println("Guest " + id + " created");
        }

        @Override
        public void run() {
            try {
                System.out.println("Guest " + id + " enters hotel with " + bags + " bag" + (bags != 1 ? "s" : ""));
                frontDeskSemaphore.acquire();
                
                // Front desk logic here
                int room = currentRoomNumber.incrementAndGet();
                System.out.println("Front desk employee registers guest " + id + " and assigns room " + room);
                System.out.println("Guest " + id + " receives room key for room " + room + " from front desk employee");
                frontDeskSemaphore.release();
                
                if (bags > 2) {
                    bellhopSemaphore.acquire(); // Bellhop is only needed if guest has more than 2 bags
                    System.out.println("Guest " + id + " requests help with bags");
                    // Bellhop logic here
                    bellhopSemaphore.release();
                }
                
                System.out.println("Guest " + id + " enters room " + room); // Guest enters room
                
                if (bags > 2) {
                    System.out.println("Guest " + id + " receives bags and gives tip"); //
                }
                
                System.out.println("Guest " + id + " retires for the evening");
                System.out.println("Guest " + id + " joined");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    static class FrontDesk implements Runnable {
        private final int id;

        public FrontDesk(int id) {
            this.id = id;
            System.out.println("Front desk employee " + id + " created");
        }

        @Override
        public void run() {
            // Logic to serve guests
        }
    }

    class Bellhop implements Runnable {
    private int id;
    private Semaphore bellhopSemaphore;
    private Semaphore guestSemaphore;
    private Queue<Guest> queue;
    
    public Bellhop(int id, Semaphore bellhopSemaphore, Semaphore guestSemaphore, Queue<Guest> queue) {
        this.id = id;
        this.bellhopSemaphore = bellhopSemaphore;
        this.guestSemaphore = guestSemaphore;
        this.queue = queue;
    }
    
    @Override
    public void run() {
        System.out.println("Bellhop " + id + " created");
        try {
            while(true) {
                bellhopSemaphore.acquire();
                Guest guest = queue.poll();
                if(guest == null) {
                    bellhopSemaphore.release();
                    break;
                }
                System.out.println("Bellhop " + id + " receives bags from guest " + guest.getId());
                guestSemaphore.release();
                
                guest.getRoomSemaphore().acquire();
                System.out.println("Bellhop " + id + " delivers bags to guest " + guest.getId());
                System.out.println("Guest " + guest.getId() + " receives bags from bellhop " + id + " and gives tip");
                guest.getBagSemaphore().release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

}
