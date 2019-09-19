--========================================================================================
CREATE USER PACIFICO_DEV
       IDENTIFIED BY <Enter User Password Here>
       DEFAULT TABLESPACE TSD_PACIFICO;
GRANT CREATE SESSION TO PACIFICO_DEV;
GRANT CONNECT TO PACIFICO_DEV;
ALTER USER PACIFICO_DEV QUOTA UNLIMITED ON TSD_PACIFICO;
--========================================================================================


---------------------------------------------------------------------------------
-------------- DROP TABLAS ---------------------------------------------------------
---------------------------------------------------------------------------------
DROP TABLE PACIFICO_DEV.SEGURIDAD_PERMISOS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.SEGURIDAD_ROLES CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.SEGURIDAD_PERMISO_ROL CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.SEGURIDAD_USUARIOS CASCADE CONSTRAINTS;

DROP TABLE PACIFICO_DEV.PLAN_DE_CUENTAS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.PLAN_DE_CUENTA_LINEAS CASCADE CONSTRAINTS;

DROP TABLE PACIFICO_DEV.PARTIDAS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.PARTIDA_LINEAS CASCADE CONSTRAINTS;

DROP TABLE PACIFICO_DEV.PARTIDA_CUENTA_CONTABLE CASCADE CONSTRAINTS;

DROP TABLE PACIFICO_DEV.CENTRO_NIVELES CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.CENTRO_TIPOS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.CENTROS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.CENTRO_LINEAS CASCADE CONSTRAINTS;

DROP TABLE PACIFICO_DEV.CUENTA_PARTIDA_CENTRO CASCADE CONSTRAINTS;

DROP TABLE PACIFICO_DEV.ENTIDAD_TIPOS CASCADE CONSTRAINTS;

DROP TABLE PACIFICO_DEV.PRODUCTOS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.PRODUCTO_LINEAS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.PRODUCTO_GRUPOS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.SUBCANALS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.SUBCANAL_LINEAS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.SUBCANAL_GRUPOS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.JERARQUIA CASCADE CONSTRAINTS;

DROP TABLE PACIFICO_DEV.DRIVER_TIPOS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.DRIVERS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.DRIVER_LINEAS CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.DRIVER_OBJETO_LINEAS CASCADE CONSTRAINTS;


DROP TABLE PACIFICO_DEV.BOLSA_DRIVER CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.ENTIDAD_ORIGEN_DRIVER CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.OBJETO_DRIVER CASCADE CONSTRAINTS;

DROP TABLE PACIFICO_DEV.EJECUCIONES CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.OBJETO_LINEAS CASCADE CONSTRAINTS;

DROP TABLE PACIFICO_DEV.TRAZA CASCADE CONSTRAINTS;
DROP TABLE PACIFICO_DEV.TRAZA_CASCADA CASCADE CONSTRAINTS;


