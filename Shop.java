import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

enum Brand { ASUS, ACER, DELL, HP, LENOVO, MSI, OTHER }
record Processor(String model, int cores) {
    Processor {
        if (model.isBlank() || cores < 1) throw new IllegalArgumentException("Неверные данные процессора");
    }
}
record Memory(int gigabytes) {
    Memory {
        if (gigabytes < 1) throw new IllegalArgumentException("Неверный объём памяти");
    }
}
record Monitor(double inches) {
    Monitor {
        if (!Double.isFinite(inches) || inches <= 0) throw new IllegalArgumentException("Неверная диагональ");
    }
}
record Computer(String id, Brand brand, Processor processor, Memory memory, Monitor monitor) {}

interface ComputerInput {
    Computer read(String id);
}

final class ConsoleComputerInput implements ComputerInput {
    private final Scanner input;

    ConsoleComputerInput(Scanner input) { this.input = input; }

    @Override public Computer read(String id) {
        System.out.println("Марка:");
        Brand brand = Brand.valueOf(input.nextLine().strip().toUpperCase(Locale.ROOT));
        System.out.println("Модель процессора:");
        String model = input.nextLine().strip();
        System.out.println("Число ядер, ОЗУ в ГБ и диагональ через пробел:");
        String[] specs = input.nextLine().strip().split("\\s+");
        if (specs.length != 3) throw new IllegalArgumentException("Нужны три числа");
        return new Computer(id, brand, new Processor(model, Integer.parseInt(specs[0])),
                new Memory(Integer.parseInt(specs[1])), new Monitor(Double.parseDouble(specs[2].replace(',', '.'))));
    }
}

final class Shop implements Printable {
    private final List<Computer> computers = new ArrayList<>();

    public boolean add(Computer computer) {
        for (Computer existing : computers) {
            if (existing.id().equals(computer.id())) return false;
        }
        return computers.add(computer);
    }

    public boolean remove(String id) {
        return computers.removeIf(computer -> computer.id().equals(id));
    }

    public List<Computer> find(Brand brand, int minimumRam) {
        List<Computer> matches = new ArrayList<>();
        for (Computer computer : computers) {
            if (computer.brand() == brand && computer.memory().gigabytes() >= minimumRam) {
                matches.add(computer);
            }
        }
        return matches;
    }

    @Override public void print() {
        System.out.println("Компьютеров в каталоге: " + computers.size());
        for (Computer computer : computers) System.out.println(computer);
    }
}
