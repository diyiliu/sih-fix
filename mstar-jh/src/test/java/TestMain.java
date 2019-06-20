import org.joda.time.DateTime;
import org.junit.Test;

/**
 * Description: TestMain
 * Author: DIYILIU
 * Update: 2019-06-18 14:22
 */
public class TestMain {


    @Test
    public void test(){
        DateTime dt = new DateTime();

        System.out.println(new DateTime().withDayOfYear(dt.getDayOfYear()).withMillisOfDay(0));
    }
}
