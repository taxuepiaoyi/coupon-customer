import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPoolExecutorTest{

    public static void main(String[] args) throws Exception{
        ThreadPoolExecutorTest executorTest = new ThreadPoolExecutorTest() ;
        executorTest.testSubmit() ;
        executorTest.testExecute();
    }


    public void testSubmit() throws Exception{
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future<String> submit = executorService.submit(() -> {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "abc";
        });
        String s = submit.get();
        System.out.println(s);
        executorService.shutdown();
    }

    public void testExecute(){
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.execute(() -> {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }
}
