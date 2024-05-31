SET SERVEROUTPUT ON;

CREATE OR REPLACE PROCEDURE create_ninja_poder_tables AS
    v_count NUMBER;
BEGIN
    -- Comprovar si la taula NINJA existeix
    SELECT COUNT(*)
    INTO v_count
    FROM user_tables
    WHERE table_name = 'NINJA';

    -- Crear la taula NINJA si no existeix
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE TABLE NINJA (
                ID NUMBER(10) PRIMARY KEY,
                NOM VARCHAR2(50),
                ANY NUMBER(4,2),
                VIU NUMBER(1)
            )';
        DBMS_OUTPUT.PUT_LINE('La taula NINJA s''ha creat satisfactoriament.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('La taula NINJA ja existeix.');
    END IF;

    -- Comprovar si la taula PODER existeix
    SELECT COUNT(*)
    INTO v_count
    FROM user_tables
    WHERE table_name = 'PODER';

    -- Crear la taula PODER si no existeix
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE TABLE PODER (
                ID NUMBER PRIMARY KEY,
                TIPUS_CHAKRA VARCHAR2(50),
                QUANTITAT_CHAKRA NUMBER,
                NINJA_ID NUMBER,
                FOREIGN KEY (NINJA_ID) REFERENCES NINJA(ID)
            )';
        DBMS_OUTPUT.PUT_LINE('La taula PODER s''ha creat satisfactoriament.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('La taula PODER ja existeix.');
    END IF;

    -- Comprovar si la taula id_manager existeix
    SELECT COUNT(*)
    INTO v_count
    FROM user_tables
    WHERE table_name = 'ID_MANAGER';

    -- Crear la taula id_manager si no existeix
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE TABLE id_manager (
                table_name VARCHAR2(50) PRIMARY KEY,
                current_id NUMBER
            )';
        DBMS_OUTPUT.PUT_LINE('La taula id_manager s''ha creat satisfactoriament.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('La taula id_manager ja existeix.');
    END IF;

    -- Inicialitzar els valors dels IDs per les taules NINJA i PODER si no existeixen
    SELECT COUNT(*)
    INTO v_count
    FROM id_manager
    WHERE table_name = 'NINJA';

    IF v_count = 0 THEN
        INSERT INTO id_manager (table_name, current_id) VALUES ('NINJA', 0);
        DBMS_OUTPUT.PUT_LINE('S''ha inicialitzat l''entrada per NINJA a id_manager.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('L''entrada per NINJA ja existeix a id_manager.');
    END IF;

    SELECT COUNT(*)
    INTO v_count
    FROM id_manager
    WHERE table_name = 'PODER';

    IF v_count = 0 THEN
        INSERT INTO id_manager (table_name, current_id) VALUES ('PODER', 0);
        DBMS_OUTPUT.PUT_LINE('S''ha inicialitzat l''entrada per PODER a id_manager.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('L''entrada per PODER ja existeix a id_manager.');
    END IF;

    COMMIT;

    -- Crear funció get_next_id
    EXECUTE IMMEDIATE '
        CREATE OR REPLACE FUNCTION get_next_id(p_table_name IN VARCHAR2) RETURN NUMBER IS
            v_next_id NUMBER;
        BEGIN
            -- Actualitzar el current_id per la taula especificada i obtenir el següent ID
            UPDATE id_manager
            SET current_id = current_id + 1
            WHERE table_name = p_table_name
            RETURNING current_id INTO v_next_id;

            RETURN v_next_id;
        END;';
    DBMS_OUTPUT.PUT_LINE('La funció get_next_id s''ha creat o reemplaçat satisfactoriament.');

    -- Crear trigger trg_ninja_id
    EXECUTE IMMEDIATE '
        CREATE OR REPLACE TRIGGER trg_ninja_id
          BEFORE INSERT ON NINJA
          FOR EACH ROW
        BEGIN
          :new.ID := get_next_id(''NINJA'');
        END;';
    DBMS_OUTPUT.PUT_LINE('El trigger trg_ninja_id s''ha creat o reemplaçat satisfactoriament.');

    -- Crear trigger trg_poder_id
    EXECUTE IMMEDIATE '
        CREATE OR REPLACE TRIGGER trg_poder_id
          BEFORE INSERT ON PODER
          FOR EACH ROW
        BEGIN
          :new.ID := get_next_id(''PODER'');
        END;';
    DBMS_OUTPUT.PUT_LINE('El trigger trg_poder_id s''ha creat o reemplaçat satisfactoriament.');
END;
/

BEGIN
    create_ninja_poder_tables;
END;
/

