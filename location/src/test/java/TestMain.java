import org.junit.Test;

/**
 * Description: TestMain
 * Author: DIYILIU
 * Update: 2019-02-11 12:41
 */
public class TestMain {

    @Test
    public void test(){
        String str = "0x10";

        int i = Integer.valueOf(str.substring(2), 16);

        System.out.println(i);
    }
}
