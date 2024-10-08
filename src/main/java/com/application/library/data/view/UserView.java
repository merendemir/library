package com.application.library.data.view;

import com.application.library.core.view.IntegerEntityView;
import com.application.library.enumerations.UserRole;

import java.util.Set;

public interface UserView extends IntegerEntityView {

    String getFirstName();

    String getLastName();

    String getEmail();

    Set<UserRole> getAuthorities();

}
