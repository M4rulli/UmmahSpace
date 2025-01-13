package misc;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final List<String> errors = new ArrayList<>();

    // Metodo per aggiungere un errore
    public void addError(String error) {
        errors.add(error);
    }

    // Controlla se la validazione è valida (senza errori)
    public boolean isValid() {
        return errors.isEmpty();
    }

    // Ritorna tutti gli errori
    public List<String> getErrors() {
        return errors;
    }

    // Ritorna gli errori come una singola stringa
    public String getErrorMessages() {
        return String.join("\n", errors);
    }
}
