package ru.practicum.shareit.booking.model;


import java.util.Comparator;

public class BookingComparator implements Comparator<Booking> {

    @Override
    public int compare(Booking o1, Booking o2) {
        return o1.getStartBooking().compareTo(o2.getStartBooking());
    }
}