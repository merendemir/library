package com.application.library.data.view;

import com.application.library.core.view.IntegerEntityView;

public interface UserView extends IntegerEntityView {

    String getFirstName();

    String getLastName();

    String getEmail();

}
