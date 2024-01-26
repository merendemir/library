package com.application.library.core.view;

import java.time.LocalDateTime;

public interface BaseIntegerEntityView {

    Long getId();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
