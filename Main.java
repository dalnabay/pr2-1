import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

interface Movable {
    void moveUp();
    void moveDown();
    void moveLeft();
    void moveRight();
}

final class MovablePoint implements Movable {
    private int x;
    private int y;
    private final int stepX;
    private final int stepY;

    MovablePoint(int x, int y, int stepX, int stepY) {
        if (stepX < 0 || stepY < 0) throw new IllegalArgumentException("Шаг должен быть неотрицательным");
        this.x = x;
        this.y = y;
        this.stepX = stepX;
        this.stepY = stepY;
    }

    boolean sameSpeed(MovablePoint point) {
        return stepX == point.stepX && stepY == point.stepY;
    }

    @Override public void moveUp() { y -= stepY; }
    @Override public void moveDown() { y += stepY; }
    @Override public void moveLeft() { x -= stepX; }
    @Override public void moveRight() { x += stepX; }
    @Override public String toString() { return "(" + x + ", " + y + ")"; }
}

abstract class MovableFigure implements Movable {
    protected abstract List<MovablePoint> points();
    @Override public void moveUp() { points().forEach(MovablePoint::moveUp); }
    @Override public void moveDown() { points().forEach(MovablePoint::moveDown); }
    @Override public void moveLeft() { points().forEach(MovablePoint::moveLeft); }
    @Override public void moveRight() { points().forEach(MovablePoint::moveRight); }
}

final class MovableCircle extends MovableFigure {
    private final MovablePoint center;
    private final int radius;

    MovableCircle(int x, int y, int stepX, int stepY, int radius) {
        if (radius <= 0) throw new IllegalArgumentException("Радиус должен быть положительным");
        center = new MovablePoint(x, y, stepX, stepY);
        this.radius = radius;
    }

    @Override protected List<MovablePoint> points() { return List.of(center); }
    @Override public String toString() { return "Круг: центр " + center + ", радиус " + radius; }
}

final class MovableRectangle extends MovableFigure {
    private final MovablePoint topLeft;
    private final MovablePoint bottomRight;

    MovableRectangle(int x1, int y1, int x2, int y2, int stepX, int stepY) {
        this(x1, y1, x2, y2, stepX, stepY, stepX, stepY);
    }

    MovableRectangle(int x1, int y1, int x2, int y2, int sx1, int sy1, int sx2, int sy2) {
        if (x1 >= x2 || y1 >= y2) throw new IllegalArgumentException("Неверные углы");
        topLeft = new MovablePoint(x1, y1, sx1, sy1);
        bottomRight = new MovablePoint(x2, y2, sx2, sy2);
        if (!topLeft.sameSpeed(bottomRight)) throw new IllegalArgumentException("Разные скорости углов");
    }

    @Override protected List<MovablePoint> points() { return List.of(topLeft, bottomRight); }
    @Override public String toString() { return "Прямоугольник: " + topLeft + " и " + bottomRight; }
}

interface Nameable { String getName(); }
interface Priceable { double getPrice(); }
interface Printable { void print(); }
interface Convertable { double convert(double celsius); }

record Animal(String name) implements Nameable {
    public String getName() { return name; }
}
record City(String name) implements Nameable {
    public String getName() { return name; }
}
record Ticket(double price) implements Priceable {
    public double getPrice() { return price; }
}
record Equipment(double price) implements Priceable {
    public double getPrice() { return price; }
}
record Book(String title) implements Printable {
    public void print() { System.out.println("Книга: " + title); }
}
record Magazine(String title) implements Printable {
    public void print() { System.out.println("Журнал: " + title); }
}

