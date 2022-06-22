package someone.alcoholic.util;

import java.sql.Timestamp;
import java.util.Calendar;

public class DateUtil {

    public static Timestamp getDateAfterTime(Timestamp today, int type, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(today.getTime());
        cal.add(type, day);
        return new Timestamp(cal.getTime().getTime());
    }

}
