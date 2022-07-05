package com.logni.account.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class AcDateTimeUtil {

    public static Instant getLastDateEndTime(){
        LocalDateTime localStartDate = LocalDate.now().atStartOfDay();
        return  localStartDate.toInstant(ZoneOffset.UTC);
    }

    public static Instant getToDayLocalOpenTime(){
        LocalDateTime localStartDate = LocalDate.now().atStartOfDay();
        //System.out.println(localStartDate.get);
        return  localStartDate.toInstant(ZoneOffset.UTC);
    }

    public static Instant getToDayUTCOpenTime(){
        LocalDateTime localStartDate = LocalDate.now().atStartOfDay();
        ZonedDateTime zonedDateTime = localStartDate.atZone(ZoneId.systemDefault());
        return  zonedDateTime.toInstant();
    }

    public static Instant convertStringToGMT(String stringDate){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try{
            date = simpleDateFormat.parse(stringDate);
            return date.toInstant();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    //public static Instant getLocalToInstant()
    //public static Instant getUTCtoLocalTime()


}