public final class Main {
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("shop")) {
            runShop();
            return;
        }
        var point = new MovablePoint(1, 2, 3, 2);
        var circle = new MovableCircle(5, 6, 3, 2, 7);
        var rectangle = new MovableRectangle(1, 1, 7, 5, 3, 2);
        for (Movable figure : List.of(point, circle, rectangle)) {
            String initial = figure.toString();
            figure.moveRight();
            figure.moveUp();
            System.out.println(figure);
            figure.moveLeft();
            figure.moveDown();
            require(initial.equals(figure.toString()));
        }
        requireThrows(() -> new MovableRectangle(1, 1, 7, 5, 3, 2, 4, 2));
        for (Nameable item : List.of(new City("Казань"), new Animal("Барс"))) {
            System.out.println("Имя: " + item.getName());
        }
        for (Priceable item : List.of(new Ticket(450), new Equipment(1900))) {
            System.out.println("Цена: " + item.getPrice());
        }
        Shop shop = new Shop();
        Computer pc = new Computer("K2", Brand.LENOVO, new Processor("Core i5", 8), new Memory(32), new Monitor(27));
        require(shop.add(pc) && !shop.add(pc) && shop.find(Brand.LENOVO, 32).equals(List.of(pc)));
        require(shop.find(Brand.LENOVO, 64).isEmpty());
        for (Printable item : new Printable[] {new Book("Ревизор"), new Magazine("Квант"), shop}) item.print();
        require(shop.remove("K2") && !shop.remove("K2"));

        Convertable kelvin = c -> c + 273.15;
        Convertable fahrenheit = c -> 32 + c * 1.8;
        require(kelvin.convert(25) == 298.15 && fahrenheit.convert(25) == 77);
        System.out.printf(Locale.ROOT, "25 °C = %.2f K и %.1f °F%n", kelvin.convert(25), fahrenheit.convert(25));

        var undo = new UndoStringBuilder();
        undo.append("код");
        undo.insert(0, "Java ");
        require(undo.undo() && undo.toString().equals("код"));
        require(undo.undo() && undo.length() == 0 && !undo.undo());
        var observable = new ObservableStringBuilder();
        List<String> events = new ArrayList<>();
        java.util.function.Consumer<String> subscriber = events::add;
        observable.subscribe(subscriber);
        observable.append("код");
        observable.reverse();
        observable.append("");
        observable.unsubscribe(subscriber);
        observable.append("!");
        require(events.equals(List.of("код", "док")));
        System.out.println("Уведомления: " + events);
        System.out.println("Все проверки пройдены");
    }

    private static void runShop() {
        try (Scanner input = new Scanner(System.in)) {
            Shop shop = new Shop();
            ComputerInput reader = new ConsoleComputerInput(input);
            System.out.println("add ID, remove ID, find МАРКА МИНИМУМ_ОЗУ, list, exit");
            while (input.hasNextLine()) {
                String[] words = input.nextLine().strip().split("\\s+");
                try {
                    switch (words[0]) {
                        case "add" -> {
                            requireCount(words, 2);
                            System.out.println(shop.add(reader.read(words[1])) ? "Добавлено" : "Номер занят");
                        }
                        case "remove" -> {
                            requireCount(words, 2);
                            System.out.println(shop.remove(words[1]) ? "Удалено" : "Номер не найден");
                        }
                        case "find" -> {
                            requireCount(words, 3);
                            System.out.println(shop.find(Brand.valueOf(words[1].toUpperCase(Locale.ROOT)),
                                    Integer.parseInt(words[2])));
                        }
                        case "list" -> shop.print();
                        case "exit" -> { return; }
                        default -> System.out.println("Неизвестная команда");
                    }
                } catch (IllegalArgumentException error) {
                    System.out.println("Ошибка: " + error.getMessage());
                } catch (java.util.NoSuchElementException error) {
                    System.out.println("Ввод закончен");
                    return;
                }
            }
        }
    }

    private static void requireCount(String[] words, int count) {
        if (words.length != count) throw new IllegalArgumentException("Проверьте число аргументов");
    }

    private static void requireThrows(Runnable action) {
        try {
            action.run();
            throw new AssertionError("Ожидалось исключение");
        } catch (IllegalArgumentException expected) {
            System.out.println(expected.getMessage());
        }
    }

    private static void require(boolean condition) {
        if (!condition) throw new AssertionError("Проверка не пройдена");
    }
}
