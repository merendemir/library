package com.application.library.constants;

public class MessageConstants {

    // User Operations
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String USER_ALREADY_EXISTS_WITH_EMAIL = "User with this email already exists.";
    public static final String NOT_AUTHORIZED_FOR_DELETE_LIBRARIAN = "You are not authorized to delete librarian.";

    // Book Operations
    public static final String BOOK_NOT_FOUND = "Book not found.";
    public static final String BOOK_COMMENT_NOT_FOUND = "Book comment not found.";
    public static final String NOT_AUTHORIZED_FOR_UPDATE_COMMENT = "You are not authorized to update this comment.";
    public static final String NOT_AUTHORIZED_FOR_DELETE_COMMENT = "You are not authorized to delete this comment.";
    public static final String SHELF_ALREADY_EXISTS_WITH_NAME = "Shelf with this name already exists.";
    public static final String SHELF_NOT_FOUND = "Shelf not found.";
    public static final String SHELF_FULL = "Shelf is full, Cannot add more books.";
    public static final String SHELF_WILL_FULL = "Shelf will be full after adding this book.";
    public static final String SHELF_CANNOT_BE_DELETED = "Shelf cannot be deleted as it contains books.";
    public static final String BOOK_ALREADY_EXISTS_WITH_ISBN = "Book with this ISBN already exists.";
    public static final String BOOK_WILL_BE_LESS_THAN_LEND_BOOK_COUNT = "Total count cannot be less than lend book count.";
    public static final String BOOK_NOT_AVAILABLE_FOR_LENDING = "Book is not available for lending.";
    public static final String USER_HAS_ALREADY_LENT_A_BOOK = "User has already lent a book.";
    public static final String MUST_PAY_LATE_FEE = "You have to pay late fee.";
    public static final String BOOK_HAS_ALREADY_BEEN_RETURN = "Book has already been returned.";
    public static final String NO_LATE_FEE_TO_PAY = "No late fee to pay.";

    // Reservation Operations
    public static final String USER_ALREADY_HAS_A_RESERVATION = "User already has a reservation.";
    public static final String USER_HAS_AN_UNCOMPLETED_RESERVATION = "User has an uncompleted reservation.";
    public static final String BOOK_IS_NOT_AVAILABLE_FOR_THE_SELECTED_DATE = "Book is not available for the selected date.";
    public static final String RESERVATION_ALREADY_COMPLETED = "Reservation already completed.";

}
