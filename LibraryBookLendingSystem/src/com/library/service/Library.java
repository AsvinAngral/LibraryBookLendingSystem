package com.library.service;

import com.library.exception.BookNotAvailableException;
import com.library.exception.BorrowLimitExceededException;
import com.library.exception.MemberNotFoundException;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.util.Pair;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Library {
    private final Map<String, Book> catalogue;
    private final Map<String, Member> members;
    private final List<Loan> loanHistory;
    private final Map<String, Set<String>> borrowedGenres;

    public Library() {
        catalogue = new HashMap<>();
        members = new HashMap<>();
        loanHistory = new ArrayList<>();
        borrowedGenres = new HashMap<>();
    }

    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (catalogue.containsKey(book.getIsbn())) {
            throw new IllegalArgumentException("A book with this ISBN already exists");
        }
        catalogue.put(book.getIsbn(), book);
    }

    public void addMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        if (members.containsKey(member.getId())) {
            throw new IllegalArgumentException("A member with this ID already exists");
        }
        members.put(member.getId(), member);
        borrowedGenres.put(member.getId(), new HashSet<>());
    }

    public void borrowBook(String memberId, String isbn)
            throws BookNotAvailableException {
        borrowBook(memberId, isbn, LocalDate.now().plusDays(14));
    }

    public void borrowBook(String memberId, String isbn, LocalDate dueDate)
            throws BookNotAvailableException {
        Member member = findMember(memberId);
        Book book = catalogue.get(isbn);

        if (book == null) {
            throw new IllegalArgumentException("Book ISBN not found: " + isbn);
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        }

        long activeLoans = loanHistory.stream()
                .filter(Loan::isActive)
                .filter(loan -> loan.getMember().equals(member))
                .count();

        if (activeLoans >= member.getMaxBooksAllowed()) {
            throw new BorrowLimitExceededException(
                    member.getName() + " has reached the borrowing limit");
        }
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(
                    "No copies available for: " + book.getTitle());
        }

        book.borrowCopy();

        // A past due date represents a loan that was already overdue when added
        // (useful for imported records and the late-fee test scenarios).
        LocalDate borrowedOn = dueDate.isBefore(LocalDate.now())
                ? dueDate.minusDays(14)
                : LocalDate.now();

        loanHistory.add(new Loan(member, book, borrowedOn, dueDate));
        borrowedGenres.get(member.getId()).add(book.getGenre());
    }

    public double returnBook(String memberId, String isbn) {
        Member member = findMember(memberId);

        Loan loan = loanHistory.stream()
                .filter(Loan::isActive)
                .filter(item -> item.getMember().equals(member))
                .filter(item -> item.getBook().getIsbn().equals(isbn))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "No active loan found for member " + memberId + " and ISBN " + isbn));

        loan.markReturned(LocalDate.now());
        loan.getBook().returnCopy();
        return loan.getFine();
    }

    private Member findMember(String memberId) {
        Member member = members.get(memberId);
        if (member == null) {
            throw new MemberNotFoundException("Member not found: " + memberId);
        }
        return member;
    }

    public List<Loan> getOverdueLoans() {
        return loanHistory.stream()
                .filter(Loan::isActive)
                .filter(loan -> loan.getDaysLate() > 0)
                .sorted(Comparator.comparingInt(Loan::getDaysLate).reversed())
                .collect(Collectors.toList());
    }

    public Map<String, Long> getBooksPerGenre() {
        return catalogue.values().stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
    }

    public Optional<Book> getMostBorrowedBook() {
        return loanHistory.stream()
                .collect(Collectors.groupingBy(Loan::getBook, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public List<Book> searchBooks(String keyword) {
        return catalogue.values().stream()
                .filter(book -> book.matches(keyword))
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.toList());
    }

    public Pair<Member, Double> getMemberWithHighestFine() {
        return members.values().stream()
                .map(member -> new Pair<>(
                        member,
                        loanHistory.stream()
                                .filter(loan -> loan.getMember().equals(member))
                                .mapToDouble(Loan::getFine)
                                .sum()))
                .max(Comparator.comparing(Pair::getValue))
                .orElseThrow(() -> new NoSuchElementException("No members have been registered"));
    }

    public Set<String> getBorrowedGenres(String memberId) {
        findMember(memberId);
        return new HashSet<>(borrowedGenres.get(memberId));
    }

    public List<Loan> getLoanHistory() {
        return new ArrayList<>(loanHistory);
    }

    public List<Book> getCatalogue() {
        return catalogue.values().stream()
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.toList());
    }
}
