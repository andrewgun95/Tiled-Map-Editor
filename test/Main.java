
import java.util.List;
import javax.swing.SwingWorker;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Andreas
 */
public class Main {

    /**
     * Creates an Example SwingWorker
     * @return 
     */
    public SwingWorker createWorker() {
        return new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Start Progress
                setProgress(0);
                waitFor(500);

                // Example Loop
                for (int iCount = 1; iCount <= 20; iCount++) {
                    // Is an even number?
                    if (iCount % 2 == 0) {
                        publish(iCount);
                    }

                    // Set Progress
                    setProgress((iCount * 100) / 20);
                    waitFor(250);
                }

                // Finished
                return true;
            }

            @Override
            protected void process(List<Integer> chunks) {
                // Get Info
                for (int number : chunks) {
                    System.out.println("Found even number: " + number);
                }
            }

            @Override
            protected void done() {
                
            }
        };
    } // End of Method: createWorker()


    /**
     * Wait the given time in milliseconds
     * @param iMillis
     */
    private void waitFor (int iMillis) {
        try {
            Thread.sleep(iMillis);
        }
        catch (Exception ex) {
            System.err.println(ex);
        }
    } // End of Method: waitFor()


    public static void main(String[] args) {
        // Create the worker
        SwingWorker work = new Main().createWorker();
        work.execute();

        // Wait for it to finish
        while (!work.isDone()) {
            // Show Progress
            try {
                int iProgress = work.getProgress();
                System.out.println("Progress %" + iProgress);
                Thread.sleep(500);
            }
            catch (Exception ex) {
                System.err.println(ex);
            }
        } // End of Loop: while (!work.isDone())
    } // End of: main()
    

}
