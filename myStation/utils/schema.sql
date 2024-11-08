CREATE TABLE type_carburant (
    id VARCHAR2(255) NOT NULL,
    libelle VARCHAR2(255) NOT NULL,
    id_unite VARCHAR2(255),
    CONSTRAINT pk_type_carburant PRIMARY KEY (id)
);

CREATE TABLE cuve (
    id VARCHAR2(255) NOT NULL,
    max NUMBER(8, 2) NOT NULL,
    id_carb VARCHAR2(255) ,
    CONSTRAINT pk_cuve PRIMARY KEY (id)
);

CREATE TABLE pompiste (
    id VARCHAR2(255) NOT NULL,
    nom VARCHAR2(255) NOT NULL,
    CONSTRAINT pk_pompiste PRIMARY KEY (id)
);

CREATE TABLE pompe (
    id VARCHAR2(255) NOT NULL,
    id_cuve VARCHAR2(255),
    nom VARCHAR2(255) NOT NULL,
    CONSTRAINT pk_pompe PRIMARY KEY (id)
);

CREATE TABLE carburant (
    id VARCHAR2(255) NOT NULL,
    nom VARCHAR2(255) NOT NULL,
    desce VARCHAR2(255),
    pu_achat NUMBER(8, 2) NOT NULL,
    pu_vente NUMBER(8, 2) NOT NULL,
    id_type_carburant VARCHAR2(255),
    CONSTRAINT pk_carburant PRIMARY KEY (id)
);

CREATE TABLE prelevement (
    id VARCHAR2(255) NOT NULL,
    qte NUMBER(8, 2) NOT NULL,
    idPrelevementAnterieur VARCHAR2(255) ,
    idUtilisateur VARCHAR2(255),
    idPompe VARCHAR2(255),
    daty DATE NOT NULL,
    CONSTRAINT pk_prelevement PRIMARY KEY (id)
);
ALTER TABLE prelevement ADD heure VARCHAR(250) NOT NULL;


SELECT *
FROM (
    SELECT *
    FROM prelevement
    WHERE idPompe = 'PMP000097'
    ORDER BY daty DESC, heure DESC
)
WHERE ROWNUM = 1;
SPOOL OFF;


CREATE TABLE stock (
    id VARCHAR2(255) NOT NULL,
    id_cuve VARCHAR2(255),
    id_type_mvt NUMBER,
    qte NUMBER(18, 2) NOT NULL,
    pu NUMBER(8, 2) NOT NULL,
    daty DATE NOT NULL,
    CONSTRAINT pk_stock PRIMARY KEY (id)
);
ALTER TABLE stock ADD etatDeStock DECIMAL(18,2) NOT NULL;

CREATE TABLE type_mvt (
    id NUMBER NOT NULL,
    libelle VARCHAR2(255) NOT NULL,
    CONSTRAINT pk_type_mvt PRIMARY KEY (id)
);

CREATE TABLE unite (
    id VARCHAR2(255) NOT NULL,
    val VARCHAR2(255) NOT NULL,
    desce VARCHAR2(255),
    CONSTRAINT pk_unite PRIMARY KEY (id)
);

CREATE TABLE cuve_mesures (
    id VARCHAR2(250) NOT NULL,
    idCuve VARCHAR2(250) NOT NULL,
    mesure NUMBER(19) NOT NULL,
    qteLitre NUMBER(19) NOT NULL,
    idUM VARCHAR2(250) NOT NULL,
    CONSTRAINT pk_cuve_mesures PRIMARY KEY (id)
);

CREATE TABLE unite_mesure (
    id VARCHAR2(250) NOT NULL,
    libelle VARCHAR2(255) NOT NULL,
    taux_cm NUMBER(19) NOT NULL,
    CONSTRAINT pk_unite_mesure PRIMARY KEY (id)
);


ALTER TABLE carburant
ADD CONSTRAINT fk_carburant_type_carburant FOREIGN KEY (id_type_carburant) REFERENCES type_carburant(id);

ALTER TABLE type_carburant
ADD CONSTRAINT fk_type_carburant_unite FOREIGN KEY (id_unite) REFERENCES unite(id);

ALTER TABLE prelevement
ADD CONSTRAINT fk_prelevement_utilisateur FOREIGN KEY (idUtilisateur) REFERENCES pompiste(id);

ALTER TABLE cuve
ADD CONSTRAINT fk_cuve_carburant FOREIGN KEY (id_carb) REFERENCES carburant(id);

ALTER TABLE prelevement
ADD CONSTRAINT fk_prel_prel_anterieur FOREIGN KEY (idPrelevementAnterieur) REFERENCES prelevement(id);

ALTER TABLE prelevement
ADD CONSTRAINT fk_prelevement_pompe FOREIGN KEY (idPompe) REFERENCES pompe(id);

ALTER TABLE stock
ADD CONSTRAINT fk_stock_type_mvt FOREIGN KEY (id_type_mvt) REFERENCES type_mvt(id);

ALTER TABLE stock
ADD CONSTRAINT fk_stock_cuve FOREIGN KEY (id_cuve) REFERENCES cuve(id);

ALTER TABLE pompe
ADD CONSTRAINT fk_pompe_cuve FOREIGN KEY (id_cuve) REFERENCES cuve(id);

