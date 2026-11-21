[README.md](https://github.com/user-attachments/files/35206562/README.md)
# pr1-1# Практическая работа № 2 по ООП

Три независимые программы. Для сборки нужен JDK 21. В каждой папке с частью выполни:

```sh
javac --release 21 -encoding UTF-8 -d out *.java
java -cp out Main
```

В частях 1 и 2 рядом с кодом есть редактируемые UML-диаграммы (.drawio).

## Часть 1

`java -cp out Main shop` открывает магазин. Пример ввода по одной строке:

```text
add K2
LENOVO
Core i5
8 32 27
find LENOVO 32
remove K2
list
exit
```

## Часть 3

Добавь режим после `Main`:

- `submission`: ввод даты и времени получения задания, формат `дд.мм.гггг чч:мм`.
- `compare`: ввод даты `дд.мм.гггг` и сравнение с системной датой по Москве.
- `student`: имя и дата рождения на отдельных строках.
- `calendar`: ввод пяти чисел, например `2024 2 29`, затем `14 20`.
- `benchmark`: сравнение ArrayList и LinkedList. В таблице указаны медианы серий.
