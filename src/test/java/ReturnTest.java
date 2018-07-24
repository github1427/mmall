/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午5:39 2018/7/24
 * @ Description：try catch finally执行return顺序
 */
public class ReturnTest {
    public int aaa(){
        int x=0;
        try {
            return ++x;
        }catch (Exception e){
            e.getStackTrace();
        }finally {
             return  ++x;
        }
    }

    public static void main(String[] args) {
        ReturnTest returnTest=new ReturnTest();
        System.out.println(returnTest.aaa());
    }
}
