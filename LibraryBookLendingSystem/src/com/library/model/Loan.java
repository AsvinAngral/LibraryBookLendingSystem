package com.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class Loan {
    private final Member member;
    private final Book book;
    private final LocalDate borrowedOn;
    private final LocalDate dueDate;
    private LocalDate returnedOn;
    private int daysLateAtReturn;
    private double fine;

    public Loan(Member member, Book book, LocalDate borrowedOn, LocalDate dueDate) {
        if (member == null || book == null || borrowedOn == null || dueDate == null) {
            throw new IllegalArgumentException("Loan details cannot be null");
        }
        if (dueDate.isBefore(borrowedOn)) {
            throw new IllegalArgumentException("Due date cannot be before the borrow date");
        }

        this.member = member;
        this.book = book;
        this.borrowedOn = borrowedOn;
        this.dueDate = dueDate;
        this.returnedOn = null;
        this.daysLateAtReturn = 0;
        this.fine = 0.0;
    }

    public boolean isActive() {
        return returnedOn == null;
    }

    public int getDaysLate() {
        if (isActive()) {
            return (int) Math.max(0, ChronoUnit.DAYS.between(dueDate, LocalDate.now()));
        }
        return daysLateAtReturn;
    }

    public void markReturned(LocalDate returnDate) {
        if (returnDate == null) {
            throw new IllegalArgumentException("Return date cannot be null");
        }
        if (!isActive()) {
            throw new IllegalStateException("This loan has already been returned");
        }

        daysLateAtReturn = (int) Math.max(0, ChronoUnit.DAYS.between(dueDate, returnDate));
        fine = member.calculateLateFee(daysLateAtReturn);
        returnedOn = returnDate;
    }

    public Member getMember() {
        return member;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getBorrowedOn() {
        return borrowedOn;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Optional<LocalDate> getReturnedOn() {
        return Optional.ofNullable(returnedOn);
    }

    public double getFine() {
        return fine;
    }

    @Override
    public String toString() {
        return "Loan{member='" + member.getName() + "', book='" + book.getTitle()
                + "', dueDate=" + dueDate + ", daysLate=" + getDaysLate()
                + ", returned=" + !isActive() + ", fine=" + fine + "}";
    }
}
