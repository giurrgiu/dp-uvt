package ro.uvt.fi.dp.persistence;

import ro.uvt.fi.dp.bank.Bank;
import ro.uvt.fi.dp.logging.AppLogger;

import java.io.*;

public class BankDataStore {

    private static final String DEFAULT_FILE = "bank_data.dat";
    private final String filePath;
    private static final AppLogger log = AppLogger.getInstance();

    public BankDataStore() {
        this(DEFAULT_FILE);
    }

    public BankDataStore(String filePath) {
        this.filePath = filePath;
    }

    public void save(Bank bank) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(bank);
            log.success("Bank data saved to " + filePath);
        } catch (IOException e) {
            log.error("Failed to save bank data: " + e.getMessage());
            throw new UncheckedIOException("Failed to save bank data", e);
        }
    }

    public Bank load() {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            Bank bank = (Bank) ois.readObject();
            log.success("Bank data loaded from " + filePath);
            return bank;
        } catch (IOException | ClassNotFoundException e) {
            log.error("Failed to load bank data: " + e.getMessage());
            return null;
        }
    }
}
