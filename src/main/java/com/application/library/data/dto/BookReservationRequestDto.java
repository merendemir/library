package com.application.library.data.dto;

import java.time.LocalDate;

public class BookReservationRequestDto {

    private LocalDate reservationDate;

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }
}
