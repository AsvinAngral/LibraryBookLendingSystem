package com.library.model;

public class StudentMember extends Member {
    public StudentMember(String id, String name) {
        super(id, name);
    }

    @Override
    public int getMaxBooksAllowed() {
        return 3;
    }

    @Override
    public double calculateLateFee(int daysLate) {
        return Math.max(0, daysLate) * 5.0;
    }
}
