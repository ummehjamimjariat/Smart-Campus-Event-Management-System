package com.smartcampus.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private static final AtomicInteger userCnt   = new AtomicInteger(100);
    private static final AtomicInteger eventCnt  = new AtomicInteger(100);
    private static final AtomicInteger regCnt    = new AtomicInteger(1000);
    private static final AtomicInteger ticketCnt = new AtomicInteger(5000);

    public static String generateUserId(String role) {
        if ("STUDENT".equals(role))   return "STU" + userCnt.incrementAndGet();
        if ("ORGANIZER".equals(role)) return "ORG" + userCnt.incrementAndGet();
        return "ADM" + userCnt.incrementAndGet();
    }

    public static String generateEventId(String type) {
        if ("ACADEMIC".equals(type)) return "ACE" + eventCnt.incrementAndGet();
        if ("CULTURAL".equals(type)) return "CUE" + eventCnt.incrementAndGet();
        return "SPE" + eventCnt.incrementAndGet();
    }

    public static String generateRegistrationId() {
        return "REG" + regCnt.incrementAndGet();
    }

    public static String generateTicketId() {
        return "TKT" + ticketCnt.incrementAndGet();
    }

    // Generate a realistic-looking transaction ID like bKash/Rocket
    public static String generateTransactionId(String method) {
        long ts = System.currentTimeMillis();
        String suffix = String.valueOf(ts).substring(7); // last 6 digits of timestamp
        if ("bKash".equals(method))  return "BK" + ts % 100000000L;
        if ("Rocket".equals(method)) return "RK" + ts % 100000000L;
        return "NG" + ts % 100000000L; // Nagad
    }
}
