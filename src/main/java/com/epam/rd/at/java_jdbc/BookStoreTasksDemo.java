package com.epam.rd.at.java_jdbc;

import com.epam.rd.at.java_jdbc.tasks.BookStoreTasks;
import com.epam.rd.at.java_jdbc.tasks.ConsoleMessages;

import java.time.LocalDate;

public class BookStoreTasksDemo {

    public static void main(String[] args) {

        ConsoleMessages.messageWelcome();
        ConsoleMessages.messageInfo();
        BookStoreTasks bookStoreTasks = new BookStoreTasks();

        System.out.println("Task 1. The names' list of all authors is: " + bookStoreTasks.getAllAuthors());

        String authorLastNameCorrect = "Asimov";
        System.out.println("Task 2. The most popular book by " + authorLastNameCorrect + " is " +
                bookStoreTasks.getMostPopularAuthorBook(authorLastNameCorrect));

        LocalDate date = LocalDate.of(2020, 12, 2);
        System.out.println("Task 3. The income for " + date + " is: " + bookStoreTasks.getIncomePerDay(date));

        String greedyAuthor = "Tolkien";
        System.out.println("Task 4. The amount of " + greedyAuthor + "'s books which prices have been risen is: " +
                bookStoreTasks.increasePriceByAuthor(greedyAuthor));

        int bookID = 1;
        System.out.println("Task 6. The price of the last book sold is: " +
                bookStoreTasks.addNewBookSaleAtCurrentDateTime(bookID));

        String soldBookName = "I, Robot";
        System.out.println("Task 7. The book " + soldBookName + " has been sold on " +
                bookStoreTasks.getLastBookSale(soldBookName));
    }
}
