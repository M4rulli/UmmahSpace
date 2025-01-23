-- Crea il database solo se non esiste già
CREATE DATABASE IF NOT EXISTS `UmmahSpaceDBSM` /*!40100 DEFAULT CHARACTER SET utf8mb4 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `UmmahSpaceDBSM`;

-- Tabella Partecipanti
CREATE TABLE IF NOT EXISTS Partecipanti (
    idUtente VARCHAR(50) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    stato BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabella Eventi
CREATE TABLE IF NOT EXISTS Eventi (
    idEvento INT AUTO_INCREMENT PRIMARY KEY,
    titolo VARCHAR(100) NOT NULL,
    descrizione TEXT NOT NULL,
    data DATE NOT NULL,
    orario TIME NOT NULL,
    limitePartecipanti INT NOT NULL,
    iscritti INT DEFAULT 0,
    stato BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE Tracker (
    idUtente VARCHAR(255) PRIMARY KEY,      -- ID univoco dell'utente (chiave primaria)
    letturaCorano INT NOT NULL DEFAULT 0,   -- Numero di pagine lette del Corano
    giorniDigiuno INT NOT NULL DEFAULT 0,   -- Numero di giorni di digiuno completati
    preghiereComplete INT NOT NULL DEFAULT 0, -- Numero di preghiere completate
    goal INT NOT NULL DEFAULT 0,            -- Obiettivo giornaliero
    progress DOUBLE NOT NULL DEFAULT 0,      -- Progresso attuale
    haDigiunato BOOLEAN NOT NULL DEFAULT 0, -- Indica se l'utente ha digiunato
    noteDigiuno TEXT,                       -- Note relative al digiuno
    fajr BOOLEAN NOT NULL DEFAULT 0,        -- Stato della preghiera Fajr
    dhuhr BOOLEAN NOT NULL DEFAULT 0,       -- Stato della preghiera Dhuhr
    asr BOOLEAN NOT NULL DEFAULT 0,         -- Stato della preghiera Asr
    maghrib BOOLEAN NOT NULL DEFAULT 0,     -- Stato della preghiera Maghrib
    isha BOOLEAN NOT NULL DEFAULT 0         -- Stato della preghiera Isha

);