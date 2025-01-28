package engclasses.beans;

public class RegistrazioneBean {
    private String nome;
    private String cognome;
    private String username;
    private String email;
    private String password;
    private Boolean seiOrganizzatore;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
    private String titoloDiStudio;
    private String nomeOrganizzatore;
    private String cognomeOrganizzatore;

    public RegistrazioneBean() {}

    // Getter e Setter
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getSeiOrganizzatore() {
        return seiOrganizzatore;
    }

    public void setSeiOrganizzatore(Boolean seiOrganizzatore) {
        this.seiOrganizzatore = seiOrganizzatore;
    }

    // Getter e Setter per le password
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getTitoloDiStudio() {
        return titoloDiStudio;
    }

    public void setTitoloDiStudio(String titoloDiStudio) {
        this.titoloDiStudio = titoloDiStudio;
    }

    public String getNomeOrganizzatore() { return nomeOrganizzatore;}

    public void setNomeOrganizzatore(String nomeOrganizzatore) { this.nomeOrganizzatore = nomeOrganizzatore; }

    public String getCognomeOrganizzatore() { return nomeOrganizzatore;}

    public void setCognomeOrganizzatore(String cognomeOrganizzatore) { this.cognomeOrganizzatore = cognomeOrganizzatore; }
}
