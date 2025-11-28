import java.util.*;

// ================= PASSENGER CLASS =================

class Passenger {
    private String name;
    private int age;
    private String gender;
    private String berthPreference;
    private String allottedBerth;
    private String ticketId;

    public Passenger(String name, int age, String gender, String berthPreference, String allottedBerth, String ticketId) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.berthPreference = berthPreference;
        this.allottedBerth = allottedBerth;
        this.ticketId = ticketId;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getBerthPreference() { return berthPreference; }
    public String getAllottedBerth() { return allottedBerth; }
    public String getTicketId() { return ticketId; }

    public void setAllottedBerth(String berth) {
        this.allottedBerth = berth;
    }

    @Override
    public String toString() {
        return "Ticket ID: " + ticketId +
                ", Name: " + name +
                ", Age: " + age +
                ", Gender: " + gender +
                ", Preference: " + berthPreference +
                ", Berth: " + allottedBerth;
    }
}


// ================= TICKET SYSTEM CLASS =================

class TicketSystem {
    private static final int TOTAL_LOWER = 2;
    private static final int TOTAL_MIDDLE = 2;
    private static final int TOTAL_UPPER = 2;
    private static final int TOTAL_RAC = 2;
    private static final int TOTAL_WAITING = 2;

    private int availableLower = TOTAL_LOWER;
    private int availableMiddle = TOTAL_MIDDLE;
    private int availableUpper = TOTAL_UPPER;
    private int availableRAC = TOTAL_RAC;
    private int availableWaiting = TOTAL_WAITING;

    private final Queue<Passenger> racQueue = new LinkedList<>();
    private final Queue<Passenger> waitingListQueue = new LinkedList<>();
    private final Map<String, Passenger> confirmedPassengers = new LinkedHashMap<>();
    private int ticketCounter = 1;

    public void bookTicket(String name, int age, String gender, String berthPreference) {
        String ticketId = "T" + ticketCounter++;
        Passenger passenger;

        if (availableLower + availableMiddle + availableUpper > 0) {

            String allocatedBerth = allocateBerth(age, gender, berthPreference);

            if (allocatedBerth != null) {
                passenger = new Passenger(name, age, gender, berthPreference, allocatedBerth, ticketId);
                confirmedPassengers.put(ticketId, passenger);
                decrementBerth(allocatedBerth);
                System.out.println("✓ Ticket Confirmed: " + passenger);
            } else {
                System.out.println("✗ Error allocating berth.");
            }

        } else if (availableRAC > 0) {

            passenger = new Passenger(name, age, gender, berthPreference, "RAC", ticketId);
            racQueue.offer(passenger);
            availableRAC--;
            System.out.println("⊙ Ticket in RAC: " + passenger);

        } else if (availableWaiting > 0) {

            passenger = new Passenger(name, age, gender, berthPreference, "WL", ticketId);
            waitingListQueue.offer(passenger);
            availableWaiting--;
            System.out.println("⊙ Ticket in Waiting List: " + passenger);

        } else {
            System.out.println("✗ No tickets available.");
        }
    }

    private String allocateBerth(int age, String gender, String preference) {
        if ((age >= 60 || gender.equalsIgnoreCase("female")) && availableLower > 0)
            return "L";

        if (preference.equals("L") && availableLower > 0) return "L";
        if (preference.equals("M") && availableMiddle > 0) return "M";
        if (preference.equals("U") && availableUpper > 0) return "U";

        if (availableLower > 0) return "L";
        if (availableMiddle > 0) return "M";
        if (availableUpper > 0) return "U";

        return null;
    }

    private void decrementBerth(String berth) {
        switch (berth) {
            case "L": availableLower--; break;
            case "M": availableMiddle--; break;
            case "U": availableUpper--; break;
        }
    }

    private void incrementBerth(String berth) {
        switch (berth) {
            case "L": availableLower++; break;
            case "M": availableMiddle++; break;
            case "U": availableUpper++; break;
        }
    }

    public void cancelTicket(String ticketId) {
        Passenger removed = confirmedPassengers.remove(ticketId);

        if (removed != null) {
            incrementBerth(removed.getAllottedBerth());
            System.out.println("✓ Cancelled Ticket: " + ticketId);

            if (!racQueue.isEmpty()) {
                Passenger racPassenger = racQueue.poll();
                availableRAC++;

                String newBerth = allocateBerth(racPassenger.getAge(), racPassenger.getGender(), racPassenger.getBerthPreference());
                racPassenger.setAllottedBerth(newBerth);

                confirmedPassengers.put(racPassenger.getTicketId(), racPassenger);
                decrementBerth(newBerth);

                System.out.println("↑ RAC moved to Confirmed: " + racPassenger);

                if (!waitingListQueue.isEmpty()) {
                    Passenger waitingPassenger = waitingListQueue.poll();
                    availableWaiting++;

                    waitingPassenger.setAllottedBerth("RAC");
                    racQueue.offer(waitingPassenger);
                    availableRAC--;

                    System.out.println("↑ WL moved to RAC: " + waitingPassenger);
                }
            }

        } else {
            System.out.println("✗ Ticket ID not found.");
        }
    }

    public void printBookedTickets() {
        System.out.println("\n=== Confirmed Tickets ===");
        if (confirmedPassengers.isEmpty()) System.out.println("None");
        else confirmedPassengers.values().forEach(System.out::println);
    }

    public void printAvailableTickets() {
        System.out.println("\n=== Available Tickets ===");
        System.out.println("Lower: " + availableLower);
        System.out.println("Middle: " + availableMiddle);
        System.out.println("Upper: " + availableUpper);
        System.out.println("RAC: " + availableRAC);
        System.out.println("Waiting: " + availableWaiting);
    }

    public void viewRacTickets() {
        System.out.println("\n=== RAC Tickets ===");
        if (racQueue.isEmpty()) System.out.println("None");
        else racQueue.forEach(System.out::println);
    }

    public void viewWaitingListTickets() {
        System.out.println("\n=== Waiting List ===");
        if (waitingListQueue.isEmpty()) System.out.println("None");
        else waitingListQueue.forEach(System.out::println);
    }
}


// ================= MAIN (TICKET BOOKING) =================

public class TicketBooking {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TicketSystem ts = new TicketSystem();

        while (true) {
            System.out.println("\n--- Railway Booking System ---");
            System.out.println("1. Book Ticket");
            System.out.println("2. Cancel Ticket");
            System.out.println("3. View Confirmed Tickets");
            System.out.println("4. View Available Tickets");
            System.out.println("5. View RAC Tickets");
            System.out.println("6. View Waiting List Tickets");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            int ch = scanner.nextInt();
            scanner.nextLine();

            switch (ch) {
                case 1:
                    System.out.print("Name: ");
                    String name = scanner.nextLine();

                    System.out.print("Age: ");
                    int age = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Gender: ");
                    String gender = scanner.nextLine();

                    System.out.print("Berth Preference (L/M/U): ");
                    String bp = scanner.nextLine().toUpperCase();

                    ts.bookTicket(name, age, gender, bp);
                    break;

                case 2:
                    System.out.print("Enter Ticket ID: ");
                    ts.cancelTicket(scanner.nextLine());
                    break;

                case 3: ts.printBookedTickets(); break;
                case 4: ts.printAvailableTickets(); break;
                case 5: ts.viewRacTickets(); break;
                case 6: ts.viewWaitingListTickets(); break;

                case 7:
                    System.out.println("Thank you!");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}