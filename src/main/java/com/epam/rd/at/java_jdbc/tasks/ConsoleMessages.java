package com.epam.rd.at.java_jdbc.tasks;

public class ConsoleMessages {

    public static void messageOpen() {
        System.out.println("Database connection established...");
    }

    public static void messageNoConnection() {
        System.out.println("No connection...");
    }

    public static void messageClose() {
        System.out.println("Database connection is closed.");
    }

    public static void messageWelcome() {
        System.out.println("Start the app...");
    }

    public static void messageInfo() {
        System.out.println("This is a demo version of the application,\n" +
                "so requests are processed with the parameters already specified.\n" +
                "If you want to try a different parameter, go to the code and change the required values.\n");
    }
}
