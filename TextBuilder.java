import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

abstract class TextBuilder implements CharSequence, Comparable<TextBuilder> {
    private final StringBuilder buffer = new StringBuilder();

    protected abstract void onEdit(String previous, String current);

    protected final void replaceSilently(String text) {
        buffer.replace(0, buffer.length(), text);
    }

    private TextBuilder change(Runnable action) {
        String previous = buffer.toString();
        action.run();
        String current = buffer.toString();
        if (!previous.equals(current)) onEdit(previous, current);
        return this;
    }

    @Override public int compareTo(TextBuilder other) { return buffer.compareTo(other.buffer); }

    public TextBuilder append(Object value) { return change(() -> buffer.append(value)); }
    public TextBuilder append(String value) { return change(() -> buffer.append(value)); }
    public TextBuilder append(StringBuffer value) { return change(() -> buffer.append(value)); }
    public TextBuilder append(CharSequence value) { return change(() -> buffer.append(value)); }
    public TextBuilder append(CharSequence value, int start, int end) { return change(() -> buffer.append(value, start, end)); }
    public TextBuilder append(char[] value) { return change(() -> buffer.append(value)); }
    public TextBuilder append(char[] value, int offset, int len) { return change(() -> buffer.append(value, offset, len)); }
    public TextBuilder append(boolean value) { return change(() -> buffer.append(value)); }
    public TextBuilder append(char value) { return change(() -> buffer.append(value)); }
    public TextBuilder append(int value) { return change(() -> buffer.append(value)); }
    public TextBuilder append(long value) { return change(() -> buffer.append(value)); }
    public TextBuilder append(float value) { return change(() -> buffer.append(value)); }
    public TextBuilder append(double value) { return change(() -> buffer.append(value)); }
    public TextBuilder appendCodePoint(int codePoint) { return change(() -> buffer.appendCodePoint(codePoint)); }
    public TextBuilder insert(int offset, Object value) { return change(() -> buffer.insert(offset, value)); }
    public TextBuilder insert(int offset, String value) { return change(() -> buffer.insert(offset, value)); }
    public TextBuilder insert(int offset, CharSequence value) { return change(() -> buffer.insert(offset, value)); }
    public TextBuilder insert(int offset, CharSequence value, int start, int end) { return change(() -> buffer.insert(offset, value, start, end)); }
    public TextBuilder insert(int offset, char[] value) { return change(() -> buffer.insert(offset, value)); }
    public TextBuilder insert(int offset, char[] value, int start, int len) { return change(() -> buffer.insert(offset, value, start, len)); }
    public TextBuilder insert(int offset, boolean value) { return change(() -> buffer.insert(offset, value)); }
    public TextBuilder insert(int offset, char value) { return change(() -> buffer.insert(offset, value)); }
    public TextBuilder insert(int offset, int value) { return change(() -> buffer.insert(offset, value)); }
    public TextBuilder insert(int offset, long value) { return change(() -> buffer.insert(offset, value)); }
    public TextBuilder insert(int offset, float value) { return change(() -> buffer.insert(offset, value)); }
    public TextBuilder insert(int offset, double value) { return change(() -> buffer.insert(offset, value)); }
    public TextBuilder delete(int start, int end) { return change(() -> buffer.delete(start, end)); }
    public TextBuilder deleteCharAt(int index) { return change(() -> buffer.deleteCharAt(index)); }
    public TextBuilder replace(int start, int end, String value) { return change(() -> buffer.replace(start, end, value)); }
    public TextBuilder reverse() { return change(() -> buffer.reverse()); }
    public TextBuilder repeat(int codePoint, int count) { return change(() -> buffer.repeat(codePoint, count)); }
    public TextBuilder repeat(CharSequence value, int count) { return change(() -> buffer.repeat(value, count)); }
    public int length() { return buffer.length(); }
    public int capacity() { return buffer.capacity(); }
    public int codePointAt(int index) { return buffer.codePointAt(index); }
    public int codePointBefore(int index) { return buffer.codePointBefore(index); }
    public int codePointCount(int start, int end) { return buffer.codePointCount(start, end); }
    public int offsetByCodePoints(int index, int offset) { return buffer.offsetByCodePoints(index, offset); }
    public int indexOf(String value) { return buffer.indexOf(value); }
    public int indexOf(String value, int from) { return buffer.indexOf(value, from); }
    public int lastIndexOf(String value) { return buffer.lastIndexOf(value); }
    public int lastIndexOf(String value, int from) { return buffer.lastIndexOf(value, from); }
    public char charAt(int index) { return buffer.charAt(index); }
    public String substring(int start) { return buffer.substring(start); }
    public String substring(int start, int end) { return buffer.substring(start, end); }
    public String toString() { return buffer.toString(); }
    public CharSequence subSequence(int start, int end) { return buffer.subSequence(start, end); }
    public java.util.stream.IntStream chars() { return buffer.chars(); }
    public java.util.stream.IntStream codePoints() { return buffer.codePoints(); }

    public void setLength(int length) { change(() -> buffer.setLength(length)); }
    public void setCharAt(int index, char value) { change(() -> buffer.setCharAt(index, value)); }
    public void ensureCapacity(int capacity) { buffer.ensureCapacity(capacity); }
    public void trimToSize() { buffer.trimToSize(); }
    public void getChars(int start, int end, char[] target, int offset) {
        buffer.getChars(start, end, target, offset);
    }
}

final class UndoStringBuilder extends TextBuilder {
    private record Restore(String value) {
        void execute(UndoStringBuilder owner) { owner.replaceSilently(value); }
    }

    private final List<Restore> history = new ArrayList<>();

    @Override protected void onEdit(String previous, String current) {
        history.add(new Restore(previous));
    }

    public boolean undo() {
        if (history.isEmpty()) return false;
        history.removeLast().execute(this);
        return true;
    }
}

final class ObservableStringBuilder extends TextBuilder {
    private final List<Consumer<String>> subscribers = new ArrayList<>();

    public void subscribe(Consumer<String> subscriber) {
        subscribers.add(java.util.Objects.requireNonNull(subscriber));
    }

    public void unsubscribe(Consumer<String> subscriber) { subscribers.remove(subscriber); }

    @Override protected void onEdit(String previous, String current) {
        for (Consumer<String> subscriber : List.copyOf(subscribers)) {
            subscriber.accept(current);
        }
    }
}
