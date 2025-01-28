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
    idEvento BIGINT NOT NULL PRIMARY KEY, -- Identificatore univoco dell'evento
    idUtente VARCHAR(50) NOT NULL,       -- Identificatore dell'utente organizzatore
    titolo VARCHAR(100) NOT NULL,       -- Titolo dell'evento
    descrizione TEXT NOT NULL,          -- Descrizione dell'evento
    data DATE NOT NULL,                 -- Data dell'evento
    orario VARCHAR(20) NOT NULL,        -- Orario dell'evento
    link VARCHAR(255),
    nomeOrganizzatore VARCHAR(100),
    cognomeOrganizzatore VARCHAR(100),
    limitePartecipanti INT NOT NULL,    -- Limite dei partecipanti
    iscritti INT DEFAULT 0,             -- Numero degli iscritti
    stato BOOLEAN NOT NULL DEFAULT TRUE -- Stato dell'evento (attivo o meno)
);

-- Tabella Tracker
CREATE TABLE Tracker (
    idUtente VARCHAR(255) PRIMARY KEY,      -- ID univoco dell'utente (chiave primaria)
    letturaCorano INT NOT NULL DEFAULT 0,   -- Numero di pagine lette del Corano
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

-- Tabella Organizzatori
CREATE TABLE Organizzatori (
    idUtente VARCHAR(255) PRIMARY KEY,                  -- ID unico dell'utente
    nome     VARCHAR(100)        NOT NULL,              -- Nome dell'utente
    cognome  VARCHAR(100)        NOT NULL,              -- Cognome dell'utente
    username VARCHAR(100) UNIQUE NOT NULL,              -- Username unico
    email    VARCHAR(255) UNIQUE NOT NULL,              -- Email unica
    password VARCHAR(255)        NOT NULL,              -- Password dell'utente
    stato    BOOLEAN             NOT NULL DEFAULT FALSE, -- Stato (es. attivo/inattivo)
    titoloDiStudio TEXT NOT NULL                         -- Titolo di Studio
);

-- Tabella Partecipazioni
CREATE TABLE IF NOT EXISTS Partecipazioni (
    idUtente VARCHAR(50) NOT NULL,
    idEvento BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    dataIscrizione DATE NOT NULL,
    PRIMARY KEY (idUtente, idEvento),
    FOREIGN KEY (idUtente) REFERENCES Partecipanti(idUtente),
    FOREIGN KEY (idEvento) REFERENCES Eventi(idEvento)
);