---------------------------------------------------------------------------------
-------------- SEGURIDAD ---------------------------------------------------------
---------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.SEGURIDAD_PERMISOS (
  codigo VARCHAR2(50) NOT NULL,
  nombre VARCHAR2(150) NOT NULL,
  descripcion VARCHAR2(150),
  CONSTRAINT seguridad_permisos_pk PRIMARY KEY(codigo)
);
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('INI_VER','INICIO - VER','Ver el módulo de Inicio.');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_VER','APROVISIONAMIENTO - VER','Ver el módulo de Aprovisionamiento.');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_VER','PARAMETRIZACIÓN - VER','Ver el módulo de Parametrización.');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PRO_VER','PROCESOS - VER','Ver el módulo de Procesos.');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('REP_VER','REPORTING - VER','Ver el módulo de Reporting.');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_BAL_CARGAR','APROVISIONAMIENTO - BALANCENTE - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_CECO_CARGAR','APROVISIONAMIENTO - DRIVER CENTROS DE COSTOS - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_CECO_CREAR','APROVISIONAMIENTO - DRIVER CENTROS DE COSTOS - CREAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_CECO_EDITAR','APROVISIONAMIENTO - DRIVER CENTROS DE COSTOS - EDITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_CECO_ELIMINAR','APROVISIONAMIENTO - DRIVER CENTROS DE COSTOS - ELIMINAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_OBCO_CARGAR','APROVISIONAMIENTO - DRIVER OBJETOS DE COSTOS - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_OBCO_CREAR','APROVISIONAMIENTO - DRIVER OBJETOS DE COSTOS - CREAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_OBCO_EDITAR','APROVISIONAMIENTO - DRIVER OBJETOS DE COSTOS - EDITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('APR_DRI_OBCO_ELIMINAR','APROVISIONAMIENTO - DRIVER OBJETOS DE COSTOS - ELIMINAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_COPIAR','PARAMETRIZACIÓN - COPIAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_CAT_CREAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - CREAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_CAT_EDITAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - EDITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_CAT_ELIMINAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - ELIMINAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_CAT_CARGAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_ASO_AGREGAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - AGREGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_ASO_QUITAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - QUITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CTA_ASO_CARGAR','PARAMETRIZACIÓN - CUENTAS CONTABLES - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_CREAR','PARAMETRIZACIÓN - GRUPOS - CREAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_EDITAR','PARAMETRIZACIÓN - GRUPOS - EDITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_ELIMINAR','PARAMETRIZACIÓN - GRUPOS - ELIMINAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_CARGAR','PARAMETRIZACIÓN - GRUPOS - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_CTA_ASIGNAR','PARAMETRIZACIÓN - GRUPOS A CUENTAS CONTABLES - ASIGNAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_CTA_QUITAR','PARAMETRIZACIÓN - GRUPOS A CUENTAS CONTABLES - QUITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_GRU_CTA_CARGAR','PARAMETRIZACIÓN - GRUPOS A CUENTAS CONTABLES - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_CAT_CREAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - CREAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_CAT_EDITAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - EDITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_CAT_ELIMINAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - ELIMINAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_CAT_CARGAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_ASO_AGREGAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - AGREGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_ASO_QUITAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - QUITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_CECO_ASO_CARGAR','PARAMETRIZACIÓN - CENTROS DE COSTOS - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_CAT_CREAR','PARAMETRIZACIÓN - OFICINAS - CREAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_CAT_EDITAR','PARAMETRIZACIÓN - OFICINAS - EDITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_CAT_ELIMINAR','PARAMETRIZACIÓN - OFICINAS - ELIMINAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_CAT_CARGAR','PARAMETRIZACIÓN - OFICINAS - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_ASO_AGREGAR','PARAMETRIZACIÓN - OFICINAS - AGREGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_ASO_QUITAR','PARAMETRIZACIÓN - OFICINAS - QUITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_OFI_ASO_CARGAR','PARAMETRIZACIÓN - OFICINAS - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_CAT_CREAR','PARAMETRIZACIÓN - BANCAS - CREAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_CAT_EDITAR','PARAMETRIZACIÓN - BANCAS - EDITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_CAT_ELIMINAR','PARAMETRIZACIÓN - BANCAS - ELIMINAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_CAT_CARGAR','PARAMETRIZACIÓN - BANCAS - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_ASO_AGREGAR','PARAMETRIZACIÓN - BANCAS - AGREGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_ASO_QUITAR','PARAMETRIZACIÓN - BANCAS - QUITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_BAN_ASO_CARGAR','PARAMETRIZACIÓN - BANCAS - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_CAT_CREAR','PARAMETRIZACIÓN - PRODUCTOS - CATÁLOGO - CREAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_CAT_EDITAR','PARAMETRIZACIÓN - PRODUCTOS - CATÁLOGO - EDITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_CAT_ELIMINAR','PARAMETRIZACIÓN - PRODUCTOS - CATÁLOGO - ELIMINAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_CAT_CARGAR','PARAMETRIZACIÓN - PRODUCTOS - CATÁLOGO - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_ASO_AGREGAR','PARAMETRIZACIÓN - PRODUCTOS - ASOCIACIÓN - AGREGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_ASO_QUITAR','PARAMETRIZACIÓN - PRODUCTOS - ASOCIACIÓN - QUITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_PRO_ASO_CARGAR','PARAMETRIZACIÓN - PRODUCTOS - ASOCIACIÓN - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_DRI_ENT_ASIGNAR','PARAMETRIZACIÓN - DRIVER A ENTIDAD - ASIGNAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_DRI_ENT_QUITAR','PARAMETRIZACIÓN - DRIVER A ENTIDAD - QUITAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PAR_DRI_ENT_CARGAR','PARAMETRIZACIÓN - DRIVER A ENTIDAD - CARGAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('PRO_EJECUTAR','PROCESOS - EJECUTAR','');
INSERT INTO PACIFICO_DEV.seguridad_permisos(codigo,nombre,descripcion) VALUES('REP_DESCARGAR','REPORTING - DESCARGAR','');
COMMIT;
----------------------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.seguridad_roles (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(150) NOT NULL,
  descripcion VARCHAR2(150),
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT seguridad_roles_pk PRIMARY KEY(codigo)
);
INSERT INTO PACIFICO_DEV.seguridad_roles(codigo,nombre,descripcion) VALUES('ADM','Administrador','Rol con permisos de lectura, creación, edición y eliminación.');
INSERT INTO PACIFICO_DEV.seguridad_roles(codigo,nombre,descripcion) VALUES('USR','Usuario','Rol con permisos de lectura.');
COMMIT;
-------------------------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.seguridad_permiso_rol (
  permiso_codigo VARCHAR2(50) NOT NULL,
  rol_codigo VARCHAR2(10) NOT NULL,
  CONSTRAINT seguridad_permiso_rol_1_fk FOREIGN KEY(permiso_codigo) REFERENCES seguridad_permisos(codigo),
  CONSTRAINT seguridad_permiso_rol_2_fk FOREIGN KEY(rol_codigo) REFERENCES seguridad_roles(codigo)
);
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('INI_VER','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_VER','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_VER','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PRO_VER','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('REP_VER','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_BAL_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_CECO_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_CECO_CREAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_CECO_EDITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_CECO_ELIMINAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_OBCO_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_OBCO_CREAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_OBCO_EDITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_DRI_OBCO_ELIMINAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_COPIAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_CAT_CREAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_CAT_EDITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_CAT_ELIMINAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_CAT_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_ASO_AGREGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_ASO_QUITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CTA_ASO_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_CREAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_EDITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_ELIMINAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_CTA_ASIGNAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_CTA_QUITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_GRU_CTA_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_CAT_CREAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_CAT_EDITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_CAT_ELIMINAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_CAT_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_ASO_AGREGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_ASO_QUITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_CECO_ASO_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_CAT_CREAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_CAT_EDITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_CAT_ELIMINAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_CAT_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_ASO_AGREGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_ASO_QUITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_OFI_ASO_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_CAT_CREAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_CAT_EDITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_CAT_ELIMINAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_CAT_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_ASO_AGREGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_ASO_QUITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_BAN_ASO_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_CAT_CREAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_CAT_EDITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_CAT_ELIMINAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_CAT_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_ASO_AGREGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_ASO_QUITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_PRO_ASO_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_DRI_ENT_ASIGNAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_DRI_ENT_QUITAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_DRI_ENT_CARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PRO_EJECUTAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('REP_DESCARGAR','ADM');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('INI_VER','USR');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('APR_VER','USR');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PAR_VER','USR');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('PRO_VER','USR');
INSERT INTO PACIFICO_DEV.seguridad_permiso_rol(permiso_codigo,rol_codigo) VALUES('REP_VER','USR');
COMMIT;
-----------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.seguridad_usuarios (
  usuario VARCHAR2(100) NOT NULL,
  contrasenha VARCHAR2(200) NOT NULL,
  nombres VARCHAR2(100),
  apellidos VARCHAR2(100),
  rol_codigo VARCHAR2(10) NOT NULL,
  CONSTRAINT seguridad_usuarios_pk PRIMARY KEY (usuario),
  CONSTRAINT seguridad_usuarios_1_fk FOREIGN KEY (rol_codigo) REFERENCES seguridad_roles(codigo)
);
INSERT INTO PACIFICO_DEV.seguridad_usuarios(usuario,contrasenha,rol_codigo) VALUES('admin','secret','ADM');
INSERT INTO PACIFICO_DEV.seguridad_usuarios(usuario,contrasenha,rol_codigo) VALUES('user','secret','USR');
COMMIT;

-----------------------------------------------------------------------------------------------------------------------------------------
-------------- CUENTAS CONTABLES ---------------------------------------------------------
------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.plan_de_cuentas (
  codigo VARCHAR2(15) NOT NULL,
  nombre VARCHAR2(100) NOT NULL,
  esta_activo NUMBER(1) NULL,
  reparto_tipo NUMBER(1) NOT NULL,
  atribuible CHAR(2 BYTE) NOT NULL ,
  tipo CHAR(2 BYTE) NOT NULL ,
  clase CHAR(2 BYTE) NOT NULL ,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT plan_de_cuentas_pk PRIMARY KEY(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.plan_de_cuenta_lineas (
  plan_de_cuenta_codigo VARCHAR2(15) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  saldo NUMBER(35,8) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT plan_de_cuenta_lineas_pk PRIMARY KEY(plan_de_cuenta_codigo,periodo)
);


---------------------------------------------------------------------------------
-------------- PARTIDAS ---------------------------------------------------------
---------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.GRUPO_GASTOS (
  CODIGO VARCHAR2(8 BYTE) NOT NULL ,
  NOMBRE VARCHAR2(100 BYTE) NOT NULL ,
  DESCRIPCION VARCHAR2(100 BYTE),
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE
);

INSERT INTO PACIFICO_DEV.GRUPO_GASTOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('GP','GASTO DE PERSONAL','GASTO DE PERSONAL',NULL,NULL);
INSERT INTO PACIFICO_DEV.GRUPO_GASTOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('GT','GASTO DE TECNOLOGIA','GASTO DE TECNOLOGIA',NULL,NULL);
INSERT INTO PACIFICO_DEV.GRUPO_GASTOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('GO','GASTO DE OPERACIONES','GASTO DE OPERACIONES',NULL,NULL);
COMMIT;

 CREATE TABLE PACIFICO_DEV.PARTIDAS
    ( CODIGO VARCHAR2(6 BYTE) NOT NULL ,
  NOMBRE VARCHAR2(100 BYTE) NOT NULL ,
  REPARTO_TIPO NUMBER(1,0) NOT NULL ,
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE,
  GRUPO_GASTO CHAR(2 BYTE) NOT NULL
 );
 CREATE TABLE PACIFICO_DEV.PARTIDA_LINEAS
   (  PARTIDA_CODIGO VARCHAR2(6 BYTE) NOT NULL ,
  PERIODO NUMBER(6,0) NOT NULL ,
  SALDO NUMBER(35,8) NOT NULL ,
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE
 );
---------------------------------------------------------------------------------
-------------- PARTIDAS - CUENTA CONTABLE ---------------------------------------------------------
---------------------------------------------------------------------------------

CREATE TABLE PACIFICO_DEV.PARTIDA_CUENTA_CONTABLE
   (  PARTIDA_CODIGO VARCHAR2(6 BYTE) NOT NULL ,
  CUENTA_CONTABLE_CODIGO VARCHAR2(15 BYTE) NOT NULL ,
  PERIODO NUMBER(6,0),
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE,
  SALDO NUMBER(35,8) NOT NULL,
  ES_BOLSA CHAR(2) NOT NULL
);
---------------------------------------------------------------------------------
-------------- CENTROS ---------------------------------------------------------
---------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.centro_niveles (
  codigo VARCHAR2(8) NOT NULL,
  nombre VARCHAR2(100) NOT NULL,
  descripcion VARCHAR2(150) NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT centro_niveles_pk PRIMARY KEY(codigo)
);

INSERT INTO PACIFICO_DEV.CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('-1','-1','CENTROS DE COSTOS TIPO BOLSA.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('-','TODOS','CENTROS DE COSTOS DE TODOS LOS NIVELES.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('1','1','CENTROS DE COSTOS DE NIVEL 1.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('2','2','CENTROS DE COSTOS DE NIVEL 2.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('3','3','CENTROS DE COSTOS DE NIVEL 3.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('4','4','CENTROS DE COSTOS DE NIVEL 4.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('5','5','CENTROS DE COSTOS DE NIVEL 5.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('6','6','CENTROS DE COSTOS DE NIVEL 6.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_NIVELES(CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('0','0','CENTROS DE COSTOS DE NIVEL 0.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));

COMMIT;
------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.centro_tipos (
  codigo VARCHAR2(8) NOT NULL,
  nombre VARCHAR2(100) NOT NULL,
  descripcion VARCHAR2(150) NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT centro_tipos_pk PRIMARY KEY(codigo)
);

INSERT INTO PACIFICO_DEV.CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('-','TODOS','TODOS LOS GRUPOS.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('A','STAFF','CENTROS DE COSTOS CORRESPONDIENTES A LOS GASTOS DE STAFF.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('B','SOPORTE','CENTROS DE COSTOS CORRESPONDIENTES A LOS GASTOS DE SOPORTE.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('C','BOLSAS','CENTROS DE COSTOS  CORRESPONDIENTES A LAS BOLSAS.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('D','FICTICIO','CENTROS DE COSTOS DE APOYO FICTICIO.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('E','CANALES','CENTROS DE COSTOS OBJETO CANAL.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('F','LINEAS DE NEGOCIO','CENTROS DE COSTOS OBJETO LINEA DE NEGOCIO.',TO_DATE('01/01/18','DD/MM/RR'),TO_DATE('01/01/18','DD/MM/RR'));
INSERT INTO PACIFICO_DEV.CENTRO_TIPOS (CODIGO,NOMBRE,DESCRIPCION,FECHA_CREACION,FECHA_ACTUALIZACION) VALUES ('G', 'OFICINAS', 'Centros de costos correspondientes a los gastos de Oficina', TO_DATE('01/01/18', 'DD/MM/RR'), TO_DATE('01/01/18', 'DD/MM/RR'));
------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.centros (
  codigo VARCHAR2(8) NOT NULL,
  nombre VARCHAR2(100) NOT NULL,
  esta_activo NUMBER(1) NULL,
  nivel NUMBER(10) NOT NULL,
  centro_padre_codigo VARCHAR2(8) NULL,
  centro_tipo_codigo VARCHAR2(8) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  reparto_tipo NUMBER(1) NOT NULL,
  ATRIBUIBLE CHAR(2) NOT NULL,
  TIPO CHAR(2) NOT NULL,
  CLASE CHAR(2) NOT NULL,
  ES_BOLSA CHAR(2) NOT NULL,
  CONSTRAINT centros_pk PRIMARY KEY(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.centro_lineas (
  centro_codigo VARCHAR2(8) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  iteracion NUMBER(10) NOT NULL,
  saldo NUMBER(35,8) NOT NULL,
  entidad_origen_codigo VARCHAR2(10) NOT NULL,
  grupo_gasto CHAR(2),
  fecha_creacion DATE,
  fecha_actualizacion DATE
);

---------------------------------------------------------------------------------
-------------- CUENTA - PARTIDA - CENTRO ---------------------------------------------------------
---------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.CUENTA_PARTIDA_CENTRO
   (  CUENTA_CONTABLE_CODIGO VARCHAR2(15 BYTE) NOT NULL ,
  PARTIDA_CODIGO VARCHAR2(6 BYTE) NOT NULL ,
  CENTRO_CODIGO VARCHAR2(8 BYTE) NOT NULL ,
  PERIODO NUMBER(6,0) NOT NULL ,
  SALDO NUMBER(35,8) NOT NULL ,
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE,
   CONSTRAINT CUENTA_PARTIDA_CENTRO_PK PRIMARY KEY (CUENTA_CONTABLE_CODIGO, PARTIDA_CODIGO, CENTRO_CODIGO,PERIODO)
 );

---------------------------------------------------------------------------------
-------------- ENTIDAD TIPOS ---------------------------------------------------------
---------------------------------------------------------------------------------

CREATE TABLE PACIFICO_DEV.entidad_tipos (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(100) NOT NULL,
  descripcion VARCHAR2(150) NULL,
  CONSTRAINT entidad_tipos_pk PRIMARY KEY(codigo)
);

INSERT INTO PACIFICO_DEV.entidad_tipos(codigo,nombre) VALUES('-','Todos');
INSERT INTO PACIFICO_DEV.entidad_tipos(codigo,nombre) VALUES('CTA','Cuenta Contable');
INSERT INTO PACIFICO_DEV.entidad_tipos(codigo,nombre) VALUES('PART','Partida');
INSERT INTO PACIFICO_DEV.entidad_tipos(codigo,nombre) VALUES('CECO','Centro de Costos');
INSERT INTO PACIFICO_DEV.entidad_tipos(codigo,nombre) VALUES('PRO','Producto');
INSERT INTO PACIFICO_DEV.entidad_tipos(codigo,nombre) VALUES ('SCA', 'Subcanal');
COMMIT;

---------------------------------------------------------------------------------
-------------- OBJETOS DE COSTO  ---------------------------------------------------------
---------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.productos (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(100) NOT NULL,
  esta_activo NUMBER(1) NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT productos_pk PRIMARY KEY(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.producto_lineas (
  producto_codigo VARCHAR2(10) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT producto_lineas_pk PRIMARY KEY(producto_codigo,periodo)
);
------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.producto_grupos (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(100) NOT NULL,
  nivel NUMBER(2) NOT NULL,
  esta_activo NUMBER(1) NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT producto_grupos_pk PRIMARY KEY(codigo)
);
------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.SUBCANALS
( CODIGO VARCHAR2(10 BYTE) NOT NULL ,
  NOMBRE VARCHAR2(100 BYTE) NOT NULL ,
  ESTA_ACTIVO NUMBER(1,0),
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE
);
------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.SUBCANAL_LINEAS
( SUBCANAL_CODIGO VARCHAR2(10 BYTE) NOT NULL,
  PERIODO NUMBER(6,0) NOT NULL ,
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE
);
------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.SUBCANAL_GRUPOS
  ( CODIGO VARCHAR2(10 BYTE) NOT NULL ,
 NOMBRE VARCHAR2(100 BYTE) NOT NULL ,
 NIVEL NUMBER(2,0) NOT NULL ,
 ESTA_ACTIVO NUMBER(1,0),
 FECHA_CREACION DATE,
 FECHA_ACTUALIZACION DATE,
 CONSTRAINT producto_grupos_pk PRIMARY KEY(CODIGO)
);
------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.jerarquia (
  periodo NUMBER(6) NOT NULL,
  entidad_codigo VARCHAR2(10) NOT NULL,
  entidad_tipo VARCHAR2(10) NOT NULL,
  nivel NUMBER(2) NOT NULL,
  entidad_padre_codigo VARCHAR2(10) NOT NULL,
  CONSTRAINT jerarquia_pk PRIMARY KEY(periodo,entidad_codigo,entidad_tipo,nivel,entidad_padre_codigo)
);
---------------------------------------------------------------------------------
-------------- DRIVER   ---------------------------------------------------------
---------------------------------------------------------------------------------

CREATE TABLE PACIFICO_DEV.driver_tipos (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(100) NOT NULL,
  descripcion VARCHAR2(200) NULL,
  CONSTRAINT driver_tipo_pk PRIMARY KEY (codigo)
);
INSERT INTO PACIFICO_DEV.driver_tipos(codigo,nombre,descripcion) VALUES('CECO','Centros de Costos',null);
INSERT INTO PACIFICO_DEV.driver_tipos(codigo,nombre,descripcion) VALUES('OBCO','Objetos de Costos',null);
COMMIT;
------------------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.drivers (
  codigo VARCHAR2(10) NOT NULL,
  nombre VARCHAR2(100) NULL,
  driver_tipo_codigo VARCHAR2(10),
  reparto_tipo NUMBER(1) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT driver_pk PRIMARY KEY (codigo),
  CONSTRAINT driver_1_fk FOREIGN KEY(driver_tipo_codigo) REFERENCES driver_tipos(codigo)
);

-------------- DRIVER - CENTRO DE COSTOS   ---------------------------------------------------------
CREATE TABLE PACIFICO_DEV.driver_lineas (
  driver_codigo VARCHAR2(10) NOT NULL,
  entidad_destino_codigo VARCHAR2(10) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  porcentaje NUMBER(10,4) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT driver_lineas_pk PRIMARY KEY(driver_codigo,entidad_destino_codigo,periodo),
  CONSTRAINT driver_lineas_1_fk FOREIGN KEY(driver_codigo) REFERENCES drivers(codigo)
);
-------------- DRIVER - OBJETOS DE COSTOS   ---------------------------------------------------------
CREATE TABLE PACIFICO_DEV.DRIVER_OBJETO_LINEAS
   (  DRIVER_CODIGO VARCHAR2(10 BYTE) NOT NULL,
  PRODUCTO_CODIGO VARCHAR2(10 BYTE) NOT NULL,
  SUBCANAL_CODIGO VARCHAR2(10 BYTE) NOT NULL,
  PERIODO NUMBER(6,0) NOT NULL,
  PORCENTAJE NUMBER(10,4) NOT NULL,
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE
);

CREATE TABLE PACIFICO_DEV.CARGAR_HOJA_DRIVER (
  EXCEL_FILA NUMBER(6) NOT NULL,
  DRIVER_CODIGO VARCHAR2(10) NOT NULL,
  CODIGO_1 VARCHAR2(10) NOT NULL,
  CODIGO_2 VARCHAR2(10) NULL,
  CODIGO_3 VARCHAR2(10) NULL,
  PORCENTAJE NUMBER(10,4) NOT NULL
);

---------------------------------------------------------------------------------
-------------- ASIGNACION DRIVER A CENTROS  ---------------------------------------------------------
---------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.BOLSA_DRIVER
   (  DRIVER_CODIGO VARCHAR2(10 BYTE) NOT NULL ,
  CUENTA_CONTABLE_CODIGO VARCHAR2(15 BYTE) NOT NULL ,
  PARTIDA_CODIGO VARCHAR2(6 BYTE) NOT NULL ,
  CENTRO_CODIGO VARCHAR2(8 BYTE) NOT NULL ,
  PERIODO NUMBER(6,0) NOT NULL ,
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE
);

CREATE TABLE PACIFICO_DEV.entidad_origen_driver (
  entidad_origen_codigo VARCHAR2(10) NOT NULL,
  driver_codigo VARCHAR2(10) NOT NULL,
  periodo NUMBER(6) NOT NULL,
  fecha_creacion DATE,
  fecha_actualizacion DATE,
  CONSTRAINT entidad_origen_driver_pk PRIMARY KEY(entidad_origen_codigo,driver_codigo,periodo)
);

CREATE TABLE PACIFICO_DEV.OBJETO_DRIVER
 (
    CENTRO_CODIGO VARCHAR2(8) NOT NULL,
    GRUPO_GASTO VARCHAR2(2) NOT NULL,
    DRIVER_CODIGO VARCHAR2(10) NOT NULL,
    PERIODO NUMBER(6, 0) NOT NULL,
    FECHA_CREACION DATE,
    FECHA_ACTUALIZACION DATE
 );
---------------------------------------------------------------------------------
-------------- EJECUCION  ---------------------------------------------------------
---------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.ejecuciones (
  periodo NUMBER(6) NOT NULL,
  fase NUMBER(10) NOT NULL,
  reparto_tipo NUMBER(1) NOT NULL,
  fecha_ini DATE NOT NULL,
  fecha_fin DATE NULL
);

CREATE TABLE PACIFICO_DEV.OBJETO_LINEAS
(
  SUBCANAL_CODIGO VARCHAR2(10 BYTE) NOT NULL,
  PRODUCTO_CODIGO VARCHAR2(10 BYTE) NOT NULL,
  PERIODO NUMBER(6, 0) NOT NULL,
  ENTIDAD_ORIGEN_CODIGO VARCHAR2(10 BYTE) NOT NULL,
  SALDO NUMBER(35, 8) NOT NULL,
  REPARTO_TIPO NUMBER(1, 0) NOT NULL,
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE,
  GRUPO_GASTO CHAR(2 BYTE) NOT NULL,
  DRIVER_CODIGO VARCHAR2(10 BYTE) NOT NULL
);
---------------------------------------------------------------------------------
-------------- TRAZA  ---------------------------------------------------------
---------------------------------------------------------------------------------
CREATE TABLE PACIFICO_DEV.TRAZA_CASCADA
(
  CENTRO_ORIGEN_CODIGO VARCHAR2(8 BYTE) NOT NULL,
  CENTRO_DESTINO_CODIGO VARCHAR2(8 BYTE) NOT NULL,
  PORCENTAJE NUMBER(35, 10) NOT NULL,
  PERIODO NUMBER(6, 0) NOT NULL,
  CENTRO_ORIGEN_NIVEL NUMBER(10, 0),
  CENTRO_DESTINO_NIVEL NUMBER(10, 0),
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE,
  PRODUCTO_CODIGO VARCHAR2(10 BYTE),
  SUBCANAL_CODIGO VARCHAR2(10 BYTE),
  GRUPO_GASTO VARCHAR2(2 BYTE)
);

CREATE TABLE PACIFICO_DEV.TRAZA
(
  CENTRO_ORIGEN_CODIGO VARCHAR2(8 BYTE) NOT NULL,
  PRODUCTO_CODIGO VARCHAR2(10 BYTE),
  SUBCANAL_CODIGO VARCHAR2(10 BYTE),
  GRUPO_GASTO VARCHAR2(2 BYTE),
  PORCENTAJE NUMBER(35, 10) NOT NULL,
  PERIODO NUMBER(6, 0) NOT NULL,
  FECHA_CREACION DATE,
  FECHA_ACTUALIZACION DATE
);
