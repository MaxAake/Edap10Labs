import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.Tracker;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

    private final JPanel workList;
    private final JPanel progressList;

    private final JProgressBar mainProgressBar;
    private final ExecutorService threadPool;

    // -----------------------------------------------------------------------

    private CodeBreaker() {
        StatusWindow w = new StatusWindow();

        workList = w.getWorkList();
        progressList = w.getProgressList();
        mainProgressBar = w.getProgressBar();
        threadPool = Executors.newFixedThreadPool(2);
        w.enableErrorChecks();
    }

    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CodeBreaker codeBreaker = new CodeBreaker();
            new Sniffer(codeBreaker).start();
        });

    }

    // -----------------------------------------------------------------------

    /** Called by a Sniffer thread when an encrypted message is obtained. */
    @Override
    public void onMessageIntercepted(String message, BigInteger n) {
        System.out.println("message intercepted (N=" + n + ")...");
        SwingUtilities.invokeLater(() -> {
            WorklistItem item = new WorklistItem(n, message);
            JButton tmp = new JButton("Break");
            item.add(tmp);
            tmp.addActionListener(e -> {
                {
                    crack(workList, progressList, item, n, message);
                }
            });
            workList.add(item);
        });
    }

    private void crack(JPanel list, JPanel progressList, WorklistItem item, BigInteger n, String message) {
        SwingUtilities.invokeLater(() -> {
            list.remove(item);
            ProgressItem progressItem = new ProgressItem(n, message);
            progressList.add(progressItem);
            Tracker tracker = new Tracker(progressItem.getProgressBar(), mainProgressBar);
            Future<String> result = threadPool.submit(() -> {
                try {
                    String text = Factorizer.crack(message, n, tracker);
                    progressItem.finish(progressList, text, mainProgressBar);
                    return text;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "error";
                }

            });
            progressItem.working(progressList, tracker, result, mainProgressBar);
        });
    }

    /*
     * No swing errros were reported when removing the invokeLater from this method.
     * Ask supervisor!
     */
    private void removeItem(JPanel list, ProgressItem progressItem) {
        SwingUtilities.invokeLater(() -> {
            list.remove(progressItem);
            mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
            mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
        });
    }
}
