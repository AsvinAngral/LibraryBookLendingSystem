# Library Book Lending System

A console-based Java mini project for practicing OOP, collections, generics, exceptions, and Java Streams. It uses no external libraries.

## Requirements

- JDK 21 (the source code uses standard Java APIs and Java 8-era language/library features)
- Eclipse IDE or another Java IDE

## Run in Eclipse

1. Create a Java project named `LibraryBookLendingSystem`.
2. Copy the `src/com` folder into the project's `src` folder.
3. Ensure the project build path uses JDK 21.
4. Run `com.library.Main` as a Java Application.

## Main features

- Abstract `Member` class with Student, Faculty, and Guest member types.
- `Searchable` interface implemented by `Book`.
- Catalogue stored as `Map<String, Book>`, loan history stored as `List<Loan>`, and genres borrowed by each member stored as `Set<String>`.
- Generic `Pair<K, V>` for the member with the highest fine.
- Stream-based reports for overdue loans, books per genre, most-borrowed book, and keyword search.
- Custom exceptions for unavailable books, borrowing-limit violations, and missing members.

## Why each custom exception has its chosen type

- `BookNotAvailableException` is **checked** because borrowing code is expected to handle a normal library condition: all copies may already be on loan. The compiler makes callers handle or declare this condition.
- `BorrowLimitExceededException` is **unchecked** because it represents an operation that violates the member's borrowing rules. The library checks the limit and throws it when the caller attempts an invalid operation.
- `MemberNotFoundException` is **unchecked** because it indicates the requested member ID is invalid for the current library data, usually due to an incorrect ID supplied by the caller.

## Notes

- A new book starts with `availableCopies` equal to `totalCopies`.
- ISBN is used as the catalogue key and as the logical identity of a book.
- The current implementation uses the system date to calculate overdue days.
- `getMemberWithHighestFine()` throws `NoSuchElementException` if no members have been registered; it does not return `null`.
