public class Runner {
    public static void main(String[] args) {
        Hotel h = new Hotel(new Manager("Saad", 2, 300, "saqi", "123"),
        new Receptionist("saad", 123, 400, "saad01",   "123"));
        h.run();
    }
}
