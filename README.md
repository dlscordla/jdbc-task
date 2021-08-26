# Java JDBC

### Установка

СУБД - MySql. Необходимо самостоятельно установить MySql на свой компьютер.

В качестве клиента к ней можно использовать [Heidi SQL](https://www.heidisql.com)

Скрипт для создания базы данных находится в папке `src/main/resources/book_store.sql`. Чтобы
импортировать эту базу, необходимо открыть этот скрипт в клиенте и запустить.

Драйвер JDBC уже подключен через Maven (исправьте версию если необходимо).

### Работа с базой данных

Требуется разработать приложение, через которое можно установить JDBC соединение с базой данных
MySql, и выполнить с предложенной базой ряд заданий.

Необходимо продумать архитектуру приложения. Минимальный набор классов (кроме них, нужно добавить
свои целесообразно архитектуре):

- `ConnectionManager` класс организует все операции с `Connection`. Необходимо выводить сообщение об
  установлении и закрытии соединения. Соединение с базой открывается одно на все задание.
- `BookStoreTasks` класс имеет методы для решения каждого задания с необходимыми параметрами,
  вызывает `ConnectionManager` для открытия соединения.
- `BookStoreTasksDemo` представляет собой демонстрацию всего задания и содержит вызовы
  методов `BookStoreTasks` с нужными параметрами, например `getAuthorBooks("Bradbury")`.

Необходимо организовать осмысленный отлов исключений SQLException.

В каждом задании необходимо использовать подходящий тип Statement.

Строка соединения, логин и пароль должны передаваться через properties файл.

##### Задания

1. Вернуть список всех авторов, представленных в книжном магазине, в формате инициалов имен и
   фамилии, например `J. R. R. Tolkien`. Сортировать по фамилии в алфавитном порядке.
1. Найти самую популярную книгу автора по его фамилии. Предусмотреть исключение, если такой автор не
   найден. В сообщении исключения должна быть фамилия автора. Если книг этого автора нет или они не
   продавались, вернуть `null`. Выполнить этот метод для `Asimov`, `Adams` и `Le Guin`.
1. Подсчитать выручку магазина за конкретную дату (класс даты-параметра - LocalDate). Подсчитать
   выручку магазина за 2020-12-02.
1. Увеличить стоимость книг автора на 10%, вернуть количество таких книг. Имя автора должно быть
   параметром этого метода.
1. Добавить запись о новой проданной книге по ее имени на конкретную дату и время (класс параметра -
   LocalDateTime). Предусмотреть исключение при вводе несуществующего имени.
1. Добавить запись о новой проданной книге, используя встроенную
   процедуру `add_book_sale(id, price)` (дата-время будут сгенерированы автоматически). id - id
   книги, входной параметр, price - выходной параметр. Вернуть стоимость книги.
1. Найти дату последней продажи книги по ее имени. Если книга не продавалась, вернуть `null`.

##### Использование шаблона

`BookStoreTasksTest` - тест для этого задания. Он использует MariaDB4j - базу данных (аналог MySQL),
которая поднимается в памяти Java-машины. Благодаря этому при каждом прогоне этого теста вы имеете
свежую базу данных, которой не нужно соединение с MySQL.

### Критерии оценки

- Пройдены все юнит-тесты упражнений - 1 балл
- Пройден статический анализ - 0.5 балла
- Код легко читаемый, методы и переменные названы понятными смысловыми именами - 0.5 балла
- Код логично разделен на классы сообразно заданной архитектуре - 1 балл
- Использованы все виды Statement - 0.5 балла
- Организована осмысленная обработка исключений - 0.5 балла
- Организован properties файл и его корректное чтение - 0.5 балла
- SQL-запросы отформатированы в удобном для чтения виде - 0.5 балла