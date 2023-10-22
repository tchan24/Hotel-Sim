import java.util.concurrent.Semaphore;
import java.util.ArrayList;

public class HotelSimulation {

    public static class Bellhop implements Runnable {
        private int id;
        private Semaphore bellhopSemaphore;
        private static ArrayList<Guest> guestsNeedingBellhop = new ArrayList<>();

        public Bellhop(int id) {
            this.id = id;
            this.bellhopSemaphore = new Semaphore(1);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    bellhopSemaphore.acquire();
                    Guest guest = getGuestRequiringBellhop();
                    if (guest == null) continue;  // No guest found, continue to next iteration

                    System.out.println("Bellhop " + id + " receives bags from guest " + guest.getId());
                    guest.getRoomSemaphore().acquire();
                    System.out.println("Bellhop " + id + " delivers bags to guest " + guest.getId());
                    System.out.println("Guest " + guest.getId() + " receives bags from bellhop " + id + " and gives tip");
                    guest.getBagSemaphore().release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public static synchronized Guest getGuestRequiringBellhop() {
            if (!guestsNeedingBellhop.isEmpty()) {
                return guestsNeedingBellhop.remove(0);
            }
            return null;
        }

        public static synchronized void addGuestRequiringBellhop(Guest guest) {
            guestsNeedingBellhop.add(guest);
        }
    }

    public static class Guest {
        private int id;
        private Semaphore roomSemaphore;
        private Semaphore bagSemaphore;

        public Guest(int id) {
            this.id = id;
            this.roomSemaphore = new Semaphore(0);
            this.bagSemaphore = new Semaphore(0);
        }

        public int getId() {
            return id;
        }

        public Semaphore getRoomSemaphore() {
            return roomSemaphore;
        }

        public Semaphore getBagSemaphore() {
            return bagSemaphore;
        }
    }

    public static void main(String[] args) {
        // Starting bellhops
        Thread bellhop1 = new Thread(new Bellhop(0));
        Thread bellhop2 = new Thread(new Bellhop(1));
        bellhop1.start();
        bellhop2.start();

        // Simulate guest creation and operations
        ArrayList<Guest> guests = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            Guest guest = new Guest(i);
            guests.add(guest);
            
            System.out.println("Guest " + guest.getId() + " created");
            // Some simulated operations here...
            Bellhop.addGuestRequiringBellhop(guest);  // Add guest for bellhop assistance
        }

        // You can continue with other simulation steps...
    }
}
