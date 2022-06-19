package someone.alcoholic.util;

import java.sql.Timestamp;
import java.util.Calendar;

public class DateUtil {

    public static Timestamp getAfterDate(Timestamp today, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(today.getTime());
        cal.add(Calendar.DATE, day);
        return new Timestamp(cal.getTime().getTime());
    }
}
