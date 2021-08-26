package com.epam.rd.at.java_jdbc.tasks;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookStoreTasks {

    PreparedStatement preparedStatement1;
    PreparedStatement preparedStatement2;
    CallableStatement callableStatement;
    Connection connection = ConnectionManager.openConnection();
    ResultSet resultSet;

    /* task 1
       Вернуть список всех авторов, представленных в книжном магазине,
       в формате инициалов имен и фамилии, например J. R. R. Tolkien.
       Сортировать по фамилии в алфавитном порядке. */
    public List<String> getAllAuthors() {

        List<String> namesList = new ArrayList<>();

        try {
            preparedStatement1 = connection
                    .prepareStatement("SELECT \n" +
                            "CONCAT(LEFT(first_name, 1), '. '),\n" +
                            "CASE\n" +
                            "WHEN INSTR(TRIM(middle_name), ' ') = 0\n" +
                            "THEN CONCAT(LEFT(middle_name, 1), '. ')\n" +
                            "ELSE CONCAT(LEFT(middle_name, 1), '.',\n" +
                            "SUBSTRING(middle_name, LOCATE(' ', middle_name), 2), '. ')\n" +
                            "END,\n" +
                            "last_name\n" +
                            "FROM author\n" +
                            "ORDER BY last_name");
            resultSet = preparedStatement1.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getString(1) == null) {
                    namesList.add(resultSet.getString(2) + resultSet.getString(3));
                } else if (resultSet.getString(2) == null) {
                    namesList.add(resultSet.getString(1) + resultSet.getString(3));
                } else if (resultSet.getString(3) == null) {
                    namesList.add(resultSet.getString(1) + resultSet.getString(2));
                } else {
                    namesList.add(resultSet.getString(1) + resultSet.getString(2) + resultSet.getString(3));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return namesList;
    }

    /* task 2
       Найти самую популярную книгу автора по его фамилии.
       Предусмотреть исключение, если такой автор не найден. В сообщении исключения должна быть фамилия автора.
       Если книг этого автора нет или они не продавались, вернуть null.
       Выполнить этот метод для Asimov, Adams и Le Guin.*/
    public String getMostPopularAuthorBook(String lastName) {

        String author = null;
        String mostPopularBook = null;
        int sold = 0;

        try {
            preparedStatement1 = connection
                    .prepareStatement("SELECT COUNT(book_sale.sale_id) AS sold, book.book_name, author.last_name FROM author\n" +
                            "LEFT JOIN book ON author.author_id = book.author_id\n" +
                            "LEFT JOIN book_sale ON book.book_id = book_sale.book_id\n" +
                            "WHERE author.last_name = ?\n" +
                            "GROUP BY book.book_name, author.last_name\n" +
                            "ORDER BY sold DESC\n" +
                            "LIMIT 1");
            preparedStatement1.setString(1, lastName);
            resultSet = preparedStatement1.executeQuery();

            while (resultSet.next()) {
                sold = resultSet.getInt("sold");
                author = resultSet.getString("last_name");
                mostPopularBook = resultSet.getString("book_name");
            }
            if (author == null) {
                System.out.println("The following author does not exist in the database: " + lastName);
                throw new NullPointerException(lastName);
            } else if (sold == 0) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mostPopularBook;
    }

    /* task 3
       Подсчитать выручку магазина за конкретную дату (класс даты-параметра - LocalDate).
       Подсчитать выручку магазина за 2020-12-02.*/
    public int getIncomePerDay(LocalDate date) {

        int earnings = 0;

        try {
            preparedStatement1 = connection
                    .prepareStatement("SELECT * FROM book\n" +
                            "JOIN book_sale ON book.book_id = book_sale.book_id\n" +
                            "WHERE CAST(book_sale.date_time AS Date) = ?");
            preparedStatement1.setDate(1, Date.valueOf(date));
            resultSet = preparedStatement1.executeQuery();

            while (resultSet.next()) {
                earnings += resultSet.getInt("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return earnings;
    }

    /* task 4
       Увеличить стоимость книг автора на 10%, вернуть количество таких книг.
       Имя автора должно быть параметром этого метода.*/
    public int increasePriceByAuthor(String lastName) {

        int amountOfBooks = 0;
        String author = null;

        try {
            preparedStatement1 = connection
                    .prepareStatement("SELECT * FROM book\n" +
                            "JOIN author ON author.author_id = book.book_id\n" +
                            "WHERE author.last_name = ?");

            preparedStatement1.setString(1, lastName);
            resultSet = preparedStatement1.executeQuery();

            preparedStatement2 = connection
                    .prepareStatement("UPDATE book\n" +
                            "JOIN author on author.author_id = book.author_id\n" +
                            "SET book.price = book.price*1.1\n" +
                            "WHERE author.last_name = ?");
            preparedStatement2.setString(1, lastName);
            preparedStatement2.executeUpdate();

            while (resultSet.next()) {
                author = resultSet.getString("last_name");
                amountOfBooks = preparedStatement2.getUpdateCount();
            }
            if (author == null) {
                System.out.println("The following author does not exist in the database: " + lastName);
                throw new NullPointerException(lastName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return amountOfBooks;
    }

    /* task 5
       Добавить запись о новой проданной книге по ее имени на конкретную дату и время (класс параметра - LocalDateTime).
       Предусмотреть исключение при вводе несуществующего имени.*/
    public void addNewBookSaleAtCustomDateTime(String bookName, LocalDateTime localDateTime) {

        String newBookName = null;

        try {
            preparedStatement1 = connection
                    .prepareStatement("SELECT * FROM book_sale\n" +
                            "JOIN book ON book.book_id = book_sale.book_id\n" +
                            "WHERE book.book_name = ?\n" +
                            "ORDER BY book_sale.date_time DESC LIMIT 1");
            preparedStatement1.setString(1, bookName);
            resultSet = preparedStatement1.executeQuery();

            preparedStatement2 = connection
                    .prepareStatement("INSERT INTO book_sale (book_sale.book_id, book_sale.date_time)\n" +
                            "SELECT book.book_id, ? FROM book\n" +
                            "WHERE book.book_name = ?");
            preparedStatement2.setString(1, localDateTime.toString());
            preparedStatement2.setString(2, bookName);
            preparedStatement2.executeUpdate();

            while (resultSet.next()) {
                newBookName = resultSet.getString("book_name");
            }
            if (newBookName == null) {
                System.out.println("The following book does not exist in the database: " + bookName);
                throw new RuntimeException(bookName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* task 6 Добавить запись о новой проданной книге, используя встроенную процедуру add_book_sale(id, price)
       (дата-время будут сгенерированы автоматически). id - id книги, входной параметр, price - выходной параметр.
       Вернуть стоимость книги.*/
    public int addNewBookSaleAtCurrentDateTime(int bookId) {

        int result = 0;

        try {
            callableStatement = connection.prepareCall("{CALL add_book_sale(?, ?)}");
            callableStatement.setInt(1, bookId);
            callableStatement.setInt(2, Types.INTEGER);
            callableStatement.execute();
            result = callableStatement.getInt("price");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /* task 7
       Найти дату последней продажи книги по ее имени.
       Если книга не продавалась, вернуть null.*/
    public LocalDateTime getLastBookSale(String bookName) {

        int bookId = 0;
        String date = null;
        LocalDateTime localDateTime = null;

        try {
            preparedStatement1 = connection
                    .prepareStatement("SELECT book_id, book_name FROM book WHERE book_name = ?");
            preparedStatement1.setString(1, bookName);
            resultSet = preparedStatement1.executeQuery();
            while (resultSet.next()) {
                bookId = resultSet.getInt("book_id");
            }
            if (bookId == 0) {
                System.out.println("The following book does not exist in the database: " + bookName);
                throw new RuntimeException(bookName);
            }
            preparedStatement2 = connection
                    .prepareStatement("SELECT book.book_id, book.book_name, book_sale.date_time FROM book_sale\n" +
                            "JOIN book ON book_sale.book_id = book.book_id\n" +
                            "WHERE book.book_name = ?\n" +
                            "ORDER BY book_sale.date_time DESC\n" +
                            "LIMIT 1");
            preparedStatement2.setString(1, bookName);
            resultSet = preparedStatement2.executeQuery();

            while (resultSet.next()) {
                date = resultSet.getString("date_time");
                localDateTime = LocalDateTime.parse(date, DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if (date == null) {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return localDateTime;
    }

    public void close() throws SQLException {

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
                ConsoleMessages.messageClose();
            }
        }
    }
}