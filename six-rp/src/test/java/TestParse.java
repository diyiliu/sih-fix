import com.tiza.sih.rp.support.model.IDataProcess;
import com.tiza.sih.rp.support.util.CommonUtil;
import com.tiza.sih.rp.support.util.DataProcessUtil;
import org.junit.Test;

/**
 * Description: TestParse
 * Author: DIYILIU
 * Update: 2019-06-06 16:44
 */
public class TestParse {

    @Test
    public void test() throws Exception{
        IDataProcess process = DataProcessUtil.getProcess(0x01);
        System.out.println(process);
    }

    @Test
    public void test1(){
        Object obj = 123;

        System.out.println(obj);
        obj = 0.5;

        System.out.println(obj);
    }

    @Test
    public void test2(){
        byte[] b = new byte[]{7, 9};
        System.out.println(CommonUtil.bytes2BinaryStr(b));
    }
}
