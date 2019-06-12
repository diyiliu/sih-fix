import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description: DateUtil
 * Author: DIYILIU
 * Update: 2016-03-21 16:03
 */
public class DateUtil {

    public static Date stringToDate(String datetime) {

        return stringToDate(datetime, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date stringToDate(String datetime, String format) {
        DateFormat df = new SimpleDateFormat(format);

        Date date = null;
        if (datetime != null && datetime.length() > 0) {
            try {

                date = df.parse(datetime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return date;
    }

    public static String dateToString(Date date) {

        if (date == null) {

            return null;
        }

        return String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", date);
    }

    public static String dateToString(Date date, String format){

        if (date == null) {

            return null;
        }

        return String.format(format, date);
    }
}
