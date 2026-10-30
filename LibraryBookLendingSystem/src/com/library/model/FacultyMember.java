package com.library.model;

public class FacultyMember extends Member {
    public FacultyMember(String id, String name) {
        super(id, name);
    }

    @Override
    public int getMaxBooksAllowed() {
        return 10;
    }

    @Override
    public double calculateLateFee(int daysLate) {
        return Math.max(0, daysLate) * 2.0;
    }
}
