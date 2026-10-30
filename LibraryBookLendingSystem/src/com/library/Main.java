package com.library;

import com.library.exception.BookNotAvailableException;
import com.library.exception.BorrowLimitExceededException;
import com.library.model.Book;
import com.library.model.FacultyMember;
import com.library.model.GuestMember;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.model.StudentMember;
import com.library.service.Library;
import com.library.util.Pair;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Library library = new Library();

        library.addBook(new Book("ISBN101", "Java Programming", "James Gosling", "Programming", 2));
        library.addBook(new Book("ISBN102", "Effective Java", "Joshua Bloch", "Programming", 2));
        library.addBook(new Book("ISBN103", "Clean Code", "Robert Martin", "Software", 2));
        library.addBook(new Book("ISBN104", "Learning Python", "Mark Lutz", "Programming", 1));
        library.addBook(new Book("ISBN105", "Empty Shelf Example", "Library Staff", "Reference", 0));

        library.addMember(new StudentMember("S01", "Aman"));
        library.addMember(new GuestMember("G01", "Riya"));
        library.addMember(new FacultyMember("F01", "Dr. Sharma"));

        System.out.println("TEST 1: Student borrows three books and tries a fourth");
        try {
            library.borrowBook("S01", "ISBN101");
            library.borrowBook("S01", "ISBN102");
            library.borrowBook("S01", "ISBN103");
            library.borrowBook("S01", "ISBN104");
        } catch (BorrowLimitExceededException ex) {
            System.out.println("Expected exception caught: " + ex.getMessage());
        } catch (BookNotAvailableException ex) {
            System.out.println("Unexpected availability issue: " + ex.getMessage());
        }

        System.out.println("\nTEST 2: Guest returns a book two days late");
        try {
            library.borrowBook("G01", "ISBN104", LocalDate.now().minusDays(2));
            double guestFine = library.returnBook("G01", "ISBN104");
            System.out.println("Guest late fee: ₹" + guestFine + " (expected ₹50)");
        } catch (BookNotAvailableException ex) {
            System.out.println("Could not borrow guest test book: " + ex.getMessage());
        }

        System.out.println("\nTEST 3: Faculty returns a book ten days late");
        try {
            library.borrowBook("F01", "ISBN101", LocalDate.now().minusDays(10));
            double facultyFine = library.returnBook("F01", "ISBN101");
            System.out.println("Faculty late fee: ₹" + facultyFine + " (expected ₹20)");
        } catch (BookNotAvailableException ex) {
            System.out.println("Could not borrow faculty test book: " + ex.getMessage());
        }

        System.out.println("\nTEST 4: Borrow a book with zero copies");
        try {
            library.borrowBook("F01", "ISBN105");
        } catch (BookNotAvailableException ex) {
            System.out.println("Expected exception caught: " + ex.getMessage());
        }

        System.out.println("\nTEST 5: Search for 'java'");
        library.searchBooks("java").forEach(System.out::println);

        // Leave one overdue loan active so the overdue report has a result.
        try {
            library.borrowBook("F01", "ISBN103", LocalDate.now().minusDays(5));
        } catch (BookNotAvailableException ex) {
            System.out.println("Could not set up overdue report example: " + ex.getMessage());
        }

        System.out.println("\nREPORT: Books per genre");
        System.out.println(library.getBooksPerGenre());

        System.out.println("\nREPORT: Most borrowed book");
        library.getMostBorrowedBook().ifPresentOrElse(
                book -> System.out.println(book.getTitle()),
                () -> System.out.println("No books have been borrowed yet"));

        System.out.println("\nREPORT: Overdue active loans");
        library.getOverdueLoans().forEach(System.out::println);

        System.out.println("\nREPORT: Member with highest fine");
        try {
            Pair<Member, Double> highest = library.getMemberWithHighestFine();
            System.out.println(highest.getKey().getName() + " — ₹" + highest.getValue());
        } catch (java.util.NoSuchElementException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("\nGenres borrowed by Aman: " + library.getBorrowedGenres("S01"));
    }
}
