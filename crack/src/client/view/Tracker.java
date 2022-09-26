package client.view;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import rsa.ProgressTracker;

public class Tracker implements ProgressTracker {
    private int totalProgress = 0;

    private JProgressBar bar;
    private JProgressBar mainBar;

    public Tracker(JProgressBar bar, JProgressBar mainBar) {
        this.bar = bar;
        this.mainBar = mainBar;
        setMaximums();
    }

    private void setMaximums() {
        SwingUtilities.invokeLater(() -> {
            bar.setMaximum(1000000);
            mainBar.setMaximum(mainBar.getMaximum() + 1000000);
        });
    }

    private void increaseProgress(int ppmDelta) {
        totalProgress += ppmDelta;
        SwingUtilities.invokeLater(() -> {
            bar.setValue(totalProgress);
            mainBar.setValue(mainBar.getValue() + ppmDelta);
        });
    }

    public void finish() {
        increaseProgress(1000000 - totalProgress);
    }

    /**
     * Called by Factorizer to indicate progress. The total sum of
     * ppmDelta from all calls will add upp to 1000000 (one million).
     * 
     * @param ppmDelta portion of work done since last call,
     *                 measured in ppm (parts per million)
     */
    @Override
    public void onProgress(int ppmDelta) {
        ppmDelta = Math.min(ppmDelta, 1000000 - totalProgress);
        increaseProgress(ppmDelta);
    }
}