-- auto-generated definition
-- auto-generated definition
CREATE TABLE Achats
(
    idAchat         VARCHAR2(25) NOT NULL
        CONSTRAINT PK_Achats
            PRIMARY KEY,
    idComposants    VARCHAR2(25) NOT NULL,
    Qte             NUMBER NOT NULL,
    PUAchat         NUMBER(10, 2) NOT NULL,
    DateAchat       DATE DEFAULT SYSDATE
)
/

/



-- Facultatif : Ajout de contraintes pour s'assurer que Qte et PUAchat sont toujours positifs
ALTER TABLE Achats
    ADD CONSTRAINT chk_qte_positive CHECK (Qte > 0);

ALTER TABLE Achats
    ADD CONSTRAINT chk_pu_positive CHECK (PUAchat > 0);

-- auto-generated definition
CREATE TABLE Composants
(
    idComposants VARCHAR2(25) NOT NULL
        CONSTRAINT PK_Composants
            PRIMARY KEY,
    Libelle      VARCHAR2(100) NOT NULL,
    Description  VARCHAR2(255)
)
/

-- auto-generated definition
CREATE TABLE BlocsVaovao
(
    idBlocsVaovao           VARCHAR2(25) NOT NULL
        CONSTRAINT PK_BlocsVaovao
            PRIMARY KEY,
    longueur          NUMBER,
    largeur           NUMBER,
    hauteur           NUMBER,
    volume            NUMBER,
    prix_Revient      NUMBER,
    date_fabrication  DATE,
    heure_fabrication VARCHAR2(8) -- Format HH:MM:SS
)
/

-- auto-generated definition
CREATE TABLE Machines
(
    idMachine  VARCHAR2(25) NOT NULL
        CONSTRAINT PK_Machines
            PRIMARY KEY,
    NomMachine VARCHAR2(100) NOT NULL
)
/

-- Ajouter la colonne idSource à la table Blocs
ALTER TABLE BLOCSVAOVAO
    ADD idSource VARCHAR2(25);

ALTER TABLE BLOCSVAOVAO
    ADD PRIX_REVIENT_PRA VARCHAR2(25);

ALTER TABLE COMPOSANTS
    ADD QTE VARCHAR2(25);


-- Définir idSource comme clé étrangère référencée par idMachine de la table Machines
ALTER TABLE BLOCSVAOVAO
    ADD CONSTRAINT FK_Blocs_Machines
        FOREIGN KEY (idSource)
            REFERENCES Machines (idMachine);
/

CREATE SEQUENCE achats_seq
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
    NOCYCLE
    CACHE 20;

CREATE SEQUENCE blocsvaovao_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

INSERT INTO MACHINES (IDMACHINE, NOMMACHINE)
VALUES ('ID_12345', 'Machine Exemple');

BEGIN
    DELETE FROM BLOCSVAOVAO;
    COMMIT;
END;

BEGIN
    DELETE FROM BLOCSVAOVAO;
    DELETE FROM ACHATS;


    COMMIT;
END;
DROP SEQUENCE BLOCSVAOVAO_SEQ;