ALTER TABLE cuve_mesures
ADD FOREIGN KEY (idCuve) REFERENCES cuve (id);

ALTER TABLE cuve_mesures
ADD CONSTRAINT cuve_mesures_idum_foreign
FOREIGN KEY (idUM)
REFERENCES unite_mesure (id);



ALTER TABLE cuve ADD label VARCHAR2(255) NOT NULL;




INSERT INTO type_mvt VALUES(-1,'Sortie');
INSERT INTO type_mvt VALUES(1,'Entrée');


CREATE SEQUENCE type_carburant_seq START WITH 1;
CREATE SEQUENCE cuve_seq START WITH 1;
CREATE SEQUENCE pompiste_seq START WITH 1;
CREATE SEQUENCE pompe_seq START WITH 1;
CREATE SEQUENCE carburant_seq START WITH 1;
CREATE SEQUENCE prelevement_seq START WITH 1;
CREATE SEQUENCE stock_seq START WITH 1;
CREATE SEQUENCE unite_seq START WITH 1;


CREATE OR REPLACE FUNCTION GET_SEQ_TYPE_CARBURANT
   RETURN NUMBER
IS
   retour   NUMBER;
BEGIN
   SELECT TYPE_CARBURANT_SEQ.NEXTVAL INTO retour FROM DUAL;
   RETURN retour;
END;
/
CREATE OR REPLACE FUNCTION GET_SEQ_CUVE
   RETURN NUMBER
IS
   retour NUMBER;
BEGIN
   SELECT cuve_seq.NEXTVAL INTO retour FROM DUAL;
   RETURN retour;
END;
/
CREATE OR REPLACE FUNCTION GET_SEQ_POMPISTE
   RETURN NUMBER
IS
   retour NUMBER;
BEGIN
   SELECT pompiste_seq.NEXTVAL INTO retour FROM DUAL;
   RETURN retour;
END;
/
CREATE OR REPLACE FUNCTION GET_SEQ_POMPE
   RETURN NUMBER
IS
   retour NUMBER;
BEGIN
   SELECT pompe_seq.NEXTVAL INTO retour FROM DUAL;
   RETURN retour;
END;
/
CREATE OR REPLACE FUNCTION GET_SEQ_CARBURANT
   RETURN NUMBER
IS
   retour NUMBER;
BEGIN
   SELECT carburant_seq.NEXTVAL INTO retour FROM DUAL;
   RETURN retour;
END;
/
CREATE OR REPLACE FUNCTION GET_SEQ_PRELEVEMENT
   RETURN NUMBER
IS
   retour NUMBER;
BEGIN
   SELECT prelevement_seq.NEXTVAL INTO retour FROM DUAL;
   RETURN retour;
END;
/
CREATE OR REPLACE FUNCTION GET_SEQ_STOCK
   RETURN NUMBER
IS
   retour NUMBER;
BEGIN
   SELECT stock_seq.NEXTVAL INTO retour FROM DUAL;
   RETURN retour;
END;
/
CREATE OR REPLACE FUNCTION GET_SEQ_UNITE
   RETURN NUMBER
IS
   retour NUMBER;
BEGIN
   SELECT unite_seq.NEXTVAL INTO retour FROM DUAL;
   RETURN retour;
END;
/

CREATE VIEW v_prelevement_cuve as SELECT prelevement.*,pompe.id_cuve FROM prelevement JOIN POMPE ON POMPE.ID=prelevement.IDPOMPE;
CREATE VIEW v_prelevement_quantity as SELECT prelevement_quantity.*,pompe.id_cuve FROM prelevement_quantity JOIN POMPE ON POMPE.ID=prelevement_quantity.IDPOMPE;


CREATE TABLE prelevement_quantity(
    id varchar2(250) PRIMARY KEY,
    idAnt varchar2(250) NOT NULL,
    idAct varchar2(250) NOT NULL,
    qty number(18,2) NOT NULL,
    daty date NOT NULL,
    idPompe varchar2(250) NOT NULL,
    idUtilisateur varchar2(250) NOT NULL
);
ALTER TABLE prelevement_quantity ADD FOREIGN KEY (idAnt) references prelevement(id);
ALTER TABLE prelevement_quantity ADD FOREIGN KEY (idAct) references prelevement(id);
ALTER TABLE prelevement_quantity ADD FOREIGN KEY (idPompe) references pompe(id);
ALTER TABLE prelevement_quantity ADD FOREIGN KEY (idUtilisateur) references pompiste(id);

CREATE SEQUENCE SEQ_PREL_QTY START WITH 1;
CREATE OR REPLACE FUNCTION GET_SEQ_PREL_QTY
   RETURN NUMBER
IS
   retour NUMBER;
BEGIN
   SELECT SEQ_PREL_QTY.NEXTVAL INTO retour FROM DUAL;
   RETURN retour;
END;
/

create table HISTORIQUE
(
    IDHISTORIQUE   VARCHAR2(20) not null
        constraint PK_HISTORIQUE
            primary key,
    DATEHISTORIQUE DATE,
    HEURE          VARCHAR2(25),
    OBJET          VARCHAR2(100),
    ACTION         VARCHAR2(50),
    IDUTILISATEUR  NUMBER       not null,
    REFOBJET       VARCHAR2(255)
)
