package com.library.model;

public class GuestMember extends Member {
    public GuestMember(String id, String name) {
        super(id, name);
    }

    @Override
    public int getMaxBooksAllowed() {
        return 1;
    }

    @Override
    public double calculateLateFee(int daysLate) {
        return daysLate <= 0 ? 0.0 : Math.max(50.0, daysLate * 20.0);
    }
}
