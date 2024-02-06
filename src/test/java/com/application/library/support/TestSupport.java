package com.application.library.support;

import com.application.library.data.dto.user.BaseUserDto;
import com.application.library.data.view.UserView;
import com.application.library.data.view.book.BookView;
import com.application.library.data.view.shelf.ShelfBaseView;
import com.application.library.data.view.shelf.ShelfView;
import com.application.library.enumerations.UserRole;
import com.application.library.model.Shelf;
import com.application.library.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public class TestSupport {

    public User getTestUser() {
        User user = new User();
        user.setFirstName("test_first_name");
        user.setLastName("test_last_name");
        user.setEmail("test_email");
        user.setPassword("test_password");
        user.setAuthorities(Set.of(UserRole.ROLE_USER));
        return user;
    }

    public BaseUserDto getBaseUserDto() {
        BaseUserDto baseUserDto = new BaseUserDto();
        baseUserDto.setId(1L);
        baseUserDto.setFirstName("test_first_name");
        baseUserDto.setLastName("test_last_name");
        baseUserDto.setEmail("test_email");
        baseUserDto.setAuthorities(Set.of(UserRole.ROLE_USER));
        return baseUserDto;
    }

    public Authentication getTestAuthentication() {
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
                return true;
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

    public UserView getTestUserView() {
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

    public Shelf getTestShelf() {
        Shelf shelf = new Shelf();
        shelf.setName("test_shelf");
        shelf.setCapacity(10);
        shelf.setAvailableCapacity(10);
        shelf.setBooks(Set.of());
        return shelf;
    }

    public ShelfView getTestShelfView() {
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

    public ShelfBaseView getTestShelfBaseView() {
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

}

