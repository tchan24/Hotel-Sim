import java.util.concurrent.Semaphore;
import java.util.ArrayList;

public class HotelSimulation {

    //create bellhop and employee static Thread variables
    public static Thread bellhop1;
    public static Thread bellhop2;
    public static Thread frontDeskEmployee1;
    public static Thread frontDeskEmployee2;

    // create static SimpleLocks for bellhop and employee
    public static SimpleLock bellhopLock = new SimpleLock();
    public static SimpleLock frontDeskLock = new SimpleLock();

    // create initial roomNumber variable
    public static int roomNumber = 1;

    // create SimpleLock class that imitates ReentrantLock class
    static class SimpleLock {
        private Semaphore mutex = new Semaphore(1);
    
        public void lock() {
            try {
                mutex.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    
        public void unlock() {
            mutex.release();
        }
    }

    // create FrontDeskEmployee class that implements Runnable
    public static class FrontDeskEmployee implements Runnable {
        private int id;
        private static Semaphore frontDeskSemaphore = new Semaphore(0);  // Initialize to 0 so front desk employees wait
        private static ArrayList<Guest> guestsAtFrontDesk = new ArrayList<>();

        public FrontDeskEmployee(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    frontDeskSemaphore.acquire();  // Wait for a guest to register

                    Guest guest;
                    frontDeskLock.lock();
                    try {
                        if (!guestsAtFrontDesk.isEmpty()) {
                            guest = guestsAtFrontDesk.remove(0);
                        } else {
                            continue;
                        }
                    } finally {
                        frontDeskLock.unlock();
                    }

                    System.out.println("Front desk employee " + id + " registers guest " + guest.getId());
                    //Thread.sleep(500);  // Simulate some time to register the guest
                    System.out.println("Guest " + guest.getId() + " receives room key for room " + roomNumber + " from front desk employee " + id);
                    roomNumber++;
                    guest.getRoomSemaphore().release();  // Notify the guest that registration is done
                }
            } catch (InterruptedException e) {
                return;
            }
        }

        public static void addGuestToRegister(Guest guest) {
            frontDeskLock.lock();
            try {
                guestsAtFrontDesk.add(guest);
                frontDeskSemaphore.release();  // Notify a front desk employee that a guest is waiting
            } finally {
                frontDeskLock.unlock();
            }
        }
    }

    // create Bellhop class that implements Runnable
    public static class Bellhop implements Runnable {
        private int id;
        private static Semaphore bellhopSemaphore = new Semaphore(0);
        private static ArrayList<Guest> guestsNeedingBellhop = new ArrayList<>();

        public Bellhop(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    bellhopSemaphore.acquire();
                    Guest guest;
                    bellhopLock.lock();
                    try {
                        if (!guestsNeedingBellhop.isEmpty()) {
                            guest = guestsNeedingBellhop.remove(0);
                        } else {
                            continue;
                        }
                    } finally {
                        bellhopLock.unlock();
                    }

                    System.out.println("Bellhop " + id + " receives bags from guest " + guest.getId());
                    //Thread.sleep(1000); // Simulate some time to deliver the bags
                    guest.getRoomSemaphore().release();
                    System.out.println("Bellhop " + id + " delivers bags to guest " + guest.getId());
                    System.out.println("Guest " + guest.getId() + " receives bags from bellhop " + id + " and gives tip");
                    guest.getBagSemaphore().release();
                }
            } catch (InterruptedException e) {
                return;
            }
        }

        public static void addGuestRequiringBellhop(Guest guest) {
            bellhopLock.lock();
            try {
                guestsNeedingBellhop.add(guest);
                bellhopSemaphore.release();
            } finally {
                bellhopLock.unlock();
            }
        }
    }

    // create Guest class that implements Runnable
    public static class Guest implements Runnable {
        private int id;
        private Semaphore roomSemaphore = new Semaphore(0);
        private Semaphore bagSemaphore = new Semaphore(0);

        public Guest(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println("Guest " + id + " enters hotel with " + ((id % 3) + 1) + " bag(s)");
                FrontDeskEmployee.addGuestToRegister(this);  // Guest waits at the front desk to be registered
                roomSemaphore.acquire();  // Wait to be registered and get a room key

                System.out.println("Guest " + id + " requests help with bags");
                Bellhop.addGuestRequiringBellhop(this);
                bagSemaphore.acquire();
                System.out.println("Guest " + id + " enters room " + id);
                System.out.println("Guest " + id + " retires for the evening");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    // main method
    public static void main(String[] args) {
        // print Simulation starts
        System.out.println("Simulation starts");

        // create 2 front desk employee threads and print their creation
        frontDeskEmployee1 = new Thread(new FrontDeskEmployee(0));
        frontDeskEmployee2 = new Thread(new FrontDeskEmployee(1));
        frontDeskEmployee1.start();
        frontDeskEmployee2.start();
        System.out.println("Front desk employee 0 created");
        System.out.println("Front desk employee 1 created");

        // create 2 bellhop threads and print their creation
        bellhop1 = new Thread(new Bellhop(0));
        bellhop2 = new Thread(new Bellhop(1));
        bellhop1.start();
        bellhop2.start();
        System.out.println("Bellhop 0 created");
        System.out.println("Bellhop 1 created");

        // create 25 guest threads and print their creation
        ArrayList<Thread> guestThreads = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            Thread guestThread = new Thread(new Guest(i));
            guestThreads.add(guestThread);
            System.out.println("Guest " + i + " created");
            guestThread.start();
        }

        for (Thread thread : guestThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // interrupt all bellhop threads
        bellhop1.interrupt();
        bellhop2.interrupt();

        // print Simulation ends and exit
        System.out.println("Simulation ends");
        System.exit(0);
    }
}
