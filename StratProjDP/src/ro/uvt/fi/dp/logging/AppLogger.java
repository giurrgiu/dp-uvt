package ro.uvt.fi.dp.logging;

public final class AppLogger {

    private static final AppLogger INSTANCE = new AppLogger();

    private AppLogger() {
    }

    public static AppLogger getInstance() {
        return INSTANCE;
    }

    // Logger levels

    public void info(String msg) {
        System.out.println("[INFO]    " + msg);
    }

    public void success(String msg) {
        System.out.println("[SUCCESS] " + msg);
    }

    public void warning(String msg) {
        System.err.println("[WARNING] " + msg);
    }

    public void error(String msg) {
        System.err.println("[ERROR]   " + msg);
    }
}
