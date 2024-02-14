package com.application.library.support;

import com.application.library.data.dto.BookCommentDto;
import com.application.library.data.dto.BookCommentRequestDto;
import com.application.library.data.dto.user.BaseUserDto;
import com.application.library.data.view.BookReservationView;
import com.application.library.data.view.ReadingListView;
import com.application.library.data.view.UserView;
import com.application.library.data.view.book.BaseBookView;
import com.application.library.data.view.book.BookView;
import com.application.library.data.view.shelf.ShelfBaseView;
import com.application.library.data.view.shelf.ShelfView;
import com.application.library.data.view.transaction.lend.LendTransactionAuthUserView;
import com.application.library.data.view.transaction.lend.LendTransactionView;
import com.application.library.enumerations.UserRole;
import com.application.library.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TestSupport {

    class testUser extends User {
        @Override
        public Long getId() {
            return 1L;
        }
    }

    class testUser2 extends User {
        @Override
        public Long getId() {
            return 2L;
        }
    }

    protected User getTestUser() {
        User user = new testUser();
        user.setFirstName("test_first_name");
        user.setLastName("test_last_name");
        user.setEmail("test_email");
        user.setPassword("test_password");
        user.setAuthorities(Set.of(UserRole.ROLE_USER));
        return user;
    }

    protected User getTestUser2() {
        User user = new testUser2();
        user.setFirstName("test_first_name");
        user.setLastName("test_last_name");
        user.setEmail("test_email");
        user.setPassword("test_password");
        user.setAuthorities(Set.of(UserRole.ROLE_USER));
        return user;
    }

    protected BaseUserDto getBaseUserDto() {
        BaseUserDto baseUserDto = new BaseUserDto();
        baseUserDto.setId(1L);
        baseUserDto.setFirstName("test_first_name");
        baseUserDto.setLastName("test_last_name");
        baseUserDto.setEmail("test_email");
        baseUserDto.setAuthorities(Set.of(UserRole.ROLE_USER));
        return baseUserDto;
    }

    protected Authentication getTestAuthentication(boolean authenticated) {
        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Set.of(new SimpleGrantedAuthority(UserRole.ROLE_USER.name()));
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return getTestUser();
            }

            @Override
            public boolean isAuthenticated() {
                return authenticated;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return "test_email";
            }
        };
    }

    protected UserView getTestUserView() {
        User testUser = getTestUser();
        return new UserView() {
            @Override
            public LocalDateTime getCreatedAt() {
                return null;
            }

            @Override
            public LocalDateTime getUpdatedAt() {
                return null;
            }

            @Override
            public Long getId() {
                return testUser.getId();
            }

            @Override
            public String getEmail() {
                return testUser.getEmail();
            }

            @Override
            public String getFirstName() {
                return testUser.getFirstName();
            }

            @Override
            public String getLastName() {
                return testUser.getLastName();
            }

        };
    }

    class testShelf extends Shelf {
        @Override
        public Long getId() {
            return 1L;
        }
    }
    protected Shelf getTestShelf() {
        Shelf shelf = new testShelf();
        shelf.setName("test_shelf");
        shelf.setCapacity(10);
        shelf.setAvailableCapacity(10);
        shelf.setBooks(Set.of());
        return shelf;
    }

    protected ShelfView getTestShelfView() {
        Shelf testShelf = getTestShelf();
        return new ShelfView() {
            @Override
            public Long getId() {
                return testShelf.getId();
            }

            @Override
            public String getName() {
                return testShelf.getName();
            }

            @Override
            public Integer getCapacity() {
                return testShelf.getCapacity();
            }

            @Override
            public Integer getAvailableCapacity() {
                return testShelf.getAvailableCapacity();
            }

            @Override
            public Set<BookView> getBooks() {
                return null;
            }
        };
    }

    protected ShelfBaseView getTestShelfBaseView() {
        Shelf testShelf = getTestShelf();
        return new ShelfBaseView() {
            @Override
            public Long getId() {
                return testShelf.getId();
            }

            @Override
            public String getName() {
                return testShelf.getName();
            }

            @Override
            public Integer getCapacity() {
                return testShelf.getCapacity();
            }

            @Override
            public Integer getAvailableCapacity() {
                return testShelf.getAvailableCapacity();
            }
        };
    }


    class testBook extends Book {
        @Override
        public Long getId() {
            return 1L;
        }
    }

    protected Book getTestBook() {
        Book book = new testBook();
        book.setName("test_name");
        book.setAuthor("test_author");
        book.setIsbn("test_isbn");
        book.setShelf(getTestShelf());
        return book;
    }

    protected BookView getTestBookView() {
        Book testBook = getTestBook();
        return new BookView() {
            @Override
            public LocalDateTime getCreatedAt() {
                return null;
            }

            @Override
            public LocalDateTime getUpdatedAt() {
                return null;
            }

            @Override
            public Long getId() {
                return testBook.getId();
            }

            @Override
            public String getName() {
                return testBook.getName();
            }

            @Override
            public String getAuthor() {
                return testBook.getAuthor();
            }

            @Override
            public String getIsbn() {
                return testBook.getIsbn();
            }

            @Override
            public Integer getPageCount() {
                return null;
            }

            @Override
            public String getPublisher() {
                return null;
            }

            @Override
            public String getPublishedAt() {
                return null;
            }

            @Override
            public String getLanguage() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public String getImageUrl() {
                return null;
            }

            @Override
            public Integer getTotalCount() {
                return null;
            }

            @Override
            public Integer getAvailableCount() {
                return null;
            }

            @Override
            public ShelfBaseView getShelf() {
                return getTestShelfBaseView();
            }
        };
    }

    class testReadingList extends ReadingList {
        @Override
        public Long getId() {
            return 1L;
        }
    }

    protected ReadingList getTestReadingList() {
        ReadingList readingList = new ReadingList();
        readingList.setUser(getTestUser());
        readingList.setBooks(new HashSet<>());
        return readingList;
    }

    protected ReadingListView getReadingListView() {
        ReadingList testReadingList = getTestReadingList();
        return new ReadingListView() {
            @Override
            public LocalDateTime getCreatedAt() {
                return null;
            }

            @Override
            public LocalDateTime getUpdatedAt() {
                return null;
            }

            @Override
            public Long getId() {
                return testReadingList.getId();
            }

            @Override
            public UserView getUser() {
                return getTestUserView();
            }

            @Override
            public Set<BookView> getBooks() {
                return null;
            }
        };
    }

    protected LendTransaction getTestLendTransaction(boolean returned) {
        LendTransaction lendTransaction = new LendTransaction();
        lendTransaction.setUser(getTestUser());
        lendTransaction.setBook(getTestBook());
        lendTransaction.setReturned(returned);
        return lendTransaction;
    }

    protected LendTransactionAuthUserView LendTransactionAuthUserView(boolean returned) {
        LendTransaction testLendTransaction = getTestLendTransaction(returned);
        return new LendTransactionAuthUserView() {
            @Override
            public LocalDateTime getCreatedAt() {
                return null;
            }

            @Override
            public LocalDateTime getUpdatedAt() {
                return null;
            }

            @Override
            public UUID getId() {
                return testLendTransaction.getId();
            }

            @Override
            public LocalDateTime getReturnDate() {
                return null;
            }

            @Override
            public LocalDate getDeadlineDate() {
                return null;
            }

            @Override
            public Double getLateFeePaid() {
                return null;
            }

            @Override
            public boolean isReturned() {
                return testLendTransaction.isReturned();
            }

            @Override
            public BaseBookView getBook() {
                return null;
            }
        };
    }

    protected LendTransactionView getLendTransactionView(boolean returned) {
        LendTransaction testLendTransaction = getTestLendTransaction(returned);
        return new LendTransactionView() {
            @Override
            public LocalDateTime getCreatedAt() {
                return null;
            }

            @Override
            public LocalDateTime getUpdatedAt() {
                return null;
            }

            @Override
            public UUID getId() {
                return testLendTransaction.getId();
            }

            @Override
            public LocalDateTime getReturnDate() {
                return null;
            }

            @Override
            public LocalDate getDeadlineDate() {
                return null;
            }

            @Override
            public Double getLateFeePaid() {
                return null;
            }

            @Override
            public boolean isReturned() {
                return testLendTransaction.isReturned();
            }

            @Override
            public BaseBookView getBook() {
                return null;
            }

            @Override
            public UserView getUser() {
                return getTestUserView();
            }

            @Override
            public UserView getLender() {
                return null;
            }
        };
    }

    protected BookCommentRequestDto getBookCommentRequestDto() {
        BookCommentRequestDto bookCommentRequestDto = new BookCommentRequestDto();
        bookCommentRequestDto.setCommentText("test_comment_text");
        bookCommentRequestDto.setRating(5.0);
        return bookCommentRequestDto;
    }

    class testBookComment extends BookComment {
        @Override
        public Long getId() {
            return 1L;
        }
    }

    protected BookComment getTestBookComment() {
        BookComment bookComment = new testBookComment();
        bookComment.setUser(getTestUser());
        bookComment.setBook(getTestBook());
        bookComment.setCommentText("test_comment_text");
        bookComment.setRating(5.0);
        return bookComment;
    }

    protected BookReservationView getBookReservationView() {
        BookReservation bookReservation = new BookReservation();
        bookReservation.setUser(getTestUser());
        bookReservation.setBook(getTestBook());
        bookReservation.setReservationDate(LocalDate.now());
        return new BookReservationView() {
            @Override
            public LocalDateTime getCreatedAt() {
                return null;
            }

            @Override
            public LocalDateTime getUpdatedAt() {
                return null;
            }

            @Override
            public Long getId() {
                return bookReservation.getId();
            }

            @Override
            public LocalDate getReservationDate() {
                return bookReservation.getReservationDate();
            }

            @Override
            public UserView getUser() {
                return getTestUserView();
            }

            @Override
            public BookView getBooks() {
                return null;
            }


            @Override
            public boolean isCompleted() {
                return false;
            }
        };
    }

    protected BookCommentDto getBookCommentDto() {
        return new BookCommentDto(
                1L,
                null,
                null,
                "comment",
                5.0,
                "userFirstName",
                "userLastName"
        );
    }

    protected static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

