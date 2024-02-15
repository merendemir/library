package com.application.library.data.dto;

import java.time.LocalDate;
import java.util.Objects;

public class BookReservationRequestDto {

    private LocalDate reservationDate;

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookReservationRequestDto that = (BookReservationRequestDto) o;
        return Objects.equals(reservationDate, that.reservationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationDate);
    }
}
