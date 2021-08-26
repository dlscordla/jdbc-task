package com.epam.rd.at.java_jdbc.tasks;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookStoreTasksTest {

    private static final int MARIA_PORT = 3307;
    private DB database;
    private BookStoreTasks underTest;
    private Connection mockedConnection;

    @BeforeAll
    void test() throws SQLException, ManagedProcessException {
        DBConfiguration config = DBConfigurationBuilder.newBuilder()
                .addArg("--user=root")
                .setPort(MARIA_PORT)
                .build();

        database = DB.newEmbeddedDB(config);
        database.start();
        database.source("book_store.sql", "test");

        mockedConnection = DriverManager.getConnection("jdbc:mysql://localhost:" + MARIA_PORT +
                "/book_store?useUnicode=true&serverTimezone=Europe/Moscow", "root", "");

        MockedStatic<DriverManager> theMock = Mockito.mockStatic(DriverManager.class);
        theMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mockedConnection);

        underTest = spy(BookStoreTasks.class);
    }

    @Test
    void getAllAuthors() throws SQLException {
        List<String> result = underTest.getAllAuthors();
        List<String> expected = Arrays.asList("D. N. Adams", "I. Asimov", "R. D. Bradbury", "G. Orwell", "J. R. R. Tolkien");
        assertEquals(expected, result, "getAllAuthors has hailed");
    }

    @Test
    void getMostPopularAuthorBook() throws SQLException {
        String lastName = "Asimov";
        String result = underTest.getMostPopularAuthorBook(lastName);
        String expected = "I, Robot";
        assertEquals(expected, result, "getAuthorBooks for " + lastName);
    }

    @Test
    void getMostPopularAuthorBookWhenNoBooks() throws SQLException {
        String lastName = "Adams";
        String result = underTest.getMostPopularAuthorBook(lastName);
        assertNull(result, "getAuthorBooks for " + lastName);
    }

    @Test
    void getMostPopularAuthorBookWhenNoSuchAuthor() throws SQLException {
        String lastName = "Le Guin";
        Throwable exceptionThatWasThrown = assertThrows(RuntimeException.class, () -> underTest.getMostPopularAuthorBook(lastName));
        assertTrue(exceptionThatWasThrown.getMessage().contains(lastName), "getAuthorBooks for " + lastName);
    }

    @Test
    void getIncomePerDay() throws SQLException {
        LocalDate date = LocalDate.of(2020, Month.DECEMBER, 2);
        int result = underTest.getIncomePerDay(date);

        Collection<Invocation> invocations = Mockito.mockingDetails(underTest).getInvocations();
        long numberOfCalls = invocations.stream()
                .filter(i -> "increasePriceByAuthor".equals(i.getMethod().getName())).count();
        int expected = numberOfCalls == 0 ? 2310 : 2385;
        assertEquals(expected, result, "getIncomePerDay for " + date);
    }

    @Test
    void increasePriceByAuthor() throws SQLException {
        String lastName = "Tolkien";
        int result = underTest.increasePriceByAuthor(lastName);
        int expected = 5;
        assertEquals(expected, result, "increasePriceByAuthor for " + lastName);
    }

    @Test
    void increasePriceByAuthorWhenAuthorNotFound() throws SQLException {
        String lastName = "Le Guin";
        Throwable exceptionThatWasThrown = assertThrows(RuntimeException.class, () -> underTest.increasePriceByAuthor(lastName));
        assertTrue(exceptionThatWasThrown.getMessage().contains(lastName), "getAuthorBooks for " + lastName);
    }

    @Test
    void addNewBookSaleAtCustomDateTime() throws SQLException {
        LocalDateTime dateTime = LocalDateTime.now().withNano(0);
        String bookName = "Fahrenheit 451";
        underTest.addNewBookSaleAtCustomDateTime(bookName, dateTime);
        try (ResultSet resultSet = mockedConnection
                .createStatement().executeQuery("SELECT * FROM book_sale ORDER BY sale_id desc limit 1")) {
            assertTrue(resultSet.next());
            int expectedBookId = 5;
            assertEquals(expectedBookId, resultSet.getInt("book_id"));
            String dateTimeFormatted = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
            assertEquals(dateTimeFormatted, resultSet.getString("date_time"));
        }
    }

    @Test
    void addNewBookSaleAtCustomDateTimeWhenNoSuchBook() throws SQLException {
        LocalDateTime dateTime = LocalDateTime.now().withNano(0);
        String bookName = "Brave New World";
        Throwable exceptionThatWasThrown = assertThrows(RuntimeException.class, () -> underTest.addNewBookSaleAtCustomDateTime(bookName, dateTime));
        assertTrue(exceptionThatWasThrown.getMessage().contains(bookName), "addNewBookSaleAtCustomDateTime for " + bookName);
    }

    @Test
    void addNewBookSaleAtCurrentDateTime() throws SQLException {
        int expectedPrice = 200;
        int bookId = 1;
        int result = underTest.addNewBookSaleAtCurrentDateTime(bookId);
        assertEquals(expectedPrice, result, "addNewBookSaleAtCurrentDateTime for book id = " + bookId);

        try (Statement statement = mockedConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM book_sale ORDER BY sale_id desc limit 1");
            LocalDateTime dateTime = LocalDateTime.now();
            assertTrue(resultSet.next());
            assertEquals(bookId, resultSet.getInt("book_id"));

            String dateTimeString = resultSet.getString("date_time").replaceAll("\\.\\d+", "");
            LocalDateTime actualDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            assertTrue(ChronoUnit.SECONDS.between(actualDateTime, dateTime) < 1);
        }
    }

    @Test
    public void getLastBookSale() throws SQLException {
        LocalDateTime expected = LocalDateTime.of(2020, Month.DECEMBER, 7, 17, 37, 40);
        String bookName = "The End of Eternity";
        LocalDateTime result = underTest.getLastBookSale(bookName);
        assertEquals(expected, result, "getLastBookSale for " + bookName);
    }

    @Test
    public void getLastBookSaleIncorrectBook() throws SQLException {
        String bookName = "Brave New World";
        Throwable exceptionThatWasThrown = assertThrows(RuntimeException.class, () -> underTest.getLastBookSale(bookName));
        assertTrue(exceptionThatWasThrown.getMessage().contains(bookName), "getLastBookSale for " + bookName);
    }

    @Test
    public void getLastBookSaleNull() throws SQLException {
        String bookName = "Nineteen Eighty-Four";
        LocalDateTime result = underTest.getLastBookSale(bookName);
        assertNull(result, "getLastBookSale for " + bookName);
    }

    @AfterAll
    void stop() throws Exception {
        underTest.close();
        assertTrue(mockedConnection.isClosed(), "Connection should be closed");
        database.stop();
    }
}
