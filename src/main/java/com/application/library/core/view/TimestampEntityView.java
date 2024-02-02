package com.application.library.core.view;

import java.time.LocalDateTime;

public interface TimestampEntityView {

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
